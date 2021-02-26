package com.slf.quant.facade.sdk.huobi.response;

import java.util.List;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 17:51
 */

public class BatchcancelResponse {

    /**
     * status : ok
     * data : {"success":["1","3"],"failed":[{"err-msg":"记录无效","order-id":"2","err-code":"base-record-invalid"}]}
     */

    private List<String> success;
    private List<BatchcancelBean> failed;

    public List<String> getSuccess() {
        return success;
    }

    public void setSuccess(List<String> success) {
        this.success = success;
    }

    public List<BatchcancelBean> getFailed() {
        return failed;
    }

    public void setFailed(List<BatchcancelBean> failed) {
        this.failed = failed;
    }

    @Override
    public String toString() {
        return "BatchcancelResponse{" +
                "success=" + success +
                ", failed=" + failed +
                '}';
    }
}
