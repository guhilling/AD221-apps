package com.redhat.training.bookpublishing;

import io.quarkus.test.junit.QuarkusTest;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@QuarkusTest
//@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class BookReviewPipelineRouteBuilderTest {

	@Produce("direct:manuscripts")
	protected ProducerTemplate template;

	@Inject
	protected CamelContext context;

	@EndpointInject("mock:file:editor")
	protected MockEndpoint fileMockEditor;

	@EndpointInject("mock:file:graphic_designer")
	protected MockEndpoint fileMockGraphicDesigner;

	@Test
	public void technicalBookIsDeliveredToEditorAndGraphicalDesigner() throws Exception {
		fileMockEditor.expectedMessageCount(1);
		fileMockGraphicDesigner.expectedMessageCount(1);

		template.sendBody(
			"direct:manuscripts",
			technicalContent()
		);

		fileMockEditor.assertIsSatisfied();
		fileMockGraphicDesigner.assertIsSatisfied();
	}

	//@Test
	public void novelBookIsDeliveredToEditor() throws Exception {
		fileMockEditor.expectedMessageCount(1);
		fileMockGraphicDesigner.expectedMessageCount(0);

		template.sendBody(
				"direct:manuscripts",
				novelContent()
		);

		fileMockEditor.assertIsSatisfied();
		fileMockGraphicDesigner.assertIsSatisfied();
	}

	//@Test
	public void wrongBookFormatIsNotDelivered() throws Exception {
		fileMockEditor.expectedMessageCount(0);
		fileMockGraphicDesigner.expectedMessageCount(0);

		template.sendBody(
				"direct:manuscripts",
				wrongContent()
		);

		fileMockEditor.assertIsSatisfied();
		fileMockGraphicDesigner.assertIsSatisfied();
	}

	private void mockRouteEndpoints() throws Exception {
/*
		context.getRouteDefinition("book-review-pipeline")
		    .adviceWith(
			    context,
				new AdviceWithRouteBuilder() {
					@Override
					public void configure() {
						replaceFromWith( "direct:manuscripts" );

						interceptSendToEndpoint("file://data/pipeline/editor")
						    .skipSendToOriginalEndpoint()
							.to("mock:file:editor");

						interceptSendToEndpoint("file://data/pipeline/graphic-designer")
							.skipSendToOriginalEndpoint()
							.to("mock:file:graphic_designer");
					}
				}
			);
*/
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
