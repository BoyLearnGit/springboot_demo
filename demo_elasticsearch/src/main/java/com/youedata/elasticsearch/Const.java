package com.youedata.elasticsearch;

/**
 * @author lyl
 *
 * @date 2016-8-10 上午11:34:08
 */
public class Const {
		
		public static final String DEFAULT_ENCODING="UTF-8";
		
		public static final String XML = "xml";
		//xsd索引名
		public static final String XSD = "xsd";
		
		public static final String XMLS = "xmls";
		//xsd条件名称
		public static final String CONDITON = "condition";
		//元数据库type名
		public static final String DATABASE = "database";
		//是
		public static final String IS_YES = "1";
		//否
		public static final String IS_NO = "0";
		//系统索引名
		public static final String SYSINDEX = "sysindex";
		//用户信息type名
		public static final String USER = "user";
		//元数据库type名
		public static final String METADB = "metadb";
		//用户权限关系
		public static final String USERAUTH = "userauth";
		//xml与xml基本信息关系表
		public static final String XMLRELA = "xmlrela";
		//初始化密码值
		public static final String RESETPASSWORD = "123456";

		
		public static final String W3C_XML_SCHEMA_NS_URI="http://www.w3.org/2001/XMLSchema";

		//并且
		public static final String OPERATOR_AND = "and";
		//或者
		public static final String OPERATOR_OR = "or";
		//等于
		public static final String OPERATOR_EQUAL = "Equal";
		//小于等于
		public static final String OPERATOR_LESSANDEQUALTHAN = "LessAndEqualThan";
		//小于
		public static final String OPERATOR_LESSTHAN = "LessThan";
		//模糊
		public static final String OPERATOR_LIKE = "Like";
		
		public static final String OPERATOR_LIKEEQUAL = "LikeEqual";
		//大于等于
		public static final String OPERATOR_MOREANDEQUALTHAN = "MoreAndEqualThan";
		//大于
		public static final String OPERATOR_MORETHAN = "MoreThan";
		//asc
		public static final String ASCENDING = "Ascending";
		//desc
		public static final String DESCENDING = "Descending";
		//asc
		public static final String ASC = "ASC";
		//desc
		public static final String DESC = "DESC";
		
		public static final String ARRAY = "[]";
		//导出压缩文件的分组大小
		public static final  int FILECOUNTFORZIP =500;
		
		//portalInterFace userLogin/register/... START
		//验证码业务类型
		public static final  String LOGIN_CODE ="loginCode";
		public static final  String REGISTER_CODE ="registerCode";
		public static final  String FORGTOPWD_CODE ="forgotCode";
		//登录失败
		public static final  int FAILEDTIMES =3;
		//登录失败间隔时间
		public static final  int FAILED_SPACETIME =2000000;
		//页面cookie对象名称
		public static final  String USER_COOKIE ="uetoken";
		//登录次数计数
		public static final  String UE_TIMES ="ue_ts";
		//portalInterFace userLogin/register/...  END

		public static final int HOTWORD_ONLINE = 1;//热词状态   1-在线

		public static final Object RECOMMENDBANNER_ONLINE = 1;//广告位状态  1-在线

		public static final Integer RECOMMENDGOODS_ONLINE = 1;//推荐商品在线状态    1-在线

		public static final int GOODSCATEGORY_TYPE_SJFL = 1;//商品分类表类别类型  1-数据分类
		
		public static final int GOODSCATEGORY_TYPE_KFZJK = 2;//商品分类表类别类型  2-开发者接口
		
		public static final int GOODSCATEGORY_YJFL_PID = 0;//商品分类表 一级分类的父id为0

		public static final Integer TUCAO_ANONYMOUSR_Y = 1;//视频吐槽是否匿名  1是
		
		public static final Integer TUCAO_ANONYMOUSR_N = 0;//视频吐槽是否匿名  0否

		public static final Integer DATACUSTOM_STATUS_WHF = 0;//数据定制状态  0-未回复

		public static final Integer VIDEO_ONLINE = 1;//直播视频状态 1-上架 
		
		public static final  String ES_INDEX ="pi_search";
		
