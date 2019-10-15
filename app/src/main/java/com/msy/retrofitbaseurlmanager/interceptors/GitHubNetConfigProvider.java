package com.msy.retrofitbaseurlmanager.interceptors;

import android.os.Build;

import com.msy.xretrofit.interfaces.NetConfigProvider;
import com.msy.xretrofit.interfaces.RequestInterceptorHandler;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class GitHubNetConfigProvider implements NetConfigProvider {
    @Override
    public Interceptor[] interceptors() {
        //添加其他拦截器
        Interceptor[] interceptors = {new TokenInterceptor()};
        return interceptors;
    }

    @Override
    public RequestInterceptorHandler configHandler() {
        return new RequestInterceptorHandler() {
            @Override
            public Request operatorBeforeRequest(Request request, Interceptor.Chain chain) {
                Request.Builder builder = chain.request().newBuilder();
                builder.addHeader("platformVersion", Build.VERSION.SDK_INT + "");
                builder.addHeader("platform", "Android");
                builder.addHeader("from", "github");

                return builder.build();
            }

            @Override
            public Response operatorAfterRequest(Response response, Interceptor.Chain chain) {
                return response;
            }
        };
    }

    @Override
    public long configConnectTimeoutMills() {
        return 0;
    }

    @Override
    public long configReadTimeoutMills() {
        return 0;
    }

    @Override
    public boolean configLogEnable() {
        return true;
    }
}
