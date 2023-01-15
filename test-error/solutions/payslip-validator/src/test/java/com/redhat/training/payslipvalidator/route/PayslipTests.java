package com.redhat.training.payslipvalidator.route;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.BeforeEach;

abstract class PayslipTests extends CamelQuarkusTestSupport {

    @Inject
    protected ProducerTemplate template;

    @Inject
    protected CamelContext context;

    @EndpointInject("mock:process")
    protected MockEndpoint mockProcess;

    @EndpointInject("mock:file:error_amount")
    protected MockEndpoint fileMockErrorAmount;

    @EndpointInject("mock:file:error_dead_letter")
    protected MockEndpoint fileMockErrorDeadLetter;

    @EndpointInject("mock:file:error_price")
    protected MockEndpoint fileMockErrorPrice;

    @EndpointInject("mock:file:validation_ok")
    protected MockEndpoint fileMockValidationOk;

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new PayslipValidationRouteBuilder();
    }

    @BeforeEach
    void doAdvice() throws Exception {
        AdviceWith.adviceWith(context(), "amount-process", PayslipTests::advicePayslipsAmountRoute);
        AdviceWith.adviceWith(context(), "price-process", PayslipTests::advicePriceProcessRoute);
    }

    private static void advicePayslipsAmountRoute(AdviceWithRouteBuilder route) {
        route.replaceFromWith("direct:payslips-amount");

        route.interceptSendToEndpoint("direct:process")
             .skipSendToOriginalEndpoint()
             .to("mock:process");

        route.interceptSendToEndpoint("file://data/validation/error-amount")
             .skipSendToOriginalEndpoint()
             .to("mock:file:error_amount");

        // Dead Letter
        route.interceptSendToEndpoint("file://data/validation/error-dead-letter")
             .skipSendToOriginalEndpoint()
             .to("mock:file:error_dead_letter");

        // onException Clause
        route.interceptSendToEndpoint("file://data/validation/error-price")
             .skipSendToOriginalEndpoint()
             .to("mock:file:error_price");
    }

    private static void advicePriceProcessRoute(AdviceWithRouteBuilder route) {
        route.replaceFromWith("direct:payslips-price");

        route.interceptSendToEndpoint("file://data/validation/correct")
             .skipSendToOriginalEndpoint()
             .to("mock:file:validation_ok");
    }

    protected String validContent() {
        return "<payslip><totalPayslip>6000.00</totalPayslip><payslipItems><payslipItem><payslipItemQty>30</payslipItemQty><payslipItemPrice>200.00</payslipItemPrice></payslipItem></payslipItems></payslip>";
    }

    protected String amountErrorContent() {
        return "<payslip><payslipItems><payslipItem><payslipItemQty>1.5</payslipItemQty><payslipItemPrice>1024.20</payslipItemPrice></payslipItem></payslipItems></payslip>";
    }

    protected String priceErrorContent() {
        return "<payslip><payslipItems><payslipItem><payslipItemId>456</payslipItemId><payslipItemQty>1</payslipItemQty><description>Award</description><payslipItemPrice>NA</payslipItemPrice></payslipItem></payslipItems></payslip>";
    }

    protected String wrongCalculationErrorContent() {
        return "<payslip><totalPayslip>1.23</totalPayslip><payslipItems><payslipItem><payslipItemId>456</payslipItemId><payslipItemQty>1</payslipItemQty><description>Award</description><payslipItemPrice>2.50</payslipItemPrice></payslipItem></payslipItems></payslip>";
    }
}
