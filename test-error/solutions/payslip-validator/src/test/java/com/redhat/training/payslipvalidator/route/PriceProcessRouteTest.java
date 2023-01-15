package com.redhat.training.payslipvalidator.route;

import io.quarkus.test.junit.QuarkusTest;

import org.junit.jupiter.api.Test;

@QuarkusTest
class PriceProcessRouteTest extends PayslipTests {

    @Test
    void routeSendsMessageToCorrectEndpoint () throws Exception {
        fileMockValidationOk.expectedMessageCount(1);

        template.sendBody(
                "direct:payslips-price",
                validContent()
        );

        fileMockValidationOk.assertIsSatisfied();
    }

    @Test
    void numberFormatExceptionCapturedByOnExceptionClause () throws Exception {
        fileMockErrorPrice.expectedMessageCount(2);

        template.sendBody(
                "direct:payslips-price",
                amountErrorContent()
        );

        template.sendBody(
                "direct:payslips-price",
                priceErrorContent()
        );

        fileMockErrorPrice.assertIsSatisfied();
    }


    @Test
    void wrongCalculationExceptionCapturedByDeadLetter () throws Exception {
        fileMockErrorDeadLetter.expectedMessageCount(1);

        template.sendBody(
                "direct:payslips-price",
                wrongCalculationErrorContent()
        );

        fileMockErrorDeadLetter.assertIsSatisfied();
    }
}
