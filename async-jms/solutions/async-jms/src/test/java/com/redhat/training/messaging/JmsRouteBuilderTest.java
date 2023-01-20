package com.redhat.training.messaging;

import io.quarkus.test.junit.QuarkusTest;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.redhat.training.model.Order;


@QuarkusTest
class JmsRouteBuilderTest extends CamelQuarkusTestSupport {

    private static final String MOCK_RESULT_LOG = "mock:result_log";
    private static final String MOCK_RESULT_AMQP = "mock:result_amqp";

    @Produce("jms:queue:jms_order_input")
    protected ProducerTemplate producerTemplate;

    @EndpointInject(MOCK_RESULT_LOG)
    protected MockEndpoint resultLogEndpoint;

    @EndpointInject(MOCK_RESULT_AMQP)
    protected MockEndpoint resultAMQPEndpoint;

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new JmsRouteBuilder();
    }

    @Test
    void testJmsOrderRoute() throws Exception {
        resultLogEndpoint.expectedMessageCount(1);
        resultAMQPEndpoint.expectedMessageCount(1);

        OrderProducer orderProducer  = new OrderProducer();

        Order testOrder = orderProducer.createOrder();
        Order testOrder2 = orderProducer.createOrder();

        // Sends messages to the start component
        producerTemplate.sendBody(testOrder);
        producerTemplate.sendBody(testOrder2);

        // Verifies that a message received
	    resultLogEndpoint.assertIsSatisfied();
        resultAMQPEndpoint.assertIsSatisfied();
    }

    @BeforeEach
    void doAdvice() throws Exception {
        AdviceWith.adviceWith(context(), JmsRouteBuilder.ROUTE_NAME,
                              JmsRouteBuilderTest::adviceRoute);
    }

    private static void adviceRoute(AdviceWithRouteBuilder route) {
        route.interceptSendToEndpoint("direct:log_orders")
            .skipSendToOriginalEndpoint()
            .to(MOCK_RESULT_LOG);
        route.interceptSendToEndpoint("amqp:queue:amqp_order_input")
            .skipSendToOriginalEndpoint()
            .to(MOCK_RESULT_AMQP);
    }
}
