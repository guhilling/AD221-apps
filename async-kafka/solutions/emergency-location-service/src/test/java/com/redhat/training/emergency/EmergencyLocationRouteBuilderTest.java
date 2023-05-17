package com.redhat.training.emergency;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import javax.inject.Inject;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.idempotent.kafka.KafkaIdempotentRepository;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.redhat.training.emergency.route.EmergencyLocationRouteBuilder;

@QuarkusTest
class EmergencyLocationRouteBuilderTest extends CamelQuarkusTestSupport {

	@Inject
	protected ConsumerTemplate consumerTemplate;

    @ConfigProperty(name = "camel.component.kafka.brokers")
    String brokers;

	@EndpointInject("mock:file:logger")
	protected MockEndpoint loggerEndpoint;

	@Override
	protected RoutesBuilder createRouteBuilder() {
		return new EmergencyLocationRouteBuilder();
	}


    KafkaIdempotentRepository kafkaIdempotentRepository() {
        return new KafkaIdempotentRepository("locations", brokers);
    }


	@Test
	void testEmergencyLocationRoute() throws Exception {
		loggerEndpoint.expectedBodiesReceived(
			"{ \"latitude\" : 52.2782697, \"longitude\" : 8.025496 }",
			"{ \"latitude\" : 28.1656499, \"longitude\" : -14.2227666 }");
		loggerEndpoint.assertIsSatisfied(1000);
	}

	@Test
	void testEmergencyLocationRouteTwo() throws Exception {
		loggerEndpoint.expectedMessageCount(2);
		loggerEndpoint.assertIsSatisfied(1000);
	}

	@BeforeEach
	void doAdvice() throws Exception {
		AdviceWith.adviceWith(context(), "emergency-location-route",
							  EmergencyLocationRouteBuilderTest::adviceRoute);
	}

	private static void adviceRoute(AdviceWithRouteBuilder route) {
		route.interceptSendToEndpoint("direct:logger")
			.skipSendToOriginalEndpoint()
			.to("mock:file:logger");
	}

}
