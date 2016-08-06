package com.wuxing.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtil {
	public static String URL = "http://www.5xclw.cn/ecmobile/";

	/**
	 * httpClient post
	 * 
	 * @param map
	 * @return
	 */
	public static String clientPost(Map<String, String> map) {
		String result = "";
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			// 创建一个Post请求对象
			HttpPost post = new HttpPost(URL);
			// 设置头信息
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			// 将添加的员工数据 放置到正文中 HttpEntity
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			Set<String> set = map.keySet();
			for (String key : set) {
				list.add(new BasicNameValuePair(key, map.get(key)));
			}
			// 通过URLEncodedFormEntity提供自动对正文中数据编码 请求正文 HttpEntity
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,
					"UTF-8");
			post.setEntity(entity);

			// 执行请求
			HttpResponse response = client.execute(post);
			System.out.println(response.getStatusLine().getStatusCode());
			// 通过状态码判断是否请求处理成功
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 获取到响应的正文
				HttpEntity respEntity = response.getEntity();
				// 将响应正文信息转换为字符串
				result = EntityUtils.toString(respEntity, "UTF-8");
				// 打印出响应正文信息
				System.out.println(result);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * httpClient get
	 * 
	 * @param map
	 * @return
	 */
	public static String clietnGet(Map<String, String> map) {
		String result = "";

		try {
			// 创建客户端
			DefaultHttpClient client = new DefaultHttpClient();
			// 请求方式
			String data = checkMap(map);
			String NewUrl = URL + "?" + data;
			Log.i("拼接后", NewUrl);
			// URL url = new URL(NewUrl);
			HttpGet get = new HttpGet(NewUrl);
			// 通过客户端执行请求 返回httpresponse对象中
			HttpResponse response = client.execute(get);
			// httpresponse 包括 状态行，头信息，正文
			// 获取响应吗
			StatusLine s = response.getStatusLine();
			if (s.getStatusCode() == 200) {
				// 获取头信息
				Header[] header = response.getAllHeaders();
				for (Header header2 : header) {
					System.out.println(header2);
				}
				// 获取响应的正文
				HttpEntity responseEntity = response.getEntity();
				// 将正文转化成字符串
				// responseEntity.getContent();获取正文的输入流
				result = EntityUtils.toString(responseEntity, "UTF-8");
				System.out.println(result);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * httpUrlpost
	 * 
	 * @param map
	 * @return
	 */
	public static String urlPost(Map<String, String> map) {
		String result = "";
		try {
			// 创建url
			URL url = new URL(URL);
			// 获取连接
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 设置发送方式
			conn.setRequestMethod("POST");
			//设置编码
			conn.setRequestProperty("Charset", "UTF-8");
			// 设置允许发送
			conn.setDoInput(true);
			conn.setDoOutput(true);
			// 发送体
			String data = checkMap(map);
			
			//设置数据内容的长度
			conn.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
			// 输出流
			OutputStream out = conn.getOutputStream();
			out.write(data.getBytes());
			// 开始请求
			InputStream in = conn.getInputStream();
			// 把输入流转化成为result
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			if (conn.getResponseCode() == 200) {
				while ((line = reader.readLine()) != null) {
					// 把结果拼接起来
					result = result + line;
					Log.i("result", result);

				}
			}else {
				System.out.println(conn.getResponseCode());
				
			}
			
			reader.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * httpurl get
	 * 
	 * @param map
	 * @return
	 */
	public static String urlGet(Map<String, String> map) {
		String result = "";
		try {
			// 拼接url
			String data = checkMap(map);
			String NewUrl = URL + "?" + data;
			Log.i("拼接后", NewUrl);
			// 新建url
			URL url = new URL(NewUrl);
			// 打开urlconnect
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 获取输入流
			InputStream in = conn.getInputStream();
			// 把输入流转化成为result
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			if (conn.getResponseCode()==200) {
				String line = null;
				while ((line = reader.readLine()) != null) {
					// 把结果拼接起来
					result = result + line;
				}
				Log.i("result", result);
			}else {
				System.out.println("urlGet返回码："+""+conn.getResponseCode());
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	private static String checkMap(Map<String, String> map) {
		// TODO Auto-generated method stub
		String str = "";
		Set<String> set = map.keySet();
		for (String key : set) {
			str = str + key + "=" + map.get(key) + "&";
		}

		return str.substring(0, str.length() - 1);
	}
}
