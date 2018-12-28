package com.youedata.util;

import com.youedata.util.cache.CacheManager;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.apache.catalina.manager.Constants.CHARSET;

/**
 * httpclinet 工具
 */
public class HttpHelper {

	private static final String CHARSET_UTF8 = "UTF-8";
	private static final String CHARSET_GBK = "GBK";

	// cache开关，true则开启自身缓存
	private static CloseableHttpClient httpClient;
	private static HttpClient httpsClient = createHttpClient();
	private boolean cacheswitch = false;
	// 懒汉式单例
	private static HttpHelper instance = new HttpHelper();
	private CacheManager cacheManager = CacheManager.getInstance();
	Header[] cookieheaders = new Header[] {};

	public CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	// 私有构造函数，单例
	private HttpHelper() {

		httpClient = getCloseableHttpClient();
//		httpClient = (CloseableHttpClient)createHttpClient();
	}

	// 缓存是否打开
	public boolean isCacheing() {
		return cacheswitch;
	}

	// 打开缓存
	public void openCache() {
		cacheswitch = true;
	}

	// 关闭缓存
	public void stopCache() {
		cacheswitch = false;
	}

	// 对外开放的获取类的单独实例的接口
	public static HttpHelper getHelper() {
		return instance;
	}

	// 设置cache保存时间，超时则刷新
	public void setCacheAlivetime(long sec) {
		if (cacheswitch) {
			cacheManager.setAliveTime(sec);
		}
	}

