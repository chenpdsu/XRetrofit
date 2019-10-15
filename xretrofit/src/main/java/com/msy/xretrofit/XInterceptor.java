package com.msy.xretrofit;

import com.msy.xretrofit.interfaces.RequestInterceptorHandler;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class XInterceptor implements Interceptor {

    RequestInterceptorHandler mInterceptorHandler;

    public XInterceptor(RequestInterceptorHandler interceptorHandler) {
        mInterceptorHandler = interceptorHandler;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (mInterceptorHandler != null) {
            request = mInterceptorHandler.operatorBeforeRequest(request, chain);
        }
        Response response = chain.proceed(request);
        if (mInterceptorHandler != null) {
            Response tmp = mInterceptorHandler.operatorAfterRequest(response, chain);
            if (tmp != null) {
                return tmp;
            }
        }
        return response;
    }
}
