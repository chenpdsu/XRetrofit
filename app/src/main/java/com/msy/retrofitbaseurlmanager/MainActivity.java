package com.msy.retrofitbaseurlmanager;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.msy.retrofitbaseurlmanager.base.BaseOutPut;
import com.msy.retrofitbaseurlmanager.base.BaseUrlConfig;
import com.msy.retrofitbaseurlmanager.bean.HotKeyResponse;
import com.msy.retrofitbaseurlmanager.interceptors.GitHubNetConfigProvider;
import com.msy.xretrofit.BaseNetConfigProvider;
import com.msy.xretrofit.NetError;
import com.msy.xretrofit.XRetrofit;
import com.msy.xretrofit.interfaces.IModel;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.reactivestreams.Publisher;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

public class MainActivity extends RxAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XRetrofit.registerProvider(new BaseNetConfigProvider());
        XRetrofit.registerProvider(BaseUrlConfig.baseUrl_GitHub, new GitHubNetConfigProvider());

        findViewById(R.id.btn_wanandroid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testWanAndroid();
            }
        });

        findViewById(R.id.btn_github).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testGithub();
            }
        });


    }

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


    private void testWanAndroid() {
        WanAndroidModel wanAndroidModel = new WanAndroidModel();
        wanAndroidModel.getHotKeys()
                .compose(MainActivity.<BaseOutPut<ArrayList<HotKeyResponse>>>getApiTransformer())
                .compose(this.<BaseOutPut<ArrayList<HotKeyResponse>>>bindToLifecycle())
                .subscribe(new ApiSubscriber<BaseOutPut<ArrayList<HotKeyResponse>>>() {
                    @Override
                    public void onNext(BaseOutPut<ArrayList<HotKeyResponse>> arrayListBaseOutPut) {
                        Log.d("test", arrayListBaseOutPut.getData().size() + "");
                        showResult("返回数据" + arrayListBaseOutPut.getData().size() + "条");
                    }

                    @Override
                    protected void onFail(NetError error) {

                    }
                });
    }

    private void showResult(String result) {
        new AlertDialog.Builder(this)
                .setMessage(result)
                .setCancelable(true)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }


    public static <T extends IModel> FlowableTransformer<T, T> getApiTransformer() {

        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.flatMap(new Function<T, Publisher<T>>() {
                    @Override
                    public Publisher<T> apply(T model) throws Exception {
                        if (model.isError()) {
                            return Flowable.error(new NetError(model.getErrorMessage(), NetError.ErrorType.ServerError));
                        } else {
                            return Flowable.just(model);
                        }
                    }
                });
            }
        };
    }
}
