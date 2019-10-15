package com.msy.retrofitbaseurlmanager.interceptors;

import android.os.Build;

import com.msy.xretrofit.interfaces.RequestInterceptorHandler;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class TokenInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("Token", "TOKEN11111111111111111111111111111111111");
        Request request = builder.build();

        Response response = chain.proceed(request);

        return response;
    }
}
