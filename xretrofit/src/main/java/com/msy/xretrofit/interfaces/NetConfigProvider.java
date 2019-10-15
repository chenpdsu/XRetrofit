package com.msy.xretrofit.interfaces;


import okhttp3.Interceptor;

//        使用框架net部分有一个必要条件:实体需要实现IModel接口
//        支持多个baseUrl，并可对每个url配置自己的provider
//        可配置公共provider
//        对一个网络请求的基本配置，一个baseUrl对应一个provider，多个baseUrl也可对应一个公共的provider.

public interface NetConfigProvider {
    //配置拦截器
    Interceptor[] interceptors();

    //配置通用请求handler
    RequestInterceptorHandler configHandler();

    //连接超时时间
    long configConnectTimeoutMills();

    //响应超时时间
    long configReadTimeoutMills();

    //日志开关
    boolean configLogEnable();
}
