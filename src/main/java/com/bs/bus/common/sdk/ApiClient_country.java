package com.bs.bus.common.sdk;

import java.beans.Encoder;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bs.bus.common.JsonEncode;
import http.HttpInvoker;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ApiClient_country {
	

	private  HttpInvoker httpInvoker; 
	//验证方式 apicode或appkey 默认apicode
	private String authoration = "apicode";
	
	//测试api地址
	private String testUrl = "https://api.yonyoucloud.com/apis/dst/ncov/country";
	//请求方法类型
	private String methodType = "GET";
	//线程池参数文件路
	private static final String propertyUrl = "src/main/resources/HttpClient.properties";
	
	public ApiClient_country() throws Exception{
		httpInvoker = new HttpInvoker(propertyUrl);
	}
	
	public void test(){
		Map<String,Object> params = new HashMap<String,Object>();
	
		Map<String,String> header = new HashMap<String,String>();
		header.put("authoration", authoration);
		header.put("apicode","91fb6170acfc4e8aa843197933f516c3");
		header.put("Accept","application/json;charset=UTF-8");
		String result = httpInvoker.invoker(testUrl, params, methodType, header);
		System.out.println(result);
	}
	
	//关闭线程
	public void destoy(){
		httpInvoker.destoy();
	}
	
	public static void main(String[] args){
		ApiClient_country apiClient;
		try {
			apiClient = new ApiClient_country();
			apiClient.test();
			apiClient.destoy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}	
}