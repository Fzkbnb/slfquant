package com.slf.quant.facade.sdk.okex.v5.bean.account.param;

public class SetPositionMode {
    private String posMode;

    public String getPosMode() {
        return posMode;
    }

    public void setPosMode(String posMode) {
        this.posMode = posMode;
    }

    @Override
    public String toString() {
        return "SetPositionMode{" +
                "posMode='" + posMode + '\'' +
                '}';
    }
}
