package com.msy.xretrofit;


public class NetError extends Exception {
    private Throwable exception;
    private ErrorType type = ErrorType.NoConnectError;

    public enum ErrorType {
        ParseError(0, "数据解析异常"), NoConnectError(1, "网络异常,请稍后再试"), ServerError(2, "服务器繁忙"),
        NoDataError(3, "暂无数据"), OtherError(5, "未知错误");

        /**
         * 类型
         */
        private int type;
        /**
         * 类型描述
         */
        private String desc;

        ErrorType(int type, String desc) {
            this.type = type;
            this.desc = desc;
        }

        public int getType() {
            return type;
        }

        public String getDesc() {
            return desc;
        }
    }

    public NetError(Throwable exception, ErrorType type) {
        this.exception = exception;
        this.type = type;
    }

    public NetError(String detailMessage, ErrorType type) {
        super(detailMessage);
        this.type = type;
    }

    //获取错误提示使用error.getType().getDesc() 此方法仅做调试用
    @Deprecated
    @Override
    public String getMessage() {
        if (exception != null) {
            return exception.getMessage();
        }
        return super.getMessage();
    }

    public ErrorType getType() {
        return type;
    }
}
