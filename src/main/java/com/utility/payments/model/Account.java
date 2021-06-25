package com.utility.payments.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "account")
@XmlRootElement(name = "account")
@XmlSeeAlso({SubCategory.class})
@XmlAccessorType(XmlAccessType.FIELD)
public class Account implements Serializable {
    @Id
    @Column(name = "accountNo")
    private Long id;
    @Column(name = "accountCode")
    private String code;
    private String name;
    @Column(name = "en_name")
    private String enName;
    @Column(name = "rn_name")
    private String rnName;
    @ManyToOne
    @JoinColumn(name = "code")
     private SubCategory subCategory;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "account", cascade = CascadeType.ALL)
    @XmlTransient
    private List<Payment> payments;

    public Account() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getRnName() {
        return rnName;
    }

    public void setRnName(String rnName) {
        this.rnName = rnName;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }
    @XmlTransient
    @JsonIgnore
    public List<Payment> getPayments() {
        return payments;
    }
    @JsonProperty
    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}
