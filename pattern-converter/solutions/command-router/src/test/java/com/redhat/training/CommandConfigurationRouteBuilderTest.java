package com.redhat.training;

import io.quarkus.test.junit.QuarkusTest;

import javax.inject.Inject;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.redhat.training.route.CommandConfigurationRouteBuilder;

@QuarkusTest
class CommandConfigurationRouteBuilderTest extends CamelQuarkusTestSupport {

	@Inject
	protected ConsumerTemplate consumerTemplate;

	@Override
	protected RoutesBuilder createRouteBuilder() {
		return new CommandConfigurationRouteBuilder();
	}

	@Test
	void testRoute() {
		Assertions.assertEquals(200, consumerTemplate.receive("direct:output").getIn().getBody());
	}

	@BeforeEach
	void doAdvice() throws Exception {
		AdviceWith.adviceWith(context(), "air-purifier-configuration-route", CommandConfigurationRouteBuilderTest::adviceRoute);
	}

	private static void adviceRoute(AdviceWithRouteBuilder route) {
		route.interceptSendToEndpoint("direct:logReturnCode")
//			.skipSendToOriginalEndpoint()
			.to("direct:output");
	}

}
