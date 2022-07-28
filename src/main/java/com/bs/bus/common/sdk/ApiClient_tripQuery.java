package com.bs.bus.common.sdk;

import java.util.*;

import com.bs.bus.entity.Temp;
import com.bs.bus.entity.Tran;
import http.HttpInvoker;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ApiClient_tripQuery {
	

	private  HttpInvoker httpInvoker; 
	//验证方式 apicode或appkey 默认apicode
	private String authoration = "apicode";
	
	//测试api地址
	private String testUrl = "https://api.yonyoucloud.com/apis/dst/ncov/tripQuery";
	//请求方法类型
	private String methodType = "GET";
	//线程池参数文件路
	private static final String propertyUrl = "src/main/resources/HttpClient.properties";
	
	public ApiClient_tripQuery() throws Exception{
		httpInvoker = new HttpInvoker(propertyUrl);
	}
	
	public List<Tran> searchTran(String area, String backdate, String tranno){
		Map<String,Object> resultmap=new HashMap<>();
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("arrive", area);
		params.put("date", backdate);
		params.put("no", tranno);
		params.put("num",30);

	
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

		String result = httpInvoker.invoker(testUrl, params, methodType, header);
		JSONObject jsonObject=JSONObject.fromObject(result);
		JSONArray jsonArray=jsonObject.getJSONArray("newslist");
		List<Tran> nolist=new ArrayList<>();
		for (int i=0;i<jsonArray.size();i++){
			Tran tran=new Tran();
			tran.setTrainno(jsonArray.getJSONObject(i).getString("no"));
			tran.setArrdate(jsonArray.getJSONObject(i).getString("date"));
			nolist.add(tran);
		}
		return nolist;
	}
	
	//关闭线程
	public void destoy(){
		httpInvoker.destoy();
	}
	
	public static void main(String[] args){
		ApiClient_tripQuery apiClient;
		try {
			apiClient = new ApiClient_tripQuery();
			System.out.println(apiClient.searchTran("唐山","2020-02-12","CZ3842"));
			apiClient.destoy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}	
}