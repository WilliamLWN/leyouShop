package com.leyou.common.auth.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 存放JWt的载荷中的登录用户信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private Long id;//用户ID
    private String username;//用户名称
    private String role;//用户角色
}
