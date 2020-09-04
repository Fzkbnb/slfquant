package com.slf.quant.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author slf
 * @date 2019/6/14 20:37
 * @description
 */
public interface DrawImageService {

    void drawImage(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
