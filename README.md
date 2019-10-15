# XRetrofit
 Retrofit 同时支持多个 BaseUrl ，为每个BaseUrl配置不同的拦截器，每个BaseUr配置多个拦截器。
 
## Notice

 - 支持多个baseUrl，并可对每个url配置自己的provider(header,拦截器，连接时间，响应时间等等)
 - 可配置公共provider
 - 对一个网络请求的基本配置，一个baseUrl对应一个provider，多个baseUrl也可对应一个公共的provider.

## 核心代码
```java
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

```

## Download

root build.gradle
 ```java
 allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://raw.githubusercontent.com/chenpdsu/XRetrofit/master" }
        
    }
}
```
app build.gradle
```java
 api 'com.chenxy.xretrofit:xretrofit:1.0.1'
```

## Usage
step 1:针对不同的baseurl生成不同的retrofit对象

``` java
 private static GithubService sGithubService;

    public static GithubService getGithubService() {
        if (sGithubService == null) {
            synchronized (ServiceCenter.class) {
                if (sGithubService == null) {
                    sGithubService = XRetrofit.getInstance().getRetrofit(BaseUrlConfig.baseUrl_GitHub, true).create(GithubService.class);
                }
            }
        }
        return sGithubService;
    }

    private static WanAndroidService sWanAndroidService;

    public static WanAndroidService getWanAndroidService() {
        if (sWanAndroidService == null) {
            synchronized (ServiceCenter.class) {
                if (sWanAndroidService == null) {
                    sWanAndroidService = XRetrofit.getInstance().getRetrofit(BaseUrlConfig.baseUrl_WanAndroid, true).create(WanAndroidService.class);
                }
            }
        }
        return sWanAndroidService;
    }
```

step2:注册provider
```java
 XRetrofit.registerProvider(new BaseNetConfigProvider());
        XRetrofit.registerProvider(BaseUrlConfig.baseUrl_GitHub, new GitHubNetConfigProvider());
```

step3:访问网络
``` java
  private void testGithub() {
        GitHubModel gitHubModel = new GitHubModel();

        gitHubModel.getEthBalance("chenxy")
                .compose(this.<ResponseBody>bindToLifecycle())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        try {
                            String string = responseBody.string();
                            Log.d("test", string);
                            showResult(string);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


```
详情可参考demo
