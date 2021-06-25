package com.utility.payments.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="marital_status")
public class MaritalStatus implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @Column(name = "en_name")
    private String enName;
    @Column(name = "rn_name")
    private String rnName;

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
}
