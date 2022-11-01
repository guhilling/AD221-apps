package com.redhat.training.processingorders;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.CamelSpringTest;
import org.apache.camel.test.spring.junit5.CamelSpringTestSupport;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@CamelSpringBootTest
@EnableAutoConfiguration
@CamelSpringTest
@MockEndpoints
@UseAdviceWith
public class FtpToFileRouteBuilderTest {

	@Produce("direct:ftp")
	protected ProducerTemplate template;

	@Autowired
	protected CamelContext context;

	@EndpointInject( "mock:file:customer_requests" )
	protected MockEndpoint fileMock;

	@Configuration
	@org.apache.camel.Configuration
	public static class TestConfig {
		@Bean
		RoutesBuilder route() {
			return new FtpToFileRouteBuilder();
		}
	}
	@BeforeEach
	public void setUp() throws Exception {
		mockRouteEndpoints();
		context.start();
	}

	@AfterEach
	public void tearDown() throws Exception {
		context.stop();
	}

	@Test
	public void testFTPFileContentIsWrittenToFile() throws Exception {
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
