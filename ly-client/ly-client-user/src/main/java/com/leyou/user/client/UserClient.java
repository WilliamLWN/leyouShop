package com.leyou.user.client;

import com.leyou.user.dto.AddressDTO;
import com.leyou.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserClient {

    /**
     * 校验用户名和密码是否正确
     */
    @GetMapping("/query")
    public User query(
            @RequestParam("username") String username,
            @RequestParam("password") String password);

    /**
     * 查询指定用户的指定收货地址
     */
    @GetMapping("/address")
    public AddressDTO findAddressById(@RequestParam("userId") Long userId, @RequestParam("id") Long id);
}

