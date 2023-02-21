package com.redhat.training.emergency;

import io.agroal.api.AgroalDataSource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.apache.camel.quarkus.test.support.kafka.KafkaTestResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.training.emergency.route.EmergencyLocationRouteBuilder;

@QuarkusTest
@QuarkusTestResource(KafkaTestResource.class)
class EmergencyLocationRouteBuilderTest extends CamelQuarkusTestSupport {

	@Inject
	protected ConsumerTemplate consumerTemplate;

	@Inject
	protected CamelContext context;

	@Override
	protected RoutesBuilder createRouteBuilder() {
		return new EmergencyLocationRouteBuilder();
	}

	@Test
	void emptyTest() {

	}

	@Test
	void testEmergencyLocationRoute() {
		assertErrorNotOccured();
	}

	@BeforeEach
	void doAdvice() throws Exception {
		AdviceWith.adviceWith(context(), "emergency-location-route",
							  EmergencyLocationRouteBuilderTest::adviceRoute);
	}

	private static void adviceRoute(AdviceWithRouteBuilder route) {
		route.replaceFromWith("direct:ready-for-printing");
		route.interceptSendToEndpoint("file://data/printing-services/technical")
			.skipSendToOriginalEndpoint()
			.to("mock:file:technical");
		route.interceptSendToEndpoint("file://data/printing-services/novel")
			.skipSendToOriginalEndpoint()
			.to("mock:file:novel");
	}

	private void assertErrorNotOccured() {
		String body = consumerTemplate.receive("direct:output").getIn().getBody(String.class);
		assertNotEquals("errorOccured", body); 
	}
}
