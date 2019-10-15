package com.msy.xretrofit;

import android.os.Build;

import com.msy.xretrofit.interfaces.RequestInterceptorHandler;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 统一添加头部header处理
 */
public class BaseRequestInterceptorHandler implements RequestInterceptorHandler {
    @Override
    public Request operatorBeforeRequest(Request request, Interceptor.Chain chain) {
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("platformVersion", Build.VERSION.SDK_INT + "");
        builder.addHeader("platform", "Android");
        builder.addHeader("form", "common");
        return builder.build();
    }

    @Override
    public Response operatorAfterRequest(Response response, Interceptor.Chain chain) {
        return response;
    }
}