	/**
	 * 根据传入参数设置cookie
	 * 
	 */
	public void getCookie(String url, Map<String, String> paramsMap,
			String charset) throws IOException {

		if (url == null || url.isEmpty()) {
			return;
		}
		// 如果传入编码则使用传入的编码，否则utf8
		charset = (charset == null ? CHARSET_UTF8 : charset);
		// 将map转成List<NameValuePair>
		List<NameValuePair> params = getParamsList(paramsMap);
		UrlEncodedFormEntity entity = null;
		HttpPost post = null;
		CloseableHttpResponse response = null;

		try {
			entity = new UrlEncodedFormEntity(params, charset);
			post = new HttpPost(url);
			// 设置post的参数
			post.setEntity(entity);
			response = httpClient.execute(post);
			// 保存response的名称为Set-Cookie的headers
			cookieheaders = response.getHeaders("Set-Cookie");
			// cookie = set_cookie.substring(0, set_cookie.indexOf(";"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return;
	}

	public void setCookies(Header[] headers) {
		cookieheaders = headers;
	}

	/**
	 * get方法，参数需自己构建到url中，如果需要cookie则用getcookie方法设置
	 * 
	 * @param url
	 *            地址
	 * @param charset
	 *            默认utf8，可为空
	 * @return
	 * @throws IOException
	 */
	public String get(String url, String charset){
		if (url == null || url.isEmpty()) {
			return null;
		}
		// 如果缓存中有，则直接取出并返回
		if (cacheswitch == true) {
			Object cacheObj = cacheManager.get(url);
			if (cacheObj != null) {
				return (String) cacheObj;
			}
		}

		charset = (charset == null || "".equals(charset) ? CHARSET_UTF8
				: charset);
		HttpGet get = null;
		
		//因飞速中科点击API的传入信息存在空格，所以做完善处理
		try {
			get = new HttpGet(URLDecoder.decode(URLEncoder.encode(url.replace(" ", "%20"),charset), charset));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		//处理结束
		
		if (cookieheaders != null && cookieheaders.length > 0) {

			for (Header header : cookieheaders) {
				get.addHeader(header);
			}
		}
		CloseableHttpResponse response = null;
		String res = null;
		try {
			response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
//			logger.info("GET:" + url);
			res = EntityUtils.toString(entity, charset);
			// 放入缓存
			if (cacheswitch) {
				cacheManager.put(url, res);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return res;
	}

	/**
	 * 对应cookie是单独设置的网站，通过某些办法把cookie的头信息提取出来，然后传到这里
	 * 
	 */
	public String get(String url, String charset, String cookie)
			throws IOException {
		if (url == null || url.isEmpty()) {
			return null;
		}
		// 如果缓存中有，则直接取出并返回
		if (cacheswitch == true) {
			Object cacheObj = cacheManager.get(url);
			if (cacheObj != null) {
				return (String) cacheObj;
			}
		}

		charset = (charset == null ? CHARSET_UTF8 : charset);
		HttpGet get = new HttpGet(url);
		if (cookieheaders != null && cookieheaders.length > 0) {

			for (Header header : cookieheaders) {
				get.addHeader(header);
			}
		}
		get.addHeader("cookie", cookie);
		CloseableHttpResponse response = null;
		String res = null;
		try {
			response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();

//			logger.info("GET:" + url);
			res = EntityUtils.toString(entity, charset);
			// 放入缓存
			if (cacheswitch) {
				cacheManager.put(url, res);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				response.close();
			}

		}
		return res;
	}

	/**
	 * 只传一个网址的get
	 * 
	 */
	public String get(String url) throws IOException {
		return get(url, null);
	}

	public String post(String url, Map<String, String> p) throws IOException {
		return post(url, p, null);
	}

	/**
	 * post方法，如果需要cookie则用getcookie方法设置
	 * 
	 */
	public String post(String url, Map<String, String> paramsMap, String charset){
		if (url == null || url.isEmpty()) {
			return null;
		}
		List<NameValuePair> params = getParamsList(paramsMap);
		UrlEncodedFormEntity formEntity = null;
		HttpPost post = null;
		CloseableHttpResponse response = null;
		String res = null;
		try {
			charset = (charset == null ? CHARSET_UTF8 : charset);
			formEntity = new UrlEncodedFormEntity(params, charset);
			post = new HttpPost(url);
//			logger.info("POST:" + url);
			post.setEntity(formEntity);
			if (cookieheaders != null && cookieheaders.length > 0) {

				for (Header header : cookieheaders) {
				}
			}
			response = httpClient.execute(post);
			//res = EntityUtils.toString(response.getEntity());
			BufferedReader in =new BufferedReader(new InputStreamReader(response.getEntity().getContent(),charset));
			String line;
			StringBuffer sf = new StringBuffer();
			while ((line = in.readLine()) != null) {
				sf.append(line);
			}
			res=sf.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return res;
	}
	public String post4Str(String url,String content, String charset){
		return this.post4Str(url, content, charset, null);
	}
	public String post4Str(String url,String content, String charset,String contentType){
		if (url == null || url.isEmpty()) {
			return null;
		}
		HttpPost httpPost = null;
		CloseableHttpResponse response = null;
		String res = null;
		try {
			charset = (charset == null ? CHARSET_UTF8 : charset);
			httpPost = new HttpPost(url);
//			logger.info("POST:" + url);
			if (cookieheaders != null && cookieheaders.length > 0) {
				for (Header header : cookieheaders) {
				}
			}
			StringEntity entity; 
            if("xml".equalsIgnoreCase(contentType)){
            	entity = new StringEntity(content,charset);
            	httpPost.setHeader("Accept", "text/xml");
            }else if("json".equalsIgnoreCase(contentType)){
            	entity = new StringEntity(content,charset);
            	entity.setContentType("application/json");
            }else{
            	entity = new StringEntity(URLEncoder.encode(content,charset));
            }
            httpPost.setEntity(entity);
			response = httpClient.execute(httpPost);
			System.out.println("response:+++++++++++++++++"+response.getEntity());
			//res = EntityUtils.toString(response.getEntity());
			BufferedReader in =new BufferedReader(new InputStreamReader(response.getEntity().getContent(),charset));
			String line;
			StringBuffer sf = new StringBuffer();
			while ((line = in.readLine()) != null) {
				sf.append(line);
			}
			res=sf.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return res;
	}
	/**
     * get请求方法
     * @param url
     * 		请求连接
     * @param charset
     * 		请求编码
     * @return
     */
    public String getHttps(String url,String charset){
        HttpGet httpGet= null;
        String resultStr = null;  
        try {
        	if(httpsClient==null){
        		httpsClient = new SSLClient();
        	}
        	//为天眼查接口做特殊处理
        	if(url.contains("Authorization=glovk2pwQoCj")){
        		url = url.replace("Authorization=glovk2pwQoCj", "");
        		httpGet = new HttpGet(url);  
        		httpGet.setHeader("Authorization", "glovk2pwQoCj");
    		}else{
    			httpGet = new HttpGet(url); 
    		}
        	
            HttpResponse response = httpClient.execute(httpGet);  
            if(response != null){  
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                	resultStr = EntityUtils.toString(resEntity,charset);
                }
            }  
        } catch (Exception e) {  
//            e.printStackTrace();  
        }  
        return resultStr;  
    }
    public String postHttps(String url,Map<String, String> params,String charset){  
        HttpPost httpPost = null;  
        String resultStr = null;
		CloseableHttpResponse response = null;
		try{
        	if(httpsClient==null){
        		httpsClient = getHttpClient();
        	}
            httpPost = new HttpPost(url);
            //设置参数  
            List<NameValuePair> list = new ArrayList<NameValuePair>();  
            Iterator iterator = params.entrySet().iterator();  
            while(iterator.hasNext()){  
                Entry<String,String> elem = (Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));  
            }  
            if(list.size() > 0){  
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);  
                httpPost.setEntity(entity);  
            }
            long start=System.currentTimeMillis();
            response = (CloseableHttpResponse )httpsClient.execute(httpPost);
			long end=System.currentTimeMillis()-start;
			System.out.println("第三方响应耗时-----"+end);
			if(response != null){
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                	resultStr = EntityUtils.toString(resEntity,charset);
//					System.out.println(resultStr);
				}
            }
			httpPost.completed();
        }catch(Exception ex){
        	ex.printStackTrace();
        }  finally {
        	if(response!=null){
				try {
					response.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		return resultStr;
    }
	/**创建HttpClient实例
	 * @return
	 */
	public static HttpClient createHttpClient(){
		HttpParams params = new BasicHttpParams();
		//设置基本参数
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);
		//超时设置
        /*从连接池中取连接的超时时间*/
		ConnManagerParams.setTimeout(params, 10000);
		// 设置最大连接数
		ConnManagerParams.setMaxTotalConnections(params, 600);
        /*连接超时*/
		HttpConnectionParams.setConnectionTimeout(params, 10000);
        /*请求超时*/
		HttpConnectionParams.setSoTimeout(params, 10000);
		//设置HttpClient支持HTTp和HTTPS两种模式
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain,
											   String authType) throws CertificateException {
				}
				@Override
				public void checkServerTrusted(X509Certificate[] chain,
											   String authType) throws CertificateException {
				}
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[]{tm}, null);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		SSLSocketFactory ssf =new SSLSocketFactory(ctx,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		schReg.register(new Scheme("https", ssf, 443));
		//使用线程安全的连接管理来创建HttpClient
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
		HttpClient client = new DefaultHttpClient(conMgr, params);
		return client;
	}
	public String postHttps4Str(String url,String content, String charset){
		return this.postHttps4Str(url, content, charset, null);
	}
    public String postHttps4Str(String url, String content,String charset,String contentType){  
        HttpPost httpPost = null;  
        String resultStr = null;  
        try{  
        	if(httpsClient==null){
        		httpsClient = new SSLClient();
        	} 
            httpPost = new HttpPost(url);  
//            StringEntity 
            StringEntity entity; 
            if("xml".equalsIgnoreCase(contentType)){
            	entity = new StringEntity(content,charset);
            	httpPost.setHeader("Content-type", "text/xml");
            }else if("json".equalsIgnoreCase(contentType)){
            	entity = new StringEntity(content,charset);
            	httpPost.setHeader("Content-type", "application/json");
            }else{
            	entity = new StringEntity(URLEncoder.encode(content,charset));
            }
			httpPost.setEntity(entity);
			
            HttpResponse response = httpsClient.execute(httpPost);  
            if(response != null){  
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                	resultStr = EntityUtils.toString(resEntity,charset);
                }  
            }  
        }catch(Exception ex){
        	ex.printStackTrace();
        }  
        return resultStr;  
    }
	private String getRequestType(String content) {
		if(content.startsWith("<?xml")&&content.startsWith(">")){
			return "xml";
		}else if(content.startsWith("{")&&content.startsWith("}")){
			return "json";
		}
		return null;
	}

	/**
	 * post方法，connection
	 * 
	 */
	public String postByURL(String url, String param, String charset) {
		if (url == null || url.isEmpty()) {
			return null;
		}
		URL realUrl;
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuffer res = new StringBuffer("");
		try {
			realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;

			while ((line = in.readLine()) != null) {
				res.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}
		return res.toString();
	}
	/**
	 * 获取httpclien，关于httpclient的设置可以在这里进行
	 * 
	 */
	private CloseableHttpClient getCloseableHttpClient() {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
//			DefaultHttpClient:
//			请求超时
//			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000); 
//			读取超时
//			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
//
//			HttpClient
//			HttpClient httpClient=new HttpClient(); 
//			链接超时
//			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(60000);  
//			读取超时
//			httpClient.getHttpConnectionManager().getParams().setSoTimeout(60000)
		return httpclient;
	}

	public  void closeClient() throws IOException {
		httpClient.close();
	}

	/**
	 * 将传入的键/值对参数转换为NameValuePair参数集
	 * 
	 * @param paramsMap
	 *            参数集, 键/值对
	 * @return NameValuePair参数集
	 */
	private List<NameValuePair> getParamsList(Map<String, String> paramsMap) {
		if (paramsMap == null || paramsMap.size() == 0) {
			return new ArrayList<NameValuePair>();
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (Entry<String, String> map : paramsMap.entrySet()) {
			params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
		}
		if (params == null) {
			return new ArrayList<NameValuePair>();
		}
		return params;
	}
}
