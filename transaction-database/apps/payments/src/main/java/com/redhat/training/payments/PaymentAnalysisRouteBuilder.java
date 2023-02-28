package com.redhat.training.payments;

import org.apache.camel.builder.RouteBuilder;

public class PaymentAnalysisRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {
        // TODO: specify JPA endpoint
        from("")
        /*
        from("jpa:com.redhat.training.payments.Payment?"
                + "persistenceUnit=mysql"
                + "&consumeDelete=false"
                + "&maximumResults=5"
                + "&initialDelay=100"
                + "&runLoggingLevel=INFO"
                + "&consumeLockEntity=false")
            .log("${body}")
        */
            .process(new PaymentFraudAnalyzer())
            // TODO: add SQL producer endpoint
            /*
            .to("sql:update payment_analysis "
                + "set fraud_score =:#${headers.fraudScore}, analysis_status = 'Completed' "
                + "where payment_id=:#${body.id}")
            */
            .to("direct:payment_analysis_complete?failIfNoConsumers=false&block=false");
    }
}
