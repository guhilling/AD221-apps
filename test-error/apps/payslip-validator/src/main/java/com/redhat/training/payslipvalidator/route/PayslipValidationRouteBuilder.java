package com.redhat.training.payslipvalidator.route;

import com.redhat.training.payslipvalidator.processor.AmountProcessor;
import com.redhat.training.payslipvalidator.processor.PriceProcessor;
import org.apache.camel.builder.RouteBuilder;

public class PayslipValidationRouteBuilder extends RouteBuilder {
    @Override
    public void configure() {
        // TODO: Handle errors with the onException clause
        /*
        onException(NumberFormatException.class)
            .to("file://data/validation/error-price")
            .log("exception")
            .handled(true);
        */

        // TODO: Handle errors with the dead letter channel EIP
        errorHandler(deadLetterChannel("file://data/validation/error-dead-letter")
             .log("error")
             .disableRedelivery());

        // TODO: Add doTry/Catch block
        from("file://data/payslips?noop=true")
            .routeId("amount-process")
            //.doTry()
                .process(new AmountProcessor())
                .log("amount")
                .to("direct:process")
            //.doCatch(NumberFormatException.class)
                .log("nfe")
                .to("file://data/validation/error-amount")
            //.endDoTry()
            ;

        from("direct:process")
            .log("process pricess")
            .routeId("price-process")
            .process(new PriceProcessor())
        .to("file://data/validation/correct");
    }
}
