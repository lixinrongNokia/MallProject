package iliker.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 向远程服务器请求数据的路径
 *
 * @author Administrator
 */
public final class GeneralUtil {
    // http://218.244.147.27/公司
    // 192.168.1.154
    public static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    public static final String HOST = "http://iliker.cn";//主机地址
    public static final String HOSTURL = HOST + "/ilikerMall/servlet";//服务器应用
    public static final String HEADURL = HOST + "/head/";//社交头像路径
    public static final String SOUNDURL = HOST + "/sound/";//声音文件路径
    public static final String GETTYPEIMG = HOST + "/img/";//商标图片
    public static final String GOODSPATH = HOST + "/goodsimg/";//商品图片
    public static final String SHAREPATH = HOST + "/share/";//分享图片路径
    public static final String THEMEURL = HOST + "/theme/";//活动海报图片路径
    public static final String STOREICON = HOST + "/storeIcon/";//门店路径
    public static final String SEARCHSVC = HOST + "/searchGoods.do";//模糊搜索商品
    public static final String SENDENABLECODE = HOST + "/enableEmail.do";//激活邮箱
    public static final String ALIPAYSIGN = HOST + "/mobilePaySign.do";//支付宝订单签名
    public static final String WXPREPAY = HOST + "/wxPayPrepayGate.do";//微信预支付路径
    public static final String STORE = HOST + "/getNearStore.do";//根据自身坐标获取附近门店

    // public static final String VERSIONUPDATE = HOST + "/apk_queryVersion.do";//查询服务端版本号，版本号大于客户端就执行下载apk更新
    public static final String VERSIONUPDATE = HOSTURL + "/GetVersionServlet";//查询服务端版本号，版本号大于客户端就执行下载apk更新

    /*用户信息*/
    public static final String LOGINON = HOST + "/userLogin.do";//用户登陆
    public static final String FACELOGIN = HOST + "/faceLogin.do";//人脸登陆
    public static final String REGISTER = HOST + "/userRegister.do";//用户注册
    public static final String EXISTSREG = HOSTURL + "/ExistsPhoneServlet";//检查电话存在
    public static final String UPDATEUSERSVT = HOSTURL + "/UpdateUserSvt";//变更用户资料
    public static final String UPDATEPWD = HOSTURL + "/UpdatePwdSvc";//更新密码
    public static final String LOOKPWD = HOSTURL + "/LookingSvc";//找回密码时根据昵称或电话返回对应电话号或昵称友好提示
    public static final String UPDATEFACESVC = HOSTURL + "/UpdateFaceSvc";//客户端用户人脸特征条码保存
    public static final String SYNCHRONOUS = HOST + "/saveSideData.do";//用户体型数据保存
    public static final String REQUESTLOCATIONSVC = HOSTURL + "/RequestLocationSvc";//保存或更新用户的位置信息
    public static final String GETSERVERPROPERTY = HOST + "/getServerProperty.do";//获取微信code
    public static final String GETWXUSERDATA = HOST + "/getWXUserData.do";//拉取微信用户信息
    public static final String REGUSERBIND = HOST + "/appBindWX.do";//绑定微信用户
    public static final String EXISTSAPPBINDWX = HOST + "/existsAPPBindWX.do";//绑定老app用户微信用户
    public static final String BINDWXCHECK = HOST + "/bindWXCheck.do";//检查是否存在手机号
    public static final String UNBINDWX = HOST + "/unBindWX.do";//检查是否存在手机号

    /*用户账户*/
    public static final String SETTINGPAYCODE = HOST + "/changePayment_code.do";//设置支付密码
    public static final String GETMYASSETS = HOST + "/showAccount_getWallet.do";//获取账户余额
    public static final String RECHARGEABLESVC = HOST + "/addRechargeOrder.do";//对账户充值
    public static final String TRANSFERACTION = HOST + "/addTransfer.do";//申请支付宝转账提现
    public static final String PAYTIP = HOSTURL + "/PaytipSvc";//打赏
    public static final String PREPAIDCARDPAY = HOSTURL + "/Prepaidcardpay";//购买卡券
    public static final String GETCOUPONS = HOST + "/findCouponsByPhone.do";//查看购买的卡券
    public static final String GETBALANCESVC = HOSTURL + "/GetBalanceSvc";//获取余额账户金额，与卡券账户余额
    public static final String INAPPPAYSVC = HOSTURL + "/InAppPaySvc";//余额支付

