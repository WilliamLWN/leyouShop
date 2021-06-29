package com.leyou.user.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Size;
import java.util.Date;

@TableName("tb_user")
@Data
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    @Size(min = 4, max = 10, message = "用户名格式不正确")
    private String username;
    @Length(min = 4, max = 10, message = "密码格式不正确")
    private String password;
    private String phone;
    private Date createTime;
    private Date updateTime;
}