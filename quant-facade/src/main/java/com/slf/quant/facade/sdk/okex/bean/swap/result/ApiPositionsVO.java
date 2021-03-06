package com.slf.quant.facade.sdk.okex.bean.swap.result;

import java.util.List;

public class ApiPositionsVO {

    private String margin_mode;
    private List<ApiPositionVO> holding;

    public ApiPositionsVO(String margin_mode, List<ApiPositionVO> holding) {
        this.margin_mode = margin_mode;
        this.holding = holding;
    }

    public ApiPositionsVO() {
    }

    public String getMargin_mode() {
        return margin_mode;
    }

    public void setMargin_mode(String margin_mode) {
        this.margin_mode = margin_mode;
    }

    public List<ApiPositionVO> getHolding() {
        return holding;
    }

    public void setHolding(List<ApiPositionVO> holding) {
        this.holding = holding;
    }

}
