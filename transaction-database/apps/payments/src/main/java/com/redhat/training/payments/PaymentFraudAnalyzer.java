package com.redhat.training.payments;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mock fraud detector
 */
public class PaymentFraudAnalyzer implements Processor {
    Logger LOG = LoggerFactory.getLogger(PaymentFraudAnalyzer.class);

    @Override
    public void process( Exchange exchange ) {
        Double fraudScore = 0.0;

        Payment payment = exchange.getIn().getBody( Payment.class );
        String email = payment.getEmail();
        Double amount = payment.getAmount();

        if ( email.length() > 10 ) {
            fraudScore += email.length() * 0.09;
        }

        if (amount > 1000) {
            fraudScore += amount * 0.00007;
        }

        if (email.contains("offer")) {
            fraudScore += 40;
        }

        if (email.contains("deal")) {
            fraudScore += 40;
        }

        fraudScore = Math.min(100.0, fraudScore) / 100;

        exchange.getIn().setHeader("fraudScore", fraudScore);

        LOG.info("computed score {} for email {}", fraudScore, email);
    }
}
