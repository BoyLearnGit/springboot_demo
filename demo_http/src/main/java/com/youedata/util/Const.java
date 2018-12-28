package com.youedata.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Const {
public static final String UTF8 = "utf-8";
	
	public static final String FALSE = "false";
	public static final String TRUE = "true";
	
	public final static String Y_TOKEN_KEY = "Y_TOKEN";
	
	//DUBBO参数字典
	public static final String DUBBO_GROUP_NORMAL = "normal";
	public static final String DUBBO_GROUP_VERSION = "1.0.0";
	
	//缓存参数
	public final static String IS_REDIS_CACHE = "isRedisCache";// 是否redis缓存
	public final static String CACHE_MAX_MSEC = "cacheMaxMSec";//缓存时间
	
	//redis key
	public final static String REDIS_KEY = "APIINFODTO_REDIS_KEY";
	public final static String REDIS_KEY_USER_TOKEN = "REDIS_KEY_USER_TOKEN";//用户权限
	public final static String REDIS_KEY_UNION_CACHE = "REDIS_KEY_UNION_CACHE";//联通缓存
	public final static String REDIS_KEY_USER_LUCHENG = "REDIS_KEY_USER_LUCHENG";//路诚客户缓存
	public final static String REDIS_KEY_UNION_CONFIG = "REDIS_KEY_UNION_CONFIG";//路诚客户缓存
	
	//联通
	public final static String UNION_USER_START = "union-";//用户权限
	
	//调用流程
	public final static String API_INVOKE_LINK [] ={ "参数处理","参数校验","参数加工"
		,"获取key","参数再加工","认证","调用" 	//3/4/5/6
		,"结果处理","结果校验","结果加工"		//7/8/9
		,"计费-供应商","结果加工","计费-用户"};	//10/11/12
	
	//排序类型 AsciiAsc AsciiDes
	public final static String [] SORT_TYPE_ASCII_ASC    = {"AsciiAsc","ascii排序"};
	public final static String [] SORT_TYPE_ASCII_DESC   = {"AsciiDesc","ascii倒排序"};
	public final static String [] SORT_TYPE_UNICODE_ASC  = {"UnicodeAsc","Unicode倒排序"};
	public final static String [] SORT_TYPE_UNICODE_DESC = {"UnicodeDesc","Unicode倒排序"};
	//加密类型
	public final static String [] ENCRYPT_TYPE_MD5 = {"md5","标准MD5"};
	public final static String [] ENCRYPT_TYPE_AES = {"aes","AES"};
	public final static String [] ENCRYPT_TYPE_RSA = {"rsa","RSA"};
	
	//http
	public static final String HTTPS_TYPE = "https";
	public static final String HTTP_TYPE = "http";
	
	public static final String METHORD_TYPE_GET = "get";
	public static final String METHORD_TYPE_POST = "post";
	
	public static final String CONTENT_TYPE_XML = "xml";
	public static final String CONTENT_TYPE_JSON="json";
	public static final String CONTENT_TYPE_JSONXML="json,xml";
	public static final String CONTENT_TYPE_TXT="txt";

	/**
	 *消费api用户
	 */
	public static final String CONSUMER_USER_ID = "CONSUMER_USER_ID";
	
	//api key
	public static String KEY_N ="0";//无需-key
	public static String KEY_STATIC ="1";//静态-与api绑定
	public static String KEY_PUB ="2";//静态-公共key
	public static String KEY_CURRENT_USER ="3";//静态-当前用户key
	public static String KEY_DYNAMIC ="4";//动态-key
	
	
	//日志级别
	public static String LOG_LEVEL_INFO ="info";
	public static String LOG_LEVEL_WARN ="warn";
	public static String LOG_LEVEL_ERR ="err";
	public static String LOG_LEVEL_DEBUG ="debug";
	//日志类型
	public static String LOG_TYPE_INVOKE ="invoke";//调用日志
	
	
	  /**
     * 结果码
------------------------

10000:系统级别错误
10001:网络超时
10002:系统超时
10003:时间误差在正负20分钟以内，当前发送的连接已超时

20001:用户或机构不存在
20002:用户或机构验证出错

20010:此接口不存在
20011:此接口禁用
20012:缺少协议参数
20013:没有此接口权限
20014:ip无权限访问此接口
20015:此接口使用到期
20016:余额不足

20021:缺少参数
20022:xxx参数不能为空
20023:参数xxx值无效
20024:参数排序失败
20025:参数加工失败
20026:参数加工失败
20027:非测试号码
20028:序列号重复

20030:需要 http请求
20031:需要 https请求
20032:需要 get请求
20033:需要 post请求
20040:没有返回结果
20041:无效的返回结果
20042:结果处理失败
20043:结果加工失败
20050:成功
20051:调用成功但不计费
30001:服务端错误

*#*#4636
*/
	//结果校验
	public static final String [] RESULT_CODE_SYS_ERR = {"10000","系统级别错误"};
	public static final String [] RESULT_CODE_CURTIME_ERR = {"10003","时间误差在正负20分钟以内，当前发送的连接已超时"};
	public static final String [] RESULT_CODE_USER_EXIST_N = {"20001","用户或机构不存在"};
	public static final String [] RESULT_CODE_USERVALI_N = {"20012","用户密钥错误"};
	public static final String [] RESULT_CODE_USERTOKEN_N = {"20013","没有此接口权限"};
	public static final String [] RESULT_CODE_IP_N = {"20014","ip无权限访问此接口"};
	public static final String [] RESULT_CODE_USERTOKEN_OUT = {"20015","此接口使用到期"};
	public static final String [] RESULT_CODE_COUNT_N = {"20016","余额不足"};
	public static final String [] RESULT_CODE_PARAM_ERR = {"20022","xxx参数不能为空"};
	public static final String [] RESULT_CODE_PARAM_VAL_ERR = {"20023","参数xxx值无效"};
	public static final String [] RESULT_CODE_TEST_DATA = {"20027","非测试数据"};
	public static final String [] RESULT_CODE_SEQ_EXIST = {"20028","序列号重复"};
	public static final String [] RESULT_CODE_RESULT_NULL = {"20040","没有返回结果"};
	public static final String [] RESULT_CODE_RESULT_ERR = {"20041","无效的返回结果"};
	public static final String [] RESULT_CODE_SUCCESS = {"20050","成功"};
	public static final String [] RESULT_CODE_SERVICE_ERR = {"30001","服务端错误"};
	
	//Get 拼接url
	public static StringBuffer getUrlByMap(Map<String, String> params, StringBuffer sf) {
		Iterator<Map.Entry<String,String>> entries= params.entrySet().iterator();
		Map.Entry<String,String> entry;
		String name;
		boolean flag = false;
		while(entries.hasNext()){
			entry = (Map.Entry<String,String>) entries.next(); 
			name = entry.getKey(); 
			String valueObj = entry.getValue(); 
			if(flag){
				sf.append("&");
			}
			flag = true;
			sf.append(name).append("=").append(valueObj);
		}
		return sf;
	}
	/**
	 * 获取32UUID
	 * @param length
	 * @return
	 */
	public static String get32UUID() {
		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
		return uuid;
	}
	
	/**
	 * 获取随机数
	 * @param length
	 * @return
	 */
	public static String randomStr (int length){
       char[] ss = new char[length];//用于保存随机生成的字符串；
       int i=0;
       while(i<length) {
           int f = (int) (Math.random()*3%3);
           if(f==0)  
               ss[i] = (char) ('A'+Math.random()*26);
           else if(f==1)  
               ss[i] = (char) ('a'+Math.random()*26);
           else 
               ss[i] = (char) ('0'+Math.random()*10);    
           i++;
        }
        return new String(ss);
	}
	private static Random random = new Random();
	public static int randomInt (int size){
		return random.nextInt(size);
	}
	public static void main(String[] args) {
		System.err.println(Const.API_INVOKE_LINK[1]);
	}
}
