package com.slf.quant.facade.sdk.huobi.response;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 16:02
 */

public class Accounts {
    /**
     * id : 100009
     * type : spot
     * state : working
     * user-id : 1000
     */

    private int id;
    private String type;
    private String state;
    private int userid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Accounts{");
        sb.append("id=").append(id);
        sb.append(", type='").append(type).append('\'');
        sb.append(", state='").append(state).append('\'');
        sb.append(", userid=").append(userid);
        sb.append('}');
        return sb.toString();
    }
}
