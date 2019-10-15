package com.msy.retrofitbaseurlmanager;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.msy.xretrofit.NetError;
import com.msy.xretrofit.interfaces.IModel;

import org.json.JSONException;

import java.net.UnknownHostException;

import io.reactivex.subscribers.ResourceSubscriber;


public abstract class ApiSubscriber<T extends IModel> extends ResourceSubscriber<T> {


    @Override
    public void onError(Throwable e) {
        NetError error = null;
        if (e != null) {
            if (!(e instanceof NetError)) {
                if (e instanceof UnknownHostException) {
                    error = new NetError(e, NetError.ErrorType.NoConnectError);
                } else if (e instanceof JSONException|| e instanceof JsonParseException|| e instanceof JsonSyntaxException) {
                    error = new NetError(e, NetError.ErrorType.ParseError);
                } else {
                    error = new NetError(e, NetError.ErrorType.OtherError);
                }
            } else {
                error = (NetError) e;
            }
            onFail(error);
        }

    }

    protected abstract void onFail(NetError error);

    @Override
    public void onComplete() {

    }

}
