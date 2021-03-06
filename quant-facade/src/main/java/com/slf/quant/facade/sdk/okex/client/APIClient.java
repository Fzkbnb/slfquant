package com.slf.quant.facade.sdk.okex.client;


import com.alibaba.fastjson.JSON;
import com.slf.quant.facade.sdk.okex.bean.ett.result.CursorPager;
import com.slf.quant.facade.sdk.okex.bean.futures.HttpResult;
import com.slf.quant.facade.sdk.okex.config.APIConfiguration;
import com.slf.quant.facade.sdk.okex.constant.APIConstants;
import com.slf.quant.facade.sdk.okex.enums.HttpHeadersEnum;
import com.slf.quant.facade.sdk.okex.exception.APIException;
import com.slf.quant.facade.sdk.okex.utils.DateUtils;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * OKEX API Client
 *
 * @author Tony Tian
 * @version 1.0.0
 * @date 2018/3/8 13:43
 */
public class APIClient {

    private static final Logger LOG = LoggerFactory.getLogger(APIClient.class);

    private final APIConfiguration config;
    private final APICredentials credentials;
    private final OkHttpClient client;
    private final Retrofit retrofit;
    private final ApiHttp apiHttp;

    /**
     * Initialize the apis client
     */
    public APIClient(final APIConfiguration config) {
        if (config == null || StringUtils.isEmpty(config.getEndpoint())) {
            throw new RuntimeException("The APIClient params can't be empty.");
        }
        this.config = config;
        this.credentials = new APICredentials(config);
        this.client = new APIHttpClient(config, this.credentials).client();
        this.retrofit = new APIRetrofit(config, this.client).retrofit();
        this.apiHttp = new ApiHttp(config, this.client);
    }

    /**
     * Initialize the retrofit operation service
     */
    public <T> T createService(final Class<T> service) {
        return this.retrofit.create(service);
    }

    public ApiHttp getApiHttp() {
        return this.apiHttp;
    }

    /**
     * Synchronous send request
     */
    //??????
    public <T> T executeSync(final Call<T> call) {
        try {

            final Response<T> response = call.execute();
            //System.out.println("response-------------------------"+call.toString());
            //????????????config????????????
            if (this.config.isPrint()) {
                //??????????????????
                this.printResponse(response);
            }
            //???????????????
            final int status = response.code();
            //??????????????????
            final String message = new StringBuilder().append(response.code()).append(" / ").append(response.message()).toString();
            //????????????
            if (response.isSuccessful()) {
                return response.body();
                ////??????????????????400,401,429,500?????????????????????????????????
            } else if (APIConstants.resultStatusArray.contains(status)) {
                final HttpResult result = JSON.parseObject(new String(response.errorBody().bytes()), HttpResult.class);
                if (result.getCode() == 0 && result.getMessage() == null) {
                    // System.out.println("????????????"+result.getErrorCode()+"\t????????????"+result.getErrorMessage());
                    throw new APIException(result.getErrorCode(), result.getErrorMessage());
                } else {
                    //System.out.println("????????????"+result.getCode()+"\t????????????"+result.getMessage());
                    //????????????
                    throw new APIException(result.getCode(), result.getMessage());
                }
            } else {
                throw new APIException(message);
            }
        } catch (final IOException e) {
            throw new APIException("APIClient executeSync exception.", e);
        }
    }

    /**
     * Synchronous send request
     */
    public <T> CursorPager<T> executeSyncCursorPager(final Call<List<T>> call) {
        try {
            final Response<List<T>> response = call.execute();
            System.out.println("????????????before");
            if (this.config.isPrint()) {
                this.printResponse(response);
            }
            System.out.println("????????????after");
            final int status = response.code();
            final String message = response.code() + " / " + response.message();
            if (response.isSuccessful()) {
                final Headers headers = response.headers();
                final CursorPager<T> cursorPager = new CursorPager<T>();
                cursorPager.setData(response.body());
                cursorPager.setBefore(headers.get("OK-BEFORE"));
                cursorPager.setAfter(headers.get("OK-AFTER"));
                cursorPager.setLimit(Optional.ofNullable(headers.get("OK-LIMIT")).map(Integer::valueOf).orElse(100));
                return cursorPager;
            }
            if (APIConstants.resultStatusArray.contains(status)) {
                final HttpResult result = JSON.parseObject(new String(response.errorBody().bytes()), HttpResult.class);
                throw new APIException(result.getCode(), result.getMessage());
            }
            throw new APIException(message);
        } catch (final IOException e) {
            System.out.println("????????????");
            throw new APIException("APIClient executeSync exception.", e);
        }
    }

    private void printResponse(final Response response) {
        final StringBuilder responseInfo = new StringBuilder();
        responseInfo.append("\n\tResponse").append("(").append(DateUtils.timeToString(null, 4)).append("):");
        if (response != null) {
            final String limit = response.headers().get(HttpHeadersEnum.OK_LIMIT.header());
            if (StringUtils.isNotEmpty(limit)) {
                responseInfo.append("\n\t\t").append("Headers: ");
//                responseInfo.append("\n\t\t\t").append(HttpHeadersEnum.OK_BEFORE.header()).append(": ").append(response.headers().get(HttpHeadersEnum.OK_BEFORE.header()));
//                responseInfo.append("\n\t\t\t").append(HttpHeadersEnum.OK_AFTER.header()).append(": ").append(response.headers().get(HttpHeadersEnum.OK_AFTER.header()));
                responseInfo.append("\n\t\t\t").append(HttpHeadersEnum.OK_FROM.header()).append(": ").append(response.headers().get(HttpHeadersEnum.OK_FROM.header()));
                responseInfo.append("\n\t\t\t").append(HttpHeadersEnum.OK_TO.header()).append(": ").append(response.headers().get(HttpHeadersEnum.OK_TO.header()));
                responseInfo.append("\n\t\t\t").append(HttpHeadersEnum.OK_LIMIT.header()).append(": ").append(limit);
            }
            //responseInfo.append("\n\t\t").append("????????????: ").append(response.toString());
            responseInfo.append("\n\t\t").append("Status: ").append(response.code());
            responseInfo.append("\n\t\t").append("Message: ").append(response.message());
            responseInfo.append("\n\t\t").append("Response Body: ").append(JSON.toJSONString(response.body()));
        } else {
            responseInfo.append("\n\t\t").append("\n\tRequest Error: response is null");
        }
        APIClient.LOG.info(responseInfo.toString());
    }

    @Override
    public String toString() {
        return "APIClient{" +
                "config=" + config +
                ", credentials=" + credentials +
                ", client=" + client +
                ", retrofit=" + retrofit +
                ", apiHttp=" + apiHttp +
                '}';
    }
}
