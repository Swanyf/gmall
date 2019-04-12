package com.swan.gmall.list.json;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

public class BaseCatalog1  implements Serializable {
    @Id
    @Column
    private String id;

    @Transient
    private List<BaseCatalog2> catalog2List;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<BaseCatalog2> getCatalog2List() {
        return catalog2List;
    }

    public void setCatalog2List(List<BaseCatalog2> catalog2List) {
        this.catalog2List = catalog2List;
    }

}
