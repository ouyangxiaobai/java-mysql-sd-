package com.bs.bus.common.sdk;

import java.util.HashMap;
import java.util.Map;
import http.HttpInvoker;

public class ApiClient_chatrobot {
	

	private  HttpInvoker httpInvoker; 
	//验证方式 apicode或appkey 默认apicode
	private String authoration = "apicode";
	
	//测试api地址
	private String testUrl = "https://api.yonyoucloud.com/apis/dst/ncov/chatrobot";
	//请求方法类型
	private String methodType = "POST";
	//线程池参数文件路
	private static final String propertyUrl = "******************";
	
	public ApiClient_chatrobot() throws Exception{
		httpInvoker = new HttpInvoker(propertyUrl);
	}
	
	public void test(){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("appcode", "******"); 
		params.put("deviceid", "******"); 
		params.put("msg", "******"); 
		params.put("msgtype", "******"); 
		params.put("sessionid", "******"); 
		params.put("tenantid", "******"); 
		params.put("token", "******"); 
		params.put("userid", "******"); 
	
		Map<String,String> header = new HashMap<String,String>();
		header.put("authoration", authoration);
		if(authoration.equals("apicode"))
		{//验证方式为apicode时需要添加apicode
			header.put("apicode","******");	
		}else
		{//验证方式为appkey时需要添加appkey值和appsecret
			header.put("appkey","******");
			header.put("appsecret","******");
		}
		header.put("Content-Type", "******"); 

		String result = httpInvoker.invoker(testUrl, params, methodType, header);
		System.out.println(result);
	}
	
	//关闭线程
	public void destoy(){
		httpInvoker.destoy();
	}
	
	public static void main(String[] args){
		ApiClient_chatrobot apiClient;
		try {
			apiClient = new ApiClient_chatrobot();
			apiClient.test();
			apiClient.destoy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}	
}