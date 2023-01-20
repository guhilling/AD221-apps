package com.redhat.training.payslipvalidator.route;

import io.quarkus.test.junit.QuarkusTest;

import org.apache.camel.builder.AdviceWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AmountProcessRouteTest extends PayslipTests {

    @Test
    void routeSendsMessageToProcessEndpoint () throws Exception {
        mockProcess.expectedMessageCount(1);
        fileMockErrorAmount.expectedMessageCount(0);

        template.sendBody("direct:payslips-amount", validContent());

        mockProcess.assertIsSatisfied();
        fileMockErrorAmount.assertIsSatisfied();
    }

    @Test
    void routeCatchesNumberFormatException () throws Exception {
        mockProcess.expectedMessageCount(0);
        fileMockErrorAmount.expectedMessageCount(1);

        template.sendBody("direct:payslips-amount", amountErrorContent());

        mockProcess.assertIsSatisfied();
        fileMockErrorAmount.assertIsSatisfied();
    }

    @BeforeEach
    void doAdvice() throws Exception {
        AdviceWith.adviceWith(context(), "amount-process", PayslipTests::advicePayslipsAmountRoute);
    }

}
