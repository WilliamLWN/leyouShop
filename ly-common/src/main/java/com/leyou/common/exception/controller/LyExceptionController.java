package com.leyou.common.exception.controller;

import com.leyou.common.exception.pojo.ExceptionResult;
import com.leyou.common.exception.pojo.LyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理类
 */
@ControllerAdvice
public class LyExceptionController {

    /**
     * 异常处理方法
     */
    @ExceptionHandler(value = LyException.class) // 捕获LyException异常
    @ResponseBody // 转换为json格式
    public ResponseEntity<ExceptionResult> resolveException(LyException e){
        return ResponseEntity.status(e.getStatus()).body(new ExceptionResult(e));
    }

}
