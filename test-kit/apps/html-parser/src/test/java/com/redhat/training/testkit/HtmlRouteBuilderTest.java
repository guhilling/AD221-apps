package com.redhat.training.testkit;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class HtmlRouteBuilderTest extends CamelQuarkusTestSupport {

	public static final File WARNING_FILE = new File("out/latest-warning.txt");
	public static final File ERROR_FILE = new File("out/latest-error.txt");
	private static final String warningsHtml;
	private static final String errorsHtml;

	static {
		try {
			warningsHtml = Files.readString(Path.of("", "src/main/resources/test_warnings.html"));
			errorsHtml = Files.readString(Path.of("", "src/main/resources/test_errors.html"));
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	@Inject
	protected ProducerTemplate producerTemplate;

	@Inject
	protected ConsumerTemplate consumerTemplate;

	@Override
	protected RoutesBuilder createRouteBuilder() {
		return new HtmlRouteBuilder();
	}

	@BeforeEach
	void cleanUp() {
		WARNING_FILE.delete();
		ERROR_FILE.delete();
	}

	@Test
	void testRouteWritesLatestWarningToFile() {
		producerTemplate.sendBody( "direct:parseHtmlErrors", warningsHtml );

		assertTrue(WARNING_FILE.exists(), "out/latest-warning.txt does not exist");
	}

	@Test
	void testRouteWritesLatestErrorToFile() {
		producerTemplate.sendBody( "direct:parseHtmlErrors", errorsHtml );

		assertTrue(ERROR_FILE.exists(), "out/latest-error.txt does not exist");
	}

	@Test
	void testRouteParsesLatestWarningText() throws Exception {
		producerTemplate.sendBody( "direct:parseHtmlErrors", warningsHtml );

		Thread.sleep(2);

		String body = consumerTemplate.receiveBody( "file:out", String.class );
		assertTrue( body.contains( "DeprecationWarning: Creating a LegacyVersion has been deprecated" ) );
	}


	@Test
	void testRouteParsesLatestErrorText() throws Exception {
		producerTemplate.sendBody( "direct:parseHtmlErrors", errorsHtml );

		Thread.sleep(2);

		String body = consumerTemplate.receiveBody( "file:out", String.class );
		assertTrue( body.contains( "Exception occurred during execution on the exchange" ) );
	}

}
