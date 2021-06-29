package com.leyou.auth.service;

import com.leyou.auth.config.JwtProperties;
import com.leyou.common.auth.pojo.Payload;
import com.leyou.common.auth.pojo.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.constants.LyConstants;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.user.client.UserClient;
import com.leyou.user.pojo.User;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 授权业务
 */
@Service
public class AuthService {
    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties jwtProps;
    @Autowired
    private RedisTemplate redisTemplate;


    public void login(String username, String password, HttpServletResponse response) {
        //1. 判断用户名和密码是否正确
        User loginUser = userClient.query(username, password);

        UserInfo userInfo = new UserInfo(loginUser.getId(),loginUser.getUsername(),"admin");

        //2.生成token，并把token写出到respone中
        this.generateTokenAndWriteCookie(userInfo,response);
    }

    /**
     * 生成token，并把token写出到respone中
     */
    public void generateTokenAndWriteCookie(UserInfo info,HttpServletResponse response){
        //2.利用JwtUtils+私钥生成加密token
        String token = JwtUtils.generateTokenExpireInMinutes(info, jwtProps.getPrivateKey(), jwtProps.getCookie().getExpire());

        //3）把token写入Cookie对象中
        CookieUtils.newCookieBuilder()
                .response(response)
                .name(jwtProps.getCookie().getCookieName())
                .value(token)
                .domain(jwtProps.getCookie().getCookieDomain())
                .build();
    }

    public UserInfo verify(HttpServletRequest request,HttpServletResponse response) {
        //1.从request取出cookie的token
        String token = CookieUtils.getCookieValue(request, jwtProps.getCookie().getCookieName());

        //2. 验证token是否合法
        Payload<UserInfo> payload = null;

        try {
            payload = JwtUtils.getInfoFromToken(token, jwtProps.getPublicKey(), UserInfo.class);
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }

        if(redisTemplate.hasKey(payload.getId())) {
            //如果redis有当前token的id，就认为当前token已经失效了
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }

        //3. 取出token中的载荷UserInfo数据
        UserInfo info = payload.getInfo();


        //刷新token过期时间
        //1.取出当前token的过期时间
        Date expireTime = payload.getExpiration();

        //2.计算刷新时间=过期时间-15
        DateTime refreshTime = new DateTime(expireTime).minusMinutes(jwtProps.getCookie().getRefreshTime());

        //3.判断刷新时间<当前时间，重新生成token
        //isBeforeNow(): 判断指定时间是否在当前时间之前
        if(refreshTime.isBeforeNow()){

            //在重新生成token之前，把旧的token也存入黑名单
            addToBackList(payload);

            //重新生成token
            generateTokenAndWriteCookie(info,response);
        }

        //4. 返回
        return payload.getInfo();
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        //1.取出Cookie里面的token
        String token = CookieUtils.getCookieValue(request,jwtProps.getCookie().getCookieName());

        //2.校验token，取出载荷
        Payload<UserInfo> payload = null;
        try {
            payload = JwtUtils.getInfoFromToken(token, jwtProps.getPublicKey(), UserInfo.class);
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }

        //3. 把token加入黑名单
        addToBackList(payload);

        //4.把浏览器的Cookie删除
        CookieUtils.deleteCookie(
                jwtProps.getCookie().getCookieName(),
                jwtProps.getCookie().getCookieDomain(),
                response
        );

    }

    /**
     * 把token加入黑名单
     * @param payload
     */
    public void addToBackList(Payload<UserInfo> payload) {
        //3.把token的id加入redis作为黑名
        String tokenId = payload.getId();
        //获取过期时间
        Date expiration = payload.getExpiration();
        //计算剩余有效时间
        long remainTime = expiration.getTime() - System.currentTimeMillis();
        //把token的id存入redis
        redisTemplate.opsForValue().set(LyConstants.BACL_KEY_PRE+tokenId,"1",remainTime, TimeUnit.MILLISECONDS);
    }
}

