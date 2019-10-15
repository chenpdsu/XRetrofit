package com.msy.xretrofit.interfaces;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 通用请求拦截
 */
public interface RequestInterceptorHandler {

    Request operatorBeforeRequest(Request request, Interceptor.Chain chain);

    Response operatorAfterRequest(Response response, Interceptor.Chain chain);
}
