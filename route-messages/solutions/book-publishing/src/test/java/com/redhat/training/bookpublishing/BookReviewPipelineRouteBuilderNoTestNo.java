package com.redhat.training.bookpublishing;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//@QuarkusTest
class BookReviewPipelineRouteBuilderNoTestNo {

	@Produce("direct:ready-for-printing")
	private ProducerTemplate template;

	@Inject
	private CamelContext context;

	@EndpointInject(uri = "mock:file:editor")
	MockEndpoint fileMockEditor;

	@EndpointInject(uri = "mock:file:graphic_designer")
	MockEndpoint fileMockGraphicDesigner;

	@Before
	public void setUp() throws Exception {
		mockRouteEndpoints();
		context.start();
	}

	@After
	public void tearDown() throws Exception {
		context.stop();
	}

	//@Test
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

	@Test
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
