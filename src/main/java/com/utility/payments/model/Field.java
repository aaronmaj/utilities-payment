package com.utility.payments.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="fields")
@XmlRootElement
@XmlSeeAlso({Detail.class})
public class Field implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "name", unique = true, nullable = false)
    private String name;
    @Transient
    private String value;
    private String description;
    @OneToMany(targetEntity = Detail.class,cascade = CascadeType.ALL,fetch = FetchType.EAGER,mappedBy = "field",orphanRemoval=true)
    private List<Detail> payments=new ArrayList<>();

    public Field() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @JsonIgnore
     public List<Detail> getPayments() {
        return payments;
    }
    @JsonProperty
    public void setPayments(List<Detail> payments) {
        this.payments = payments;
    }
}
