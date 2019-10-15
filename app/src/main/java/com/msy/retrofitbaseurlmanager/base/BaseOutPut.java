package com.msy.retrofitbaseurlmanager.base;


import com.msy.xretrofit.interfaces.IModel;

public class BaseOutPut<T> implements IModel {
    private T data;
    private int errorCode;
    private String errorMsg;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public boolean isError() {
        if (getErrorCode() != 0) {
            return true;
        }
        return false;
    }

    @Override
    public String getErrorMessage() {
        return getErrorMsg();
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
