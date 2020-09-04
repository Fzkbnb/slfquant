/*
 * @(#)ErrorCodeDescribable.java 2014年3月6日 下午3:06:33
 * Copyright 2014 Playguy, Inc. All rights reserved. com.blocain
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.slf.quant.facade.bean;

import java.io.Serializable;

public interface EnumDescribable extends Serializable
{
    Integer getCode();
    
    String getMessage();
}
