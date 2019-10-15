# XRetrofit
 Retrofit 同时支持多个 BaseUrl ，为每个BaseUrl配置不同的拦截器，每个BaseUr配置多个拦截器。
 
## Notice

 - 支持多个baseUrl，并可对每个url配置自己的provider(header,拦截器，连接时间，响应时间等等)
 - 可配置公共provider
 - 对一个网络请求的基本配置，一个baseUrl对应一个provider，多个baseUrl也可对应一个公共的provider.


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
