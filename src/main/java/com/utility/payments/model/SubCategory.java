package com.utility.payments.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="sub_category")
@XmlRootElement(name = "SubCategory")
public class SubCategory implements Serializable {

    @Id
    @GeneratedValue
    @Column(name="code")
    private Integer id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="category_id", nullable = false)
    private Category category;
    private String description;
    @OneToMany(fetch=FetchType.EAGER,mappedBy = "subCategory",cascade=CascadeType.ALL)
    @XmlTransient
    private List<Account> accounts;

    public SubCategory() {
    }

    public SubCategory(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @XmlTransient
    @JsonIgnore
    public List<Account> getAccounts() {
        return accounts;
    }
    @JsonProperty
    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
