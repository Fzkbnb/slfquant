package com.slf.quant.facade.sdk.okex.bean.spot.result;

import lombok.Data;

@Data
public class OrderResult
{
    private boolean result;
    
    private Long    order_id;
    
    private String  client_oid;
    
    private String  error_code;
    
    private String  error_message;
}
