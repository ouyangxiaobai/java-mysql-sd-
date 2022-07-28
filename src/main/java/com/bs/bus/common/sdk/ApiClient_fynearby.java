package com.bs.bus.common.sdk;

import java.util.HashMap;
import java.util.Map;
import http.HttpInvoker;

public class ApiClient_fynearby {
	

	private  HttpInvoker httpInvoker; 
	//验证方式 apicode或appkey 默认apicode
	private String authoration = "apicode";
	
	//测试api地址
	private String testUrl = "https://api.yonyoucloud.com/apis/dst/ncov/fynearby";
	//请求方法类型
	private String methodType = "GET";
	//线程池参数文件路
	private static final String propertyUrl = "src/main/resources/HttpClient.properties";
	
	public ApiClient_fynearby() throws Exception{
		httpInvoker = new HttpInvoker(propertyUrl);
	}
	
	public void test(){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("city", "唐山市");
		params.put("district", "曹妃甸区");
		params.put("province", "河北省");
	
		Map<String,String> header = new HashMap<String,String>();
		header.put("authoration", authoration);
		if(authoration.equals("apicode"))
		{//验证方式为apicode时需要添加apicode
			header.put("apicode","91fb6170acfc4e8aa843197933f516c3");
		}else
		{//验证方式为appkey时需要添加appkey值和appsecret
			header.put("appkey","******");
			header.put("appsecret","******");
		}
		header.put("Accept","application/json;charset=UTF-8");

		String result = httpInvoker.invoker(testUrl, params, methodType, header);
		System.out.println(result);
	}
	
	//关闭线程
	public void destoy(){
		httpInvoker.destoy();
	}
	
	public static void main(String[] args){
		ApiClient_fynearby apiClient;
		try {
			apiClient = new ApiClient_fynearby();
			apiClient.test();
			apiClient.destoy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}	
}