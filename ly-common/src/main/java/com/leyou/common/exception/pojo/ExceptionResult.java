package com.leyou.common.exception.pojo;

import lombok.Getter;

/**
 * 封装异常结果信息
 */
@Getter
public class ExceptionResult {
    private Integer status;
    private String message;

    public ExceptionResult(LyException e){
        this.status = e.getStatus();
        this.message = e.getMessage();
    }
}
