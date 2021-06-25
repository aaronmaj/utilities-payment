package com.utility.payments.model;


import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Entity
@Table(name = "payment_details")
@XmlRootElement(name = "payment_details")
@IdClass(DetailKey.class)
public class Detail implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "field_id")
    private Long fieldId;
    @Id
    @Column(name = "payment_id")
    private Long paymentId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_id", insertable = false, updatable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "field_id", insertable = false, updatable = false)
    private Field field;
    @Column(name = "name")
    private String name;
    @Column(name = "value")
    private String value;

    public Detail() {
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
