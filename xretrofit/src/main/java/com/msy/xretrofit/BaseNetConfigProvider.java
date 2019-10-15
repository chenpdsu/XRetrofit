package com.msy.xretrofit;


import com.msy.xretrofit.interfaces.NetConfigProvider;
import com.msy.xretrofit.interfaces.RequestInterceptorHandler;

import okhttp3.Interceptor;

public class BaseNetConfigProvider implements NetConfigProvider {
    @Override
    public Interceptor[] interceptors() {
        return new Interceptor[0];
    }

    @Override
    public RequestInterceptorHandler configHandler() {
        return new BaseRequestInterceptorHandler();
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
