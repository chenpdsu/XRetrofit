package com.msy.xretrofit;


import com.msy.xretrofit.interfaces.IModel;
import com.msy.xretrofit.interfaces.NetConfigProvider;
import com.msy.xretrofit.interfaces.RequestInterceptorHandler;

import org.reactivestreams.Publisher;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class XRetrofit {
    private static NetConfigProvider sProvider = null;

    private Map<String, NetConfigProvider> providerMap = new HashMap<>();
    private Map<String, Retrofit> retrofitMap = new HashMap<>();
    private Map<String, OkHttpClient> clientMap = new HashMap<>();

    public static final long connectTimeoutMills = 10 * 1000L;
    public static final long readTimeoutMills = 10 * 1000L;

    //私有构造
    private XRetrofit() {
    }

    //静态内部类获取单例
    public static XRetrofit getInstance() {
        return SingletonHolder.sXRetrofit;
    }


    /**
     * 内部类
     */
    private static class SingletonHolder {
        private static XRetrofit sXRetrofit = new XRetrofit();
    }

    public static <S> S get(String baseUrl, Class<S> service) {
        return getInstance().getRetrofit(baseUrl).create(service);
    }

    public static void registerProvider(NetConfigProvider provider) {
        sProvider = provider;
    }

    public static void registerProvider(String baseUrl, NetConfigProvider provider) {
        getInstance().providerMap.put(baseUrl, provider);
    }

    /**
     * 默认使用rxjava
     *
     * @param baseUrl
     * @return
     */
    public Retrofit getRetrofit(String baseUrl) {
        return getRetrofit(baseUrl, null, true);
    }

    /**
     * 必须默认手动注册
     *
     * @param baseUrl
     * @param useRx
     * @return
     */
    public Retrofit getRetrofit(String baseUrl, boolean useRx) {
        return getRetrofit(baseUrl, null, useRx);
    }


    public Retrofit getRetrofit(String baseUrl, NetConfigProvider configProvider, boolean useRx) {
        if (baseUrl == null || baseUrl.length() == 0) {
            throw new IllegalStateException("base url can not be null!");
        }

        if (retrofitMap.get(baseUrl) != null) {
            return retrofitMap.get(baseUrl);
        }

        //添加一个默认的配置 否则抛出异常
        if (configProvider == null) {
            configProvider = providerMap.get(baseUrl);
            if (configProvider == null) {
                configProvider = sProvider;
            }
        }

        checkProvider(configProvider);

        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(baseUrl);
        OkHttpClient okHttpClient = getClient(baseUrl, configProvider);
        builder.client(okHttpClient);
        builder.addConverterFactory(GsonConverterFactory.create());

        if (useRx) {
            builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        }

        Retrofit retrofit = builder.build();
        retrofitMap.put(baseUrl, retrofit);
        providerMap.put(baseUrl, configProvider);
        clientMap.put(baseUrl, okHttpClient);
        return retrofit;
    }

    private OkHttpClient getClient(String baseUrl, NetConfigProvider provider) {
        if (baseUrl == null || baseUrl.length() == 0) {
            throw new IllegalArgumentException("base url can not be null!");
        }
        if (clientMap.get(baseUrl) != null) {
            return clientMap.get(baseUrl);
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(provider.configConnectTimeoutMills() == 0 ? connectTimeoutMills : provider.configConnectTimeoutMills(), TimeUnit.MILLISECONDS);
        builder.readTimeout(provider.configReadTimeoutMills() == 0 ? readTimeoutMills : provider.configReadTimeoutMills(), TimeUnit.MILLISECONDS);

        //添加通用拦截器
        RequestInterceptorHandler requestInterceptorHandler = provider.configHandler();
        if (requestInterceptorHandler != null) {
            builder.addInterceptor(new XInterceptor(requestInterceptorHandler));
        }

        //配置每个主机对应的拦截器 每个host对应的拦截器数目不定
        Interceptor[] interceptors = provider.interceptors();
        if (interceptors != null && interceptors.length != 0) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }

        //添加日志拦截器
        if (provider.configLogEnable()) {
            LogInterceptor logInterceptor = new LogInterceptor();
            builder.addInterceptor(logInterceptor);
        }
        OkHttpClient client = builder.build();
        return client;
    }

    private void checkProvider(NetConfigProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("must register provider first");
        }
    }

    //清除缓存
    public static void clearCache() {
        getInstance().retrofitMap.clear();
        getInstance().clientMap.clear();
    }


    /**
     * 线程切换
     *
     * @param <T>
     * @return
     */
    public static <T> FlowableTransformer<T, T> getScheduler() {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

}
