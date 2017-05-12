package com.vtg.app.util;

public interface CommonDefine {
	public static final int DIVIDER_MONEY = 1000000;
	public static final int CREDIT = 2;
	public static final int DEBIT = 1;
	public static final String METHOD_GET = "get";
	public static final String METHOD_POST = "post";
	public static final String MY_PACKAGE = "com.vtg.app";
	public static final String NUMBER_HEADER = "855";
	public static final String PASSWORD = "f319dce9a403a11a1755ef423296a346";
	public static final String SIGNATURE = "signature";
	public static final String USERNAME = "f319dce9a403a11a4687ccba21e07165";

	public static final class Action {
		public static final String ACTION = "action";
		public static final String DONE_RELOAD_ACCOUNT = "done_reload_account";
		public static final String DONE_RELOAD_DATA = "done_reload_data";
		public static final String FILTER = "com.vtg.app";
		public static final String RELOAD_ACCOUNT = "reload_account";
		public static final String RELOAD_DATA = "reload_data";
		public static final String RELOAD_PROFILE = "reload_profile";
		public static final String RELOAD_PROFILE_DONE = "reload_profile_done";
		public static final String RELOAD_VAS = "reload_vas";
	}

	public static final class MyService {
		public static final String WEB_DOMAIN = "http://mf.mine.vn";
		public static final String DOMAIN = "http://api.truonglx.me/";
		public static final String ABOUT = DOMAIN
				+ "api/info/about";
		public static final String BANNER = DOMAIN
				+ "api/banner/list?signature=dcb0f814d2d18359cc6e77f63e890806";
		public static final String GET_EXTEND_DATA = DOMAIN
				+ "api/packextent/list";
		public static final String GET_NEW = WEB_DOMAIN + "/api/news/hot/20";
		public static final String GET_OTP = DOMAIN + "api/getotp";
		public static final String GET_PROVINCE = DOMAIN
				+ "api/province/list";
		public static final String GET_SUB_DATA = DOMAIN
				+ "api/3g/list?signature=dcb0f814d2d18359cc6e77f63e890806";
		public static final String LOGIN = DOMAIN + "api/login";
		public static final String NOTI = DOMAIN
				+ "api/info/notification?signature=dcb0f814d2d18359cc6e77f63e890806";
		public static final String PROMOTION = DOMAIN + "api/promote/list";
		public static final String REGIS = DOMAIN + "api/register";
		public static final String SHOWROOM = DOMAIN + "api/showroom/list";
		public static final String TARIFF = DOMAIN
				+ "api/info/tariff";
		public static final String VAS = DOMAIN
				+ "api/vas/list";
		public static final String USED_VAS = DOMAIN
				+ "/api/vas/get";
		public static final String GET_OTP_CHANGE_PASS = DOMAIN
				+ "api/getotpchangepass";
		public static final String CHANGE_PASS = DOMAIN + "api/changepass";
		public static final String SUB_DATA_INFO = DOMAIN + "api/3g/item";
	}

	public static final class PreferenceKey {
		public static final String LOCATE = "locate";
		public static final String PHONE_NUMBER = "phone_number";
		public static final String PHONE_NUMBER_FULL = "phone_number_full";
	}

	public static final class SoapTag {
		public static final String NAME = "name";
		public static final String PASSWORD = "password";
		public static final String RAWDATA = "rawData";
		public static final String USERNAME = "username";
		public static final String VALUE = "value";
	}

	public static final class WSCode {
		public static final String BUY_DATA = "SCbuyDataUnlimited";
		public static final String CALL_DETAIL_HISTORY_POSTPAID = "SCgetCallDetailHistoryPostpaid";
		public static final String CHECK_3G = "SCcheck3G";
		public static final String DETECT_ISDN = "RadiusDetectISDN";
		public static final String GET_CARD_HISTORY = "SCgetScatchHistory";
		public static final String GET_CHARGE_DETAIL = "SCgetChargingDetail";
		public static final String GET_DEBIT_INFO = "SCgetDebitInfoInCycle";
		public static final String GET_DEBIT_INFO_BUILD = "SCgetDetailInfoBill";
		public static final String GET_DISTRICT = "SCgetListAllDistrictByProvince";
		public static final String GET_LIST_ID = "SCgetListIdType";
		public static final String GET_PRECINT = "SCgetListAllPrecinctByDistrict";
		public static final String GET_PROVINCE = "SCgetListAllProvince";
		public static final String GET_SMS_DETAIL = "SCgetChargingSmsDetail";
		public static final String GET_SUB_ACC = "SCgetSubAcc";
		public static final String GET_SUB_INFO = "SCgetsubinfo";
		public static final String GET_VAS_DETAIL = "SCgetChargingDataVasDetail";
		public static final String PAY_BY_CARD = "SCpayByCard";
		public static final String REGISTER_MI = "SCregisterData3gV2";
		public static final String REGIS_VAS_FAKE_MO = "SCregisterVasFakeMO";
		public static final String REMOVE_MI = "SCremove3G";
		public static final String SYNC_VAS = "WebserviceSyncVas";
		public static final String TOP_UP_PAY_BY_CARD = "SCtopUpPayByCard";
		public static final String UPDATE_CUS_INFO = "SCupdateCustomerInfo";
		public static final String GET_DATA_CODE = "SCget3Ginfo";
		public static final String HIGH_SPEED = "SCviewHighSpeed";
		public static final String TRANFER_MONEY = "SC_TransferMoney";
	}

	public static final class mXML {
		public static final String ACT_STATUS = "actStatus";
		public static final String ACT_STATUS_BIT = "actStatusBit";
		public static final String ADDRESS = "address";
		public static final String BIRTHDATE = "birthDate";
		public static final String BUS_TYPE = "busType";
		public static final String CODE = "code";
		public static final String CUST_ID = "custId";
		public static final String CUS_NAME = "custName";
		public static final String DESC = "desc";
		public static final String DESCRIPTION = "description";
		public static final String ERR_CODE = "errCode";
		public static final String ERROR_CODE = "errorCode";
		public static final String ERR_OCS = "errOcs";
		public static final String ID_NO = "idNo";
		public static final String ID_TYPE = "idType";
		public static final String IMSI = "imsi";
		public static final String ISDN = "isdn";
		public static final String ISSUE_PLACE = "issuePlace";
		public static final String MESSAGE = "message";
		public static final String PRODUCT_CODE = "productCode";
		public static final String RESPONSE_CODE = "responseCode";
		public static final String RESULT = "return";
		public static final String SERIAL = "serial";
		public static final String SERVICE_TYPE = "serviceType";
		public static final String START_DATE_TIME = "startDatetime";
		public static final String STATUS = "status";
		public static final String SUB_ID = "subId";
		public static final String TELECOM_SERVICE = "telecomService";
		public static final String TEL_SERVICE_ID = "telServiceId";
		public static final String USER_USING = "userUsing";
		public static final String VALUE = "value";
		public static final String DATA = "data";
	}
}
