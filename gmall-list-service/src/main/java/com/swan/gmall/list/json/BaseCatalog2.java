package com.swan.gmall.list.json;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

public class BaseCatalog2 implements Serializable {

    @Column
    private String catalog1Id;

    @Id
    @Column
    private String id;

    @Column
    private String name;

    @Transient
    private List<BaseCatalog3> catalog3List;

    public String getCatalog1Id() {
        return catalog1Id;
    }

    public void setCatalog1Id(String catalog1Id) {
        this.catalog1Id = catalog1Id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BaseCatalog3> getCatalog3List() {
        return catalog3List;
    }

    public void setCatalog3List(List<BaseCatalog3> catalog3List) {
        this.catalog3List = catalog3List;
    }
}
