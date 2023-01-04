package com.redhat.training;

public class CommandConfigurationRouteBuilderTest {

//	@Autowired
//	private ConsumerTemplate consumerTemplate;
//
//	@Autowired
//	private CamelContext context;
//
//
//	@Test
//	public void testRoute() throws Exception {
//
//		RouteDefinition airPurifierConfigRouteDef = context.getRouteDefinition("air-purifier-configuration-route");
//
//		airPurifierConfigRouteDef.adviceWith(context, new AdviceWithRouteBuilder() {
//				@Override
//				public void configure() {
//					interceptSendToEndpoint("direct:logReturnCode")
//					.skipSendToOriginalEndpoint()
//					.to("direct:output");
//				}
//			}
//		);
//
//		context.startRoute(airPurifierConfigRouteDef.getId());
//
//		Assert.assertEquals(200, consumerTemplate.receive("direct:output").getIn().getBody());
//
//		context.stopRoute(airPurifierConfigRouteDef.getId());
//	}

}
