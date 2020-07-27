package com.connectiontech.demo.entity;

import com.connectiontech.demo.common.ClientExceptionConstants;
import com.connectiontech.demo.common.ResultCode;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 响应信息主体
 * 参照mybatis-plus R类
 * @param <T>
 * @author
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class R<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	@Getter
	@Setter
    // 返回标记：成功=0，失败=1
	private int code;

	@Getter
	@Setter
	private String msg;


	@Getter
	@Setter
	private T data;

	public Boolean isOk() {
		if(code!= 0){
			return false;
		}else{
			return true;
		}
	}

	public static <T> R<T> ok() {
		return restResult(null, ResultCode.SUCCESS, ClientExceptionConstants.SUCCESS);
	}

	public static <T> R<T> ok(T data) {
		return restResult(data,  ResultCode.SUCCESS, ClientExceptionConstants.SUCCESS);
	}

	public static <T> R<T> ok(T data, String msg) {
		return restResult(data, ResultCode.SUCCESS, msg);
	}

	public static <T> R<T> failed() {
		return restResult(null, ResultCode.FAIL, null);
	}

	public static <T> R<T> failed(String msg) {
		return restResult(null, ResultCode.FAIL, msg);
	}

	public static <T> R<T> failed(T data) {
		return restResult(data, ResultCode.FAIL, null);
	}

	public static <T> R<T> failed(T data, String msg) {
		return restResult(data, ResultCode.FAIL, msg);
	}

	public static <T> R<T> failed(T data, ResultCode resultCode, String msg) {
		return restResult(data, resultCode, msg);
	}

	public static <T> R<T> failed(ResultCode code, String msg) {
		return restResult(null, code, msg);
	}

	private static <T> R<T> restResult(T data, ResultCode resultCode,String msg) {
		R<T> apiResult = new R<>();
		apiResult.setCode(resultCode.getCode());
		apiResult.setData(data);
		apiResult.setMsg(msg);
		return apiResult;
	}
}