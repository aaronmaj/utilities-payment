package com.utility.payments.model;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "category")
@XmlRootElement(name ="category" )
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "category_name", nullable = false, unique = true)
    private String name;
    private String description;
    @OneToMany(fetch=FetchType.EAGER,mappedBy = "category",cascade=CascadeType.ALL)
    private List<SubCategory> subCategories;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }
    @JsonIgnore
    public List<SubCategory> getSubCategories() {
        return subCategories;
    }
    @JsonProperty
    public void setSubCategories(List<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }
}
