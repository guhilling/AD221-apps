package com.redhat.training.messaging;

import io.quarkus.test.junit.QuarkusTest;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.Test;

@QuarkusTest
class OrderLogRouteBuilderTest extends CamelQuarkusTestSupport {

    @Produce("direct:log_orders")
    protected ProducerTemplate producerTemplate;

    @EndpointInject("mock:fulfillmentSystem")
    protected MockEndpoint mockOrderLog;

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new OrderLogRouteBuilder();
    }

    @Test
    void testLogOrderRoute() throws Exception {
        // Sets an assertion
        mockOrderLog.expectedMessageCount(1);

        // Sends a message to the start component
        producerTemplate.sendBody(null);

        // Verifies that the mock component received 1 message
        mockOrderLog.assertIsSatisfied();
    }

}