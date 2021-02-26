package com.slf.quant.facade.sdk.huobi.spot.client;

import java.net.URL;

import com.slf.quant.facade.sdk.huobi.spot.client.exception.HuobiApiException;

/**
 * The configuration for the request APIs
 */
public class RequestOptions
{
    private String url = "https://api.huobi.pro";
    
    public RequestOptions()
    {
    }
    
    public RequestOptions(String url)
    {
        this.url = url;
    }
    
    public RequestOptions(RequestOptions option)
    {
        this.url = option.url;
    }
    
    /**
     * Set the URL for request.
     *
     * @param url The URL name like "https://api.huobi.pro".
     */
    public void setUrl(String url)
    {
        try
        {
            URL u = new URL(url);
        }
        catch (Exception e)
        {
            throw new HuobiApiException(HuobiApiException.INPUT_ERROR, "The URI is incorrect: " + e.getMessage());
        }
        this.url = url;
    }
    
    public String getUrl()
    {
        return url;
    }
}