		public static final  String ES_TYPE_GOODS ="goods";
		
		public static final  String ES_TYPE_VIDEO ="video";
		
		public static final  String ES_ANALYZER_KEYWORD ="keyword";
		
		public static final  String ES_ANALYZER_IK ="ik_max_word";
		
        //预下单
		public static final int ORDER_STATUS_READY = 1;
		//已下单
		public static final int ORDER_STATUS_UNDER = 2;
		//已完成
		public static final int ORDER_STATUS_COMPLETED = 3;
		//已取消
		public static final int ORDER_STATUS_CANEL = 4;
		//已删除
		public static final int ORDER_STATUS_DELETE = 5;
		//待支付
		public static final int PAY_STATUS_READY = 1;
		//已支付
		public static final int PAY_STATUS_COMPLETED = 2;
		//块
		public static final int BLOCK = 1;
		//流
		public static final int API = 2;
		//数据产品
		public static final int APP=3;
		
		public static final int NO = 0;
		
		public static final int YES = 1;
		
		//充值
		public static final int RECHARGE =1;
		//消费
		public static final int SPENDING =2;
		
		public static final String BLOCK_STR = "BLOCK";
		public static final String API_STR = "API";
		
		//0元
		public static final String MAKETPRICE = "0";
		//0元商品默认可用次数
		public static final int ALLOWTIMES = 1000;
		
		public static final long ZERO_LONG = 0l;
		//线下允许次数
		public static final int ZERO_OFFLINE = 0;
		
		//0:全部订单 1:待付款
		public static final String SEARCH_ALL = "0";
		public static final String SEARCH_DFK = "1";

		public static final String ADV_TYPE_TOP = "1";//首页轮播图右侧广告位：1为大图广告，2为小图左，3为小图右
		public static final String ADV_TYPE_LEFT = "2";//首页轮播图右侧广告位：1为大图广告，2为小图左，3为小图右
		public static final String ADV_TYPE_RIGHT = "3";//首页轮播图右侧广告位：1为大图广告，2为小图左，3为小图右
		
		public static final String CANCEL = "cancel";
		public static final String DELTET = "delete";
		//计费分页传-1查询全部
		public static final int NEGATIVE = -1;
		//剩余次数
		public static final int SURPLUSTIMES = 5;
		
		//1:普通订单 2:线下实时结算
		public static final int ORDERTYPE_NORMAL = 1;
		public static final int ORDERTYPE_OFFLINE = 2;
		//线下支付 1:微信 2:支付 3:线下
		public static final int OFFLINE_PAYMENT = 3;
		//免支付
		public static final int FREE_PAYMENT = 4;
	
		//1:立即购买 2:结算
		public static final String PURCHASE = "1";
		public static final String SETTLEMENT = "2";
		
		public static final String OUTTIME = "outTime";
		
		
		public static final String REC_CODE_JXHYSJ="JXHYSJ";//精选行业数据推荐码

		public static final String REC_CODE_SYLBT = "SYLBT";//首页轮播图

		public static final String REC_CODE_GG = "GG";

		public static final String REC_CODE_GZ = "GZ";

		public static final String REC_CODE_ZXSX = "ZXSX";//最新上线推荐位推荐码

		public static final String REC_CODE_RMTJ = "RMTJ";//热门推荐推荐位推荐码

		public static final String REC_CODE_YHZK = "YHZK";

		public static final String REC_CODE_TMDZY = "TMDZY";//他们都在用推荐位推荐码

		public static final String REC_CODE_YYZQ = "YYZQ";//一元专区推荐位推荐码

		public static final String REC_CODE_XSZK = "XSZK";//限时折扣推荐位推荐码

		public static final int HELP_CATEGORY_GG = 1;//类型代码：0普通类型帮助 1公告 2规则
		
		public static final int HELP_CATEGORY_GZ = 2;//类型代码：0普通类型帮助 1公告 2规则

		public static final int PID_TOP_HELPCATEGORY = 0;//顶级帮助类目的父id

		public static final String HOTWORD = "HOTWORD";
		
		public static final int DOWN_STATUS = 2;//下架
}
