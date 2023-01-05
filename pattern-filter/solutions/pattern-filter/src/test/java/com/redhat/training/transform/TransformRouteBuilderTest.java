package com.redhat.training.transform;

import io.quarkus.test.junit.QuarkusTest;

import javax.inject.Inject;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.Test;

import com.redhat.training.model.Order;

@QuarkusTest
class TransformRouteBuilderTest extends CamelQuarkusTestSupport {

    @Inject
    protected ProducerTemplate producerTemplate;

    @EndpointInject("mock:fulfillmentSystem")
    protected MockEndpoint mockFulfillmentSystem;

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new TransformRouteBuilder();
    }

    @Test
    void testLogOrderRoute() throws InterruptedException {
        String exectedJson = "{\"orderItems\":[{\"extPrice\":\"110\",\"id\":\"2\"},{\"extPrice\":\"110\",\"id\":\"3\"}],\"customer\":{\"admin\":\"false\",\"email\":\"tanderson@email.com\",\"firstName\":\"Tony\",\"lastName\":\"Anderson\",\"password\":\"password\",\"username\":\"tanderson\"},\"delivered\":\"true\",\"id\":\"2\"}";

        mockFulfillmentSystem.expectedBodyReceived();

        // Builds sample test data
        OrderProducer orderProducer  = new OrderProducer();
        Order testOrder = orderProducer.createOrder();

        // Sends a message to the start component
        producerTemplate.sendBody("activemq:queue:orderInput", testOrder);

        // Verifies that the mock component received 1 message
        mockFulfillmentSystem.assertIsSatisfied();
    }

}
