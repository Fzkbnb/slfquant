package com.slf.quant.facade.sdk.okex.v5.utils;

import java.util.UUID;

import com.slf.quant.facade.sdk.okex.v5.constant.APIConstants;


public class OrderIdUtils {

    /**
     * The order ids, use uuid and remove the line dividing line. <br/>
     * id length = 32
     *
     * @return String eg: 39360db0a45e41309511f4fba658b01c
     */
    public static String generator() {
        return UUID.randomUUID().toString().toLowerCase().replace(APIConstants.HLINE, APIConstants.EMPTY);
    }

    public static void main(String[] args) {
        System.out.println(generator());
    }
}
