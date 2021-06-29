package com.leyou.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leyou.common.constants.LyConstants;
import com.leyou.common.constants.MQConstants;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.pojo.SmsData;
import com.leyou.user.config.PasswordConfig;
import com.leyou.user.dto.AddressDTO;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Wrapper;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Boolean checkData(String data, Integer type) {
        //1.构建条件
        User user = new User();

        switch (type){
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
        }

        QueryWrapper<User> queryWrapper = Wrappers.query(user);

        //2.执行查询，返回结果
        return userMapper.selectCount(queryWrapper)==0;
    }

    public void sendVerifyCode(String phone) {
        //1.生成随机验证码
        String code = RandomStringUtils.randomNumeric(6);

//        code = "123456";

        //2.把验证码保存到redis
        redisTemplate.opsForValue().set(LyConstants.REDIS_KEY_PRE+phone,code,5, TimeUnit.MINUTES);

        //3.把手机号和验证码发送给MQ
        //1）创建对象
        SmsData smsData = new SmsData();
        smsData.setPhone(phone);
        smsData.setCode(code);

        //2)发送消息
        rocketMQTemplate.convertAndSend(MQConstants.Topic.SMS_TOPIC_NAME,smsData);
    }

    public void register(User user, String code) {
        //1.校验验证码是否存在和正确
        String redisCode = redisTemplate.opsForValue().get(LyConstants.REDIS_KEY_PRE + user.getPhone());
        if(redisCode==null || !redisCode.equals(code)){
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }

        try {
            //2.对密码进行加盐加密
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            //3.保存用户数据
            userMapper.insert(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    public User query(String username, String password) {
        //1.判断用户名是否存在
        User user = new User();
        user.setUsername(username);
        QueryWrapper<User> queryWrapper = Wrappers.query(user);
        User loginUser = userMapper.selectOne(queryWrapper);

        if(loginUser==null){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }

        //2.判断密码是否正确
        if(!passwordEncoder.matches(password,loginUser.getPassword())){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        return loginUser;
    }

    public AddressDTO findAddressById(Long userId, Long id) {
        AddressDTO address = new AddressDTO();
        address.setId(1L);
        address.setStreet("珠吉路58号津安创业园一层黑马程序员");
        address.setCity("广州");
        address.setDistrict("天河区");
        address.setAddressee("小飞飞");
        address.setPhone("15800000000");
        address.setProvince("广东");
        address.setPostcode("510000");
        address.setIsDefault(true);
        return address;
    }
}

