package com.redhat.training.bookpublishing;

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

import com.redhat.training.bookpublishing.route.BookPrintingPipelineRouteBuilder;

@QuarkusTest
class BookPrintingPipelineRouteBuilderTest extends CamelQuarkusTestSupport {

	@Produce("direct:ready-for-printing")
	protected ProducerTemplate template;

	@EndpointInject("mock:file:technical")
	protected MockEndpoint fileMockTechnical;

	@EndpointInject("mock:file:novel")
	protected MockEndpoint fileMockNovel;

	@Override
	protected RoutesBuilder createRouteBuilder() {
		return new BookPrintingPipelineRouteBuilder();
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

	@BeforeEach
	void doAdvice() throws Exception {
		AdviceWith.adviceWith(context(), "book-printing-pipeline", BookPrintingPipelineRouteBuilderTest::adviceRoute);
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

	private String technicalContent() {
		return "<book><bookinfo><productname>technical</productname></bookinfo></book>";
	}

	private String novelContent() {
		return "<book><bookinfo><productname>novel</productname></bookinfo></book>";
	}
}
