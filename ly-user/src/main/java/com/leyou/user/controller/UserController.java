package com.leyou.user.controller;

import com.leyou.common.exception.pojo.LyException;
import com.leyou.user.dto.AddressDTO;
import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

/**
 * 用户
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 校验用户名和手机号唯一性
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(
            @PathVariable("data") String data,
            @PathVariable("type") Integer type
    ){
        Boolean isCanUse = userService.checkData(data,type);
        return ResponseEntity.ok(isCanUse);
    }

    /**
     * 发送短信验证码
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendVerifyCode(
            @RequestParam("phone") String phone
    ){
        userService.sendVerifyCode(phone);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 用户注册
     * BindingResult: 封装了所有字段验证失败的错误信息
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user, BindingResult result, @RequestParam("code") String code){
        if(result.hasErrors()){
            //取出错误信息
            String errorMsg = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("|"));

            //自行封装异常信息
            throw new LyException(500,errorMsg);
        }

        userService.register(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 校验用户名和密码是否正确
     */
    @GetMapping("/query")
    public ResponseEntity<User> query(
            @RequestParam("username") String username,
            @RequestParam("password") String password){
        User loginUser = userService.query(username,password);
        return ResponseEntity.ok(loginUser);
    }

    /**
     * 根据地址ID查询用户的收货地址
     */
    @GetMapping("/address")
    public ResponseEntity<AddressDTO> findAddressById(
            @RequestParam("userId") Long userId,
            @RequestParam("id") Long id
    ){
        AddressDTO addressDTO = userService.findAddressById(userId,id);
        return ResponseEntity.ok(addressDTO);
    }

}