    /*商品相关*/
    public static final String GETGOODS = HOST + "/productList.do";
    public static final String GETTIMEGOODS = HOST + "/productTimeList.do";
    //    public static final String GETCLOTYPE = HOSTURL + "/GetCloTypeSvc";//返回所有商品二级分类
    public static final String GETCLOTYPE = HOST + "/queryCrowd.do";//返回所有商品二级分类
    public static final String GETREC = HOSTURL + "/GetRecServlet";//查询产品推荐，上下身套装搭配，锦上添花返回json数据到客户端
    /*分享与收藏*/
    public static final String UPLOADURL = HOST + "/share_portrait.do";//上传图片
    public static final String GETSHARE = HOSTURL + "/GetShareServlet";//获取分享列表
    public static final String ADDCOMM = HOSTURL + "/AddCommServlet";//对分享评论
    public static final String REPORTSHARE = HOST + "/reportShare.do";
    public static final String GETCOMM = HOSTURL + "/GetCommentsServlet";//获取对应分享的评论o
    public static final String DELFOLL = HOSTURL + "/DelFoll";//取消对用户的关注
    public static final String FOLL = HOSTURL + "/FollowerSvc";//对用户关注
    public static final String GETFOLLWER = HOSTURL + "/GetFollwersSvc";//根据参数获取关注用户或被关注用户
    public static final String COLLGOODS = HOSTURL + "/CollectionSvc";//收藏商品
    public static final String GETCOLL = HOST + "/getCollection.do";//获取用户收藏的商品
    public static final String DELCOLL = HOSTURL + "/DelColl";//删除收藏
    public static final String GETSHAREBYTIMESVC = HOSTURL + "/GetShareByTimeSvc";//按时间排序获取用户的分享
    public static final String GETOLDPHOTOSVC = HOSTURL + "/GetOldPhotoSvc";//获取用户分享的图片
    public static final String GETNEARPERSON = HOSTURL + "/GetnearPersonSvc";//获取附近的用户
    public static final String UPDATESOCIALPROPER = HOSTURL + "/UpdateSocialProper";// 添加与更新交友资料
    public static final String GETSOCIALPROPER = HOSTURL + "/GetSocialProperSvc";//社交个人信息头像，家乡，职业
    public static final String PRAISESVC = HOSTURL + "/PraiseSvc";//点赞查询点赞
    public static final String GETPRAISEBYTOUID = HOSTURL + "/GetPraiseByToUid";//查询点赞数
    public static final String GETTHEME = HOSTURL + "/GetthemeSvc";//获取主体活动
    public static final String GETREWARDLISTSVC = HOSTURL + "/GetRewardListSvc";//查询打赏我的人
    public static final String ADDTHEME = HOSTURL + "/AddThemeSvc";//加入主题与删除主题
    public static final String GETGODDESS = HOSTURL + "/GetGoddessSvc";//获取所有女神
    public static final String GETTOP4GODDESS = HOSTURL + "/GetTop4Goddess";//获取排名前四的女神

    /*订单相关*/
    public static final String ADDORDERSVC = HOST + "/addOrder.do";//添加订单
    public static final String FINDORDERSVC = HOST + "/loadOrderByPhone.do";//根据订单状态查询所有订单
    public static final String FINDBYORDERIDSVC = HOST + "/orderDetailById.do";//根据查询订单号查询订单项
    public static final String CHANGEORDERID = HOSTURL + "/ChangeOrderIDSvc";//更改订单号
    public static final String CONFIRMORDER = HOSTURL + "/ChangeOrderSvc";//修改订单状态
    public static final String DELORDERSVC = HOSTURL + "/DelOrderSvc";//删除订单
    public static final String CANCELORDER = HOST + "/userCancelOrder.do";//取消订单
    public static final String EDITADDRESS = HOST + "/editDeliverInfo.do";//编辑收货地址
    public static final String GETADDRESSSVC = HOST + "/loadDeliverInfo.do";//获取地址列表
    public static final String DELADDRESSSVC = HOSTURL + "/DelAddressSvc";//删除地址
    public static final String PARSORDERBYIDUNPACKVIEW = HOST + "/parsOrderByIdUnPackView.do";//查询订单信息

    /*三级分销相关*/
    public static final String GETPARTNER = HOSTURL + "/GetpartnerSvc";//获取一阶店或二阶店
    public static final String GETBOOKKEEPLIST = HOST + "/showAccount_getSpendings.do";//消费记录列表接口，参数phone,offset
    public static final String GETINCOMESVC = HOST + "/showAccount_getIncomes.do";//收入列表
    public static final String SUPERSTATUS = HOSTURL + "/GetSuperStatus";//获取可修改上级状态

    /*门店相关*/
    public static final String REGISTERSTORE = HOSTURL + "/RegisterStoreSvc";//门店注册
    public static final String STORELONINSVC = HOSTURL + "/StoreLoginSvc";//门店登陆
    public static final String ADDUNPACKORDER = HOST + "/addUnPackOrder.do";//添加门店自提转单
    public static final String USERUNPACKCONFIRM = HOST + "/userUnpackConfirmReceived.do";//用户自提确认
    public static final String STOREACCOUNTINFO = HOST + "/getStoreBalance.do";//查看门店服务收入
    public static final String GETUNPACKORDERSVC = HOST + "/getStoreUnpackOrder.do";//获取门店所有转单
    public static final String GETUNPACKORDERBYIDSVC = HOST + "/GetUnPackOrderById.do";//根据门店转单号获取订单详情

    public static final String LOADBRANDS = HOST + "/loadBrands.do";
    public static final String GETATTRBYGOODSCODE = HOST + "/getAttrByGoodsCode.do";
    public static final String UPDATESTORESTOCK = HOST + "/updateStoreStock.do";
    public static final String GETSTORESTOCK = HOST + "/getStoreStocks.do";
    public static final String GETBRANDBYSTOREID = HOST + "/getStoreRunBrands.do";
}
