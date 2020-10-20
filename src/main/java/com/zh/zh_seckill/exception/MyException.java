package com.zh.zh_seckill.exception;

import lombok.Data;

@Data
public class MyException extends RuntimeException {

    private String msg;
    private Integer code;

    public MyException(){}
    public MyException(Integer code, String msg){
        super();
        this.code = code;
        this.msg = msg;
    }

}
