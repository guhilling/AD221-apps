package com.redhat.training.payments.route;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.ResultSet;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PaymentRouteBuilderTest extends CamelQuarkusTestSupport {

    @Inject
    protected ProducerTemplate template;

    @Inject
    protected EntityManager entityManager;

    @EndpointInject("mock:jms:error_dead_letter")
    MockEndpoint jmsMockErrorDeadLetter;

    @EndpointInject("mock:jms:queue")
    MockEndpoint jmsMockQueue;

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new PaymentRouteBuilder();
    }

    @Test
    void routeSendsMessageToCorrectEndpoints () throws Exception {
        jmsMockQueue.expectedMessageCount(1);
        jmsMockErrorDeadLetter.expectedMessageCount(0);

        // Happy path
        template.sendBody(
                "direct:payments",
                validContent()
        );

        // Assert messages in the database
        assertEquals(1, totalRecordsInDatabase());

        // Assert messages in the queue
        jmsMockQueue.assertIsSatisfied();

        // Assert messages in the dead letter queue
        jmsMockErrorDeadLetter.assertIsSatisfied();
    }

    @Disabled
    @Test
    void illegalStateExceptionCapturedByDeadLetter () throws Exception {
        jmsMockQueue.expectedMessageCount(0);
        jmsMockErrorDeadLetter.expectedMessageCount(1);

        // Throws IllegalStateException
        template.sendBody(
                "direct:payments",
                invalidOrderId()
        );

        // Assert ZERO messages in the database because of the rollback
        assertEquals(0, totalRecordsInDatabase());

        // Assert ZERO messages in the queue
        jmsMockQueue.assertIsSatisfied();

        // Assert ONE message in the dead letter queue
        jmsMockErrorDeadLetter.assertIsSatisfied();
    }

    @Disabled
    @Test
    void invalidEmailExceptionCapturedByDeadLetter () throws Exception {
        jmsMockQueue.expectedMessageCount(0);
        jmsMockErrorDeadLetter.expectedMessageCount(1);

        // Throws InvalidEmailException
        template.sendBody(
                "direct:payments",
                invalidEmail()
        );

        // Assert ZERO messages in the database because of the rollback
        assertEquals(0, totalRecordsInDatabase());

        // Assert ZERO messages in the queue
        jmsMockQueue.assertIsSatisfied();

        // Assert ONE message in the dead letter queue
        jmsMockErrorDeadLetter.assertIsSatisfied();
    }

    @BeforeEach
    void doAdvice() throws Exception {
        AdviceWith.adviceWith(context(), "payments-process",
                              PaymentRouteBuilderTest::adviceRoute);
    }

    private static void adviceRoute(AdviceWithRouteBuilder route) {
        route.replaceFromWith("direct:payments");
        // Notifications queue
        route.interceptSendToEndpoint("jms:queue:payment-notifications")
            .skipSendToOriginalEndpoint()
            .to("mock:jms:queue");

        // Dead Letter
        route.interceptSendToEndpoint("jms:queue:dead-letter")
            .skipSendToOriginalEndpoint()
            .to("mock:jms:error_dead_letter");
    }

    private int totalRecordsInDatabase() {
        Session session = (Session)entityManager.getDelegate();
        return session.doReturningWork(connection -> {
            ResultSet result = connection.createStatement().executeQuery("select count(*) from payments");
            result.next();
            int count = result.getInt(1);
            return count;
        });
    }

    private String validContent() {
        return "<payment><user_id>1</user_id><order_id>100</order_id><amount>100.00</amount><currency>EUR</currency><email>user@example.com</email></payment>";
    }

    private String invalidOrderId () {
        return "<payment><user_id>3</user_id><order_id>-1</order_id><amount>300.00</amount><currency>EUR</currency><email>fail@example.com</email></payment>";
    }

    private String invalidEmail() {
        return "<payment><user_id>2</user_id><order_id>200</order_id><amount>200.00</amount><currency>EUR</currency><email></email></payment>";
    }
}
