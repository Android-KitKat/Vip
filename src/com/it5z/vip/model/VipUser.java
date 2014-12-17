package com.it5z.vip.model;

import java.sql.Timestamp;

/**
 * Created by Administrator on 2014/12/10.
 */
public class VipUser {
    private String name;
    private String base;
    private String type;
    private Timestamp updateTime;
    private Timestamp validTime;
    private boolean baseValid;

    public VipUser(String name, String base, String type, Timestamp updateTime, Timestamp validTime, boolean baseValid) {
        this.name = name;
        this.base = base;
        this.type = type;
        this.updateTime = updateTime;
        this.validTime = validTime;
        this.baseValid = baseValid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Timestamp getValidTime() {
        return validTime;
    }

    public void setValidTime(Timestamp validTime) {
        this.validTime = validTime;
    }

    public boolean isBaseValid() {
        return baseValid;
    }

    public void setBaseValid(boolean baseValid) {
        this.baseValid = baseValid;
    }
}
