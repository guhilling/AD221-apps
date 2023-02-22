package com.redhat.training.payments;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.graalvm.nativeimage.c.struct.CPointerTo;

@Entity
@Table( name = "payment_analysis" )
public class PaymentAnalysis {

    @Id
    private Long id;
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(name = "fraud_score")
    private Double score;

    @Column(name = "analysis_status")
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}