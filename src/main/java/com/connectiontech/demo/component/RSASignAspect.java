package com.connectiontech.demo.component;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.connectiontech.demo.annotation.RSASign;
import com.connectiontech.demo.common.ApiConstants;
import com.connectiontech.demo.common.ClientException;
import com.connectiontech.demo.common.ClientExceptionConstants;
import com.connectiontech.demo.entity.RequestBody;
import com.connectiontech.demo.mapper.MchMapper;
import com.connectiontech.demo.pojo.Mch;
import com.connectiontech.demo.util.RSAUitil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author
 * <p>
 * 服务间接口不鉴权处理逻辑
 */
@Aspect
@Component
public class RSASignAspect {

	private final HttpServletRequest request;

	public RSASignAspect(HttpServletRequest request) {
		this.request = request;
	}

	@Autowired
	private MchMapper mchMapper;

    // 过期时间 5分钟
	private static final Long timeOut = 300000L;

	@Autowired
	private RedisTemplate redisTemplate;

	@Around("@annotation(rsaSign)")
	public Object around(ProceedingJoinPoint point, RSASign rsaSign) throws Throwable {
        String str = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining());
        RequestBody body = JSONUtil.toBean(str, RequestBody.class);
        String accessKey = body.getAccessKey();
        if(StringUtils.isBlank(accessKey)) throw new ClientException(ClientExceptionConstants.ILLEGAL_ACCESSKEY);
        Mch mch = mchMapper.selectOne(new LambdaQueryWrapper<Mch>().eq(Mch::getAccessKey, accessKey));
		if(mch == null) throw new ClientException(ClientExceptionConstants.ILLEGAL_ACCESSKEY);
        String publicKey = mch.getPublicKey();
        PublicKey publicKey1 = RSAUitil.getPublicKey(publicKey);
        //解密
        String bodyStr = RSAUitil.decrypt(body.getBody(),publicKey1);
		Map<String,String> map = strToMap(bodyStr);
		//校验时效性
        verifyTimestemp(accessKey, map);
        //校验签名
        verifySign(publicKey1, map);
        Object[] args = point.getArgs();
		args[0] = map;
		return point.proceed(args);
	}

    private void verifySign(PublicKey publicKey1, Map<String, String> map) {
        String sign = map.get("sign");
        //删除签名取得数据，进行验签名
        map.remove("sign");
        String preVerifyBody = RSAUitil.mapToString(map);
        if(!RSAUitil.verify(preVerifyBody, publicKey1, sign)) throw new ClientException(ClientExceptionConstants.SIGN_VERIFY_ERROR);
    }

    private void verifyTimestemp(String accessKey, Map<String, String> map) {
        String timestemp = map.get("timestemp");
        String nonce = map.get("nonce");
        if(StringUtils.isBlank(timestemp)) throw new ClientException(ClientExceptionConstants.TIMESTAMP_ERROR);
        if(StringUtils.isBlank(nonce)) throw new ClientException(ClientExceptionConstants.NONCE_ERROR);
        long now = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long l = now - Long.parseLong(timestemp);
        if(l < 0 || l > timeOut){
            throw new ClientException(ClientExceptionConstants.TIMESTEMP_EXPIRED);
        }
        String nonceCache = (String)redisTemplate.opsForValue().get(ApiConstants.api_nonce + accessKey +":"+ nonce);
        if(StringUtils.isNotBlank(nonceCache)){
            throw new ClientException(ClientExceptionConstants.NONCE_EXIST);
        }
        redisTemplate.opsForValue().set(ApiConstants.api_nonce + accessKey +":"+ nonce,nonce,timeOut, TimeUnit.MILLISECONDS);
    }

    private static Map<String,String>  strToMap(String str){
		Map<String, String> mapRequest = new HashMap<>(16);
		String[] arrSplit = str.split("[&]");
		for (String strSplit : arrSplit) {
			String[] arrSplitEqual = null;
			arrSplitEqual = strSplit.split("[=]");
			//解析出键值
			if (arrSplitEqual.length > 1) {
				//正确解析
				mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
			} else {
				if (arrSplitEqual[0] != "") {
					//只有参数没有值，不加入
					mapRequest.put(arrSplitEqual[0], "");
				}
			}
		}
		return mapRequest;
	}

}
