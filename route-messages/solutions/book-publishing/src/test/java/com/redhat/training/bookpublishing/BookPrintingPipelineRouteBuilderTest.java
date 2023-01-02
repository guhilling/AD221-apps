package com.redhat.training.bookpublishing;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.callback.QuarkusTestMethodContext;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.Configuration;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.redhat.training.bookpublishing.route.BookPrintingPipelineRouteBuilder;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class BookPrintingPipelineRouteBuilderTest extends CamelQuarkusTestSupport {

	@Inject
	protected CamelContext context;

	@Produce("direct:ready-for-printing")
	protected ProducerTemplate template;

	@EndpointInject("mock:file:technical")
	protected MockEndpoint fileMockTechnical;

	@EndpointInject("mock:file:novel")
	protected MockEndpoint fileMockNovel;

	@Configuration
	public static class TestConfig {
		@Produces
		RoutesBuilder route() {
			return new BookPrintingPipelineRouteBuilder();
		}
	}

	@Override
	protected void doBeforeEach(QuarkusTestMethodContext context) throws Exception {
		AdviceWith.adviceWith(this.context, "book-printing-pipeline",
							  BookPrintingPipelineRouteBuilderTest::enhanceRoute);
	}

	@Test
	void technicalBookIsDeliveredToTechnicalDirectory() throws Exception {
		fileMockTechnical.expectedMessageCount(1);
		fileMockNovel.expectedMessageCount(0);

		template.sendBody(
			"direct:ready-for-printing",
			technicalContent()
		);

		fileMockTechnical.assertIsSatisfied();
		fileMockNovel.assertIsSatisfied();
	}

	@Test
	void novelBookIsDeliveredToNovelDirectory() throws Exception {
		fileMockTechnical.expectedMessageCount(0);
		fileMockNovel.expectedMessageCount(1);

		template.sendBody(
				"direct:ready-for-printing",
				novelContent()
		);

		fileMockTechnical.assertIsSatisfied();
		fileMockNovel.assertIsSatisfied();
	}

	private static void enhanceRoute(AdviceWithRouteBuilder route) {
		route.replaceFromWith("direct:ready-for-printing");

		route.interceptSendToEndpoint("file://data/printing-services/technical")
			.skipSendToOriginalEndpoint()
			.to("mock:file:technical");

		route.interceptSendToEndpoint("file://data/printing-services/novel")
			.skipSendToOriginalEndpoint()
			.to("mock:file:novel");
	}

	private String technicalContent() {
		return "<book><bookinfo><productname>technical</productname></bookinfo></book>";
	}

	private String novelContent() {
		return "<book><bookinfo><productname>novel</productname></bookinfo></book>";
	}
}
