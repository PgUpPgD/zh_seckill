package com.zh.zh_seckill.vo;

import lombok.Data;

@Data
public class R {
    private int code;
    private String msg;
    private Object data;

    public R(){}

    public R(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static R ok(){ return new R(0, "ok", null); }
    public static R ok(String msg){ return new R(0, msg, null); }
    public static R ok(Object data){
        return new R(0, "ok", data);
    }

    public static R error(){
        return new R(1, "error", null);
    }
    public static R error(String msg){
        return new R(1, msg, null);
    }


}
