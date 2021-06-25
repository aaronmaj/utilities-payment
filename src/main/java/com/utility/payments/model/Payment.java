package com.utility.payments.model;



import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfile;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "payment")
@FetchProfile(name = "payments-with-details", fetchOverrides = {
        @FetchProfile.FetchOverride(entity = Payment.class, association = "details", mode = FetchMode.JOIN)
})
@XmlRootElement(name = "payment")
@XmlSeeAlso({Detail.class,Account.class})
@XmlAccessorType(XmlAccessType.FIELD)
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;
    private String msisdn;
    @Column(name="payer_name")
    private String payerName;
    @Column(name="referenceNo", nullable = false,unique =true)
    private String referenceNo;
    private String status;
    private double amount;
    private String description;
    @ManyToOne
    @JoinColumn(name = "accountNo")
    private Account account ;
    @Column(name="externalRefNo", nullable = false,unique =true)
    private String externalRefNo;
    @Column(name="selfPay")
    private Boolean selfPay;
    @Column(name="customer_name")
    private String customerName;
    @Column(name="payment_date")
    private Date paymentDate;
    @OneToMany(targetEntity = Detail.class,cascade = CascadeType.ALL, fetch = FetchType.EAGER,mappedBy = "payment")
    @XmlElementWrapper
    @XmlElement(name = "detail")
    private List<Detail> details=new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getExternalRefNo() {
        return externalRefNo;
    }

    public void setExternalRefNo(String externalRefNo) {
        this.externalRefNo = externalRefNo;
    }

    public Boolean getSelfPay() {
        return selfPay;
    }

    public void setSelfPay(Boolean selfPay) {
        this.selfPay = selfPay;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
    @JsonIgnore
    public List<Detail> getDetails() {
        return details;
    }
    @JsonProperty
    public void setDetails(List<Detail> details) {
        this.details = details;
    }


}
