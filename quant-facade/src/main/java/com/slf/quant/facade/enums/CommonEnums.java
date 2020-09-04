package com.slf.quant.facade.enums;


import com.slf.quant.facade.bean.EnumDescribable;


public enum CommonEnums implements EnumDescribable
{
    SUCCESS(200, "success"), // 操作成功
    FAIL(400, "failed"), // 操作失败
    UNAUTHORIZED(401, "unauthorized"), // 未认证（签名错误）
    LOGIN_FAIL(402, "username or password verification failure"), // 接口不存在
    NOT_FOUND(404, "request url not fond"), // 接口不存在
    INTERNAL_SERVER_ERROR(500, "internal server error"), // 服务器内部错误
    SERVER_BUSY_ERROR(501, "server is busy,Please try later"), // 服务器内部错误
    ;
    public Integer code;
    
    public String  message;

    CommonEnums(Integer code, String message)
    {
        this.code = code;
        this.message = message;
    }
    
    /**
     * 根据状态码获取状态码描述
     *
     * @param code 状态码
     * @return String 状态码描述
     */
    public static String getMessage(Integer code)
    {
        String result = null;
        for (CommonEnums c : CommonEnums.values())
        {
            if (c.code.equals(code))
            {
                result = c.message;
                break;
            }
        }
        return result;
    }
    
    @Override
    public Integer getCode()
    {
        return this.code;
    }
    
    public void setCode(Integer code)
    {
        this.code = code;
    }
    
    @Override
    public String getMessage()
    {
        return this.message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
}
