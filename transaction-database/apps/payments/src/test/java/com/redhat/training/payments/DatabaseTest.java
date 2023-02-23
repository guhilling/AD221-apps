package com.redhat.training.payments;

import io.quarkus.test.junit.QuarkusTest;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class DatabaseTest {

    @Inject
    protected EntityManager entityManager;

    @BeforeEach
    void waitForCompletion() throws Exception {
        Thread.sleep(1000);
    }

    @Test
    void testAnalysisIsSetAsCompletedInDB() {
        List<PaymentAnalysis> paymentAnalysis = entityManager.createQuery("SELECT p FROM PaymentAnalysis p",
                                                                          PaymentAnalysis.class)
                                                             .getResultList();
        paymentAnalysis.forEach(each -> {
            Assertions.assertEquals("Completed", each.getStatus());
        });
    }

    @Test
    void testFraudScoreIsCorrectInDB() {
        Double totalScore = entityManager.createQuery("SELECT sum(p.score) FROM PaymentAnalysis p",
                                                      Double.class)
                                         .getSingleResult();
        Assertions.assertEquals(1.256, totalScore);
    }
}
