package com.redhat.training.processingorders;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.callback.QuarkusTestMethodContext;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.Configuration;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.language.bean.Bean;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class FtpToFileRouteBuilderTest extends CamelQuarkusTestSupport {

	@Produce("direct:ftp")
	protected ProducerTemplate template;

	@Inject
	protected CamelContext context;

	@EndpointInject( "mock:file:customer_requests" )
	protected MockEndpoint fileMock;

	@Configuration
	public static class TestConfig {
		@Produces
		RoutesBuilder route() {
			return new FtpToFileRouteBuilder();
		}
	}
	@Override
	protected void doBeforeEach(QuarkusTestMethodContext context) throws Exception {
		mockRouteEndpoints();
	}

	@Test
	void testFTPFileContentIsWrittenToFile() throws Exception {
		fileMock.message( 0 ).body().isEqualTo( "Hello World" );
		template.sendBody( "direct:ftp", "Hello World" );
		fileMock.assertIsSatisfied();
	}

	private void mockRouteEndpoints() throws Exception {
		AdviceWithRouteBuilder.adviceWith(context, "ftpRoute", route ->
			route.replaceFromWith("direct:ftp"));
		AdviceWithRouteBuilder.adviceWith(context, "ftpRoute", route ->
			route.interceptSendToEndpoint( "file:.*customer_requests.*" )
				 .skipSendToOriginalEndpoint()
				 .to( "mock:file:customer_requests" ));
	}

}
