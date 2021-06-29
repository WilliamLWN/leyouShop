package com.leyou;

import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.auth.pojo.Payload;
import com.leyou.common.auth.utils.RsaUtils;
import com.leyou.common.auth.pojo.UserInfo;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {
    private final String pubKeyPath = "D:\\software\\rsa_key\\rsa-key.pub";
    private final String priKeyPath = "D:\\software\\rsa_key\\rsa-key";


    @Test
    public void testEncode() throws Exception {
        UserInfo userInfo = new UserInfo(1L,"jack","admin");

        PrivateKey privateKey = RsaUtils.getPrivateKey(priKeyPath);

        String token = JwtUtils.generateTokenExpireInMinutes(userInfo, privateKey, 30);

        System.out.println(token);
    }

    //eyJhbGciOiJSUzI1NiJ9.eyJ1c2VyIjoie1wiaWRcIjoxLFwidXNlcm5hbWVcIjpcImphY2tcIixcInJvbGVcIjpcImFkbWluXCJ9IiwianRpIjoiTlRBMk5UUTBPREV0WlROa1pDMDBOR1ptTFRnNVpEVXRPREl4Wm1Zd1pqZzVNMkptIiwiZXhwIjoxNjE3OTM5NzU2fQ.BzviRZurZgmHIRn9tRlvUlbxX3AHZL6EIeQziGiJ4uQlGyhBFmmVRofOWqOxprNebJJwW-5zrJ21LhATeXhL2Ke4kj3c7oMd-7BGgC1y8DhJxaF0jTM5K5oiUaEYG86q26yuSEd21vydva9XVpXbOixfRFwZMy7VXfzT54DAa8AcGGIvTNO3IuNzApCXC4DcC-AlnrArjJdoiWWnyWsRPzoSI5tRuuIYFAE6AK4HKz4_6mQF0OwWeDt9qRWTm1ne_LuUDoZ1bw8wrk-lu_mChBqLlkg5DEd4ZCBQ2IxxYcKgjc7PmSyJcrlvut1skwiMn9TGcFw-nCH8lGXZgwu2hg
    //eyJhbGciOiJSUzI1NiJ9.eyJ1c2VyIjoie1wiaWRcIjoxLFwidXNlcm5hbWVcIjpcImphY2tcIixcInJvbGVcIjpcImFkbWluXCJ9IiwianRpIjoiWWpJMU4yTXlOREF0T0dJeU5TMDBOakZsTFRobE4yVXRaVGRrWVRJd09Ua3pOell6IiwiZXhwIjoxNjIzNTY5ODUxfQ.TGdP6xidfCb44ieLEHJ61ik9XNjHAQO0L2PivKcO8qTnHdjaBtpdh4FtXAcmytkz55H3ubHslRi5vsyo0swE9fAPBMfF2xIna81IQ9yabvyUqT96paQqycp5iqovnoHVhbCc495uELAr8qOJS4krxGZoEjGXqOGLIAeoiUXu0sOpRKxQM2ypHuZZqBaIvBhuGQhkr523MQvUD_H1buBJgPClItNu-gC0rWjhLmqYXcL1Jn1cqLdtkOUNcS6WmZOzaa2bN6_vkKZcxP5LfP3SafLZqDG3vnYvUVIrEo4NO_8f268lWyjVm31YBQfRijiuDx0v5PpsNnBnzJ2WI3NL8A
    @Test
    public void testParse() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJ1c2VyIjoie1wiaWRcIjoxLFwidXNlcm5hbWVcIjpcImphY2tcIixcInJvbGVcIjpcImFkbWluXCJ9IiwianRpIjoiWWpJMU4yTXlOREF0T0dJeU5TMDBOakZsTFRobE4yVXRaVGRrWVRJd09Ua3pOell6IiwiZXhwIjoxNjIzNTY5ODUxfQ.TGdP6xidfCb44ieLEHJ61ik9XNjHAQO0L2PivKcO8qTnHdjaBtpdh4FtXAcmytkz55H3ubHslRi5vsyo0swE9fAPBMfF2xIna81IQ9yabvyUqT96paQqycp5iqovnoHVhbCc495uELAr8qOJS4krxGZoEjGXqOGLIAeoiUXu0sOpRKxQM2ypHuZZqBaIvBhuGQhkr523MQvUD_H1buBJgPClItNu-gC0rWjhLmqYXcL1Jn1cqLdtkOUNcS6WmZOzaa2bN6_vkKZcxP5LfP3SafLZqDG3vnYvUVIrEo4NO_8f268lWyjVm31YBQfRijiuDx0v5PpsNnBnzJ2WI3NL8A";

        PublicKey publicKey = RsaUtils.getPublicKey(pubKeyPath);

        try {
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, publicKey, UserInfo.class);

            //取出用户信息
            UserInfo userInfo = payload.getInfo();

            System.out.println(userInfo);
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("验证失败");
        }


    }

}

