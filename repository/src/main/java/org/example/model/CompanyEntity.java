package org.example.model;

import java.util.Date;

public class CompanyEntity  extends BaseEntity {

    private String code;
    private String name;
    private Integer listPriority;
    private Boolean isDisabled;

    private Integer createdUserId;
    private Date createdAt;
    private Integer modifiedUserId;
    private Date modifiedAt;

    public CompanyEntity() {
        super(CompanyEntity.class, "Code");
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setListPriority(Integer listPriority) {
        this.listPriority = listPriority;
    }

    public Integer getListPriority() {
        return listPriority;
    }

    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public Integer getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(Integer createdUserId) {
        this.createdUserId = createdUserId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getModifiedUserId() {
        return modifiedUserId;
    }

    public void setModifiedUserId(Integer modifiedUserId) {
        this.modifiedUserId = modifiedUserId;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
