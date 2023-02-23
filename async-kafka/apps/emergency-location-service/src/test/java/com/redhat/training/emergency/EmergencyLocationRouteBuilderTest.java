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
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.apache.camel.quarkus.test.support.kafka.KafkaTestResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.redhat.training.emergency.route.EmergencyLocationRouteBuilder;

@QuarkusTest
@QuarkusTestResource(KafkaTestResource.class)
class EmergencyLocationRouteBuilderTest extends CamelQuarkusTestSupport {

	@Inject
	protected ConsumerTemplate consumerTemplate;

	@EndpointInject("mock:file:logger")
	protected MockEndpoint loggerEndpoint;

	//@Override
	//protected RoutesBuilder createRouteBuilder() {
	//	return new EmergencyLocationRouteBuilder();
	//}

	@Test
	void emptyTest() throws Exception {
		loggerEndpoint.expectedMessageCount(1);
		loggerEndpoint.assertIsSatisfied(1000);
	}

	@Test
	void testEmergencyLocationRoute() {
		assertErrorNotOccured();
	}

	//@BeforeEach
	void doAdvice() throws Exception {
		AdviceWith.adviceWith(context(), "emergency-location-route",
							  EmergencyLocationRouteBuilderTest::adviceRoute);
	}

	private static void adviceRoute(AdviceWithRouteBuilder route) {
		route.interceptSendToEndpoint("direct:logger")
			.skipSendToOriginalEndpoint()
			.to("mock:file:logger");
	}

	private void assertErrorNotOccured() {
		String body = consumerTemplate.receive("direct:output").getIn().getBody(String.class);
		assertNotEquals("errorOccured", body); 
	}
}
