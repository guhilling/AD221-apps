package com.redhat.training.bookpublishing;

import io.quarkus.test.junit.QuarkusTest;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
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

import com.redhat.training.bookpublishing.route.BookReviewPipelineRouteBuilder;

@QuarkusTest
class BookReviewPipelineRouteBuilderTest extends CamelQuarkusTestSupport {

	@Produce("direct:manuscripts")
	protected ProducerTemplate template;

	@Inject
	protected CamelContext context;

	@EndpointInject("mock:file:editor")
	protected MockEndpoint fileMockEditor;

	@EndpointInject("mock:file:graphic_designer")
	protected MockEndpoint fileMockGraphicDesigner;

	@Override
	protected RoutesBuilder createRouteBuilder() {
		return new BookReviewPipelineRouteBuilder();
	}

	@Test
	void technicalBookIsDeliveredToEditorAndGraphicalDesigner() throws Exception {
		fileMockEditor.expectedMessageCount(1);
		fileMockGraphicDesigner.expectedMessageCount(1);

		template.sendBody(
			"direct:manuscripts",
			technicalContent()
		);

		fileMockEditor.assertIsSatisfied();
		fileMockGraphicDesigner.assertIsSatisfied();
	}

	@Test
	void novelBookIsDeliveredToEditor() throws Exception {
		fileMockEditor.expectedMessageCount(1);
		fileMockGraphicDesigner.expectedMessageCount(0);

		template.sendBody(
				"direct:manuscripts",
				novelContent()
		);

		fileMockEditor.assertIsSatisfied();
		fileMockGraphicDesigner.assertIsSatisfied();
	}

	@Test
	void wrongBookFormatIsNotDelivered() throws Exception {
		fileMockEditor.expectedMessageCount(0);
		fileMockGraphicDesigner.expectedMessageCount(0);

		template.sendBody(
				"direct:manuscripts",
				wrongContent()
		);

		fileMockEditor.assertIsSatisfied();
		fileMockGraphicDesigner.assertIsSatisfied();
	}

	@BeforeEach
	void doAdvice() throws Exception {
		AdviceWith.adviceWith(context(), "book-review-pipeline",
							  BookReviewPipelineRouteBuilderTest::adviceRoute);
	}

	private static void adviceRoute(AdviceWithRouteBuilder route) {
		route.replaceFromWith( "direct:manuscripts" );

		route.interceptSendToEndpoint("file://data/pipeline/editor")
			.skipSendToOriginalEndpoint()
			.to("mock:file:editor");

		route.interceptSendToEndpoint("file://data/pipeline/graphic-designer")
			.skipSendToOriginalEndpoint()
			.to("mock:file:graphic_designer");
	}

	private String technicalContent() {
		return "<book><bookinfo><productname>technical</productname></bookinfo></book>";
	}

	private String novelContent() {
		return "<book><bookinfo><productname>novel</productname></bookinfo></book>";
	}

	private String wrongContent() {
		return "<book><bookinfo><productname>wrong_type</productname></bookinfo></book>";
	}
}
