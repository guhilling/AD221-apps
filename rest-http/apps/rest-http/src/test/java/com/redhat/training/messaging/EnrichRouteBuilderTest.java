package com.redhat.training.messaging;

import io.quarkus.test.junit.QuarkusTest;

import javax.inject.Inject;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.Test;

@QuarkusTest
class EnrichRouteBuilderTest extends CamelQuarkusTestSupport {

    @EndpointInject("mock:fulfillmentSystem")
    protected MockEndpoint mockFulfillment;

    @Inject
    protected ProducerTemplate producerTemplate;

    @Override
    protected RoutesBuilder[] createRouteBuilders() {
        return new RoutesBuilder[]{
        new SoapRouteBuilder(),
        new EnrichRouteBuilder(),
        new OrderLogRouteBuilder()
        };
    }

    @Test
    void testEnrichRoute() throws Exception {

	    mockFulfillment.expectedMessageCount(2);

        String testOrder = "{\"ID\":5,\"Discount\":0.012,\"Delivered\":false,\"Desc\":\"Test Order 1\",\"Name\":\"customer-a\"}";

        // Sends messages to the start component
        producerTemplate.sendBody("direct:enrich", testOrder);

        // Verifies that a message received
	    mockFulfillment.assertIsSatisfied();
    }

}