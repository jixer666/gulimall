package com.ljx.auth.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ljx.auth.constant.OAuth2Constants;
import com.ljx.auth.domain.vo.*;
import com.ljx.auth.feign.MemberServiceFeign;
import com.ljx.auth.feign.SmsServiceFeign;
import com.ljx.auth.service.LoginService;
import com.ljx.common.constant.CacheConstant;
import com.ljx.common.exception.RRException;
import com.ljx.common.utils.AssertUtil;
import com.ljx.common.utils.HttpUtils;
import com.ljx.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SmsServiceFeign smsServiceFeign;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MemberServiceFeign memberServiceFeign;

    @Value("${github.callback}")
    private String githubCallback;

    @Value("${github.client.secret}")
    private String githubClientSecret;

    @Value("${github.client.id}")
    private String githubClientId;


    @Override
    public void sendSms(String phone) {
        AssertUtil.isNotEmpty(phone, "电话不能为空");

        String smsCacheKey = CacheConstant.SMS_PHONE + phone;
        String smsVal = stringRedisTemplate.opsForValue().get(smsCacheKey);
        // 验证码防刷
        // 优化点：可用用uuid来代替phone
        if (StringUtils.isNotEmpty(smsVal)) {
            String putTime = smsVal.split("_")[1];
            if (System.currentTimeMillis() - Long.parseLong(putTime) < 60 * 1000) {
                throw new RRException("一分钟请勿重复发送验证码");
            }
        }

        String code = RandomUtil.randomNumbers(6);

        smsServiceFeign.sendCode(phone, code);

        stringRedisTemplate.opsForValue().set(smsCacheKey, code + "_" + System.currentTimeMillis(), CacheConstant.SMS_PHONE_EXPIRE, TimeUnit.MINUTES);
    }

    @Override
    public void register(RegisterVO registerVO) {
        registerVO.checkParams();
        checkPhoneCode(registerVO.getPhone(), registerVO.getCode());

        memberServiceFeign.register(registerVO);
    }

    private void checkPhoneCode(String phone, String code) {
        String smsCacheKey = CacheConstant.SMS_PHONE + phone;
        String smsVal = stringRedisTemplate.opsForValue().get(smsCacheKey);
        AssertUtil.isNotEmpty(smsVal, "验证码已失效");

        String trueCode = smsVal.split("_")[0];
        AssertUtil.isTrue(code.equalsIgnoreCase(trueCode), "验证码错误");
    }

    @Override
    public R login(LoginVO loginVO) {
        loginVO.checkParams();

        R loginR = memberServiceFeign.login(loginVO);
        if (StringUtils.isEmpty(loginR.getErrorMsg())) {
            // todo
        }

        return loginR;
    }

    @Override
    public String githubCallback(String code, String state) {
        AssertUtil.isNotEmpty(code, "code不能为空");
        AssertUtil.isNotEmpty(state, "state不能为空");

        try {
            Map<String, String> reqTokenMap = buildAccessTokenMap(code);

            HashMap<String, String> headers = new HashMap<>();
            headers.put("Accept", "application/json");

            HttpResponse response = HttpUtils.doPost(OAuth2Constants.GITHUB_HOST_URL, OAuth2Constants.GITHUB_ACCESS_TOKEN_PATH_URL,
                    OAuth2Constants.POST, headers, reqTokenMap, new HashMap<>());

            if (response.getStatusLine().getStatusCode() == 200) {
                String json = EntityUtils.toString(response.getEntity());
                AccessTokenVO accessTokenVO = JSONUtil.toBean(json, AccessTokenVO.class);

                headers.put("Authorization", "token " + accessTokenVO.getAccess_token());
                response = HttpUtils.doGet(OAuth2Constants.GITHUB_HOST_API_URL, OAuth2Constants.GITHUB_USER_INFO_PATH_URL,
                        OAuth2Constants.GET, headers, new HashMap<>());
                json = EntityUtils.toString(response.getEntity());
                GithubUserVO githubUserVO = JSONUtil.toBean(json, GithubUserVO.class);

                R<MemberVO> result = memberServiceFeign.githubLogin(githubUserVO);
                if (result.getCode() == 0) {
                    MemberVO memberVO = result.getDataObj(MemberVO.class);
                    log.info("登陆成功，用户：{}", memberVO.getUsername());

                    return "redirect:http://gulimall.com";
                } else {
                    return "redirect:http://auth.gulimall.com/login.html";
                }

            } else {
                return "redirect:http://auth.gulimall.com/login.html";
            }
        } catch (Exception e) {
            log.error("调用Github出错：", e.getMessage(), e);
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

    private Map<String, String> buildAccessTokenMap(String code) {
        Map<String, String> reqMap = new HashMap<>();
        reqMap.put("code", code);
        reqMap.put("redirect_uri", githubCallback);
        reqMap.put("client_id", githubClientId);
        reqMap.put("client_secret", githubClientSecret);
        return reqMap;
    }

}
