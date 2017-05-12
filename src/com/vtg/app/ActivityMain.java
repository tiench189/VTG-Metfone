package com.vtg.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.vtg.app.component.AdapterBanner;
import com.vtg.app.component.DialogReport;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelBalance;
import com.vtg.app.model.ModelBanner;
import com.vtg.app.model.ModelDebit;
import com.vtg.app.model.ModelMoreData;
import com.vtg.app.model.ModelPaper;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelTag;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.Action;
import com.vtg.app.util.CommonDefine.MyService;
import com.vtg.app.util.CommonDefine.PreferenceKey;
import com.vtg.app.util.CommonDefine.SoapTag;
import com.vtg.app.util.CommonDefine.WSCode;
import com.vtg.app.util.CommonDefine.mXML;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.JSONParser;
import com.vtg.app.util.NetworkUtil;
import com.vtg.app.util.SOAPUtil;
import com.vtg.app.util.XMLParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NodeList;

public class ActivityMain extends FragmentActivity implements CommonDefine,
		OnClickListener {
	// public static final String PASSWORD = "f319dce9a403a11a1755ef423296a346";
	private static final String TAG = "ActivityMain";
	// public static final String USERNAME = "f319dce9a403a11a4687ccba21e07165";
	public static ModelBalance basicAcc;
	public static String data3g;
	public static String dataExpirity;
	public static String dataCode;
	public static int dataErrCode;
	public static ModelDebit debitAcc = new ModelDebit();
	public static List<ModelBanner> listBanner;
	public static List<ModelMoreData> listMoreData;
	private AdapterBanner adapterBanner;
	Configuration config;
	private boolean get3gData;
	private boolean getBalance;
	private boolean getOffer;
	private ImageView ivMenu;
	private ImageView ivNoti;
	private RelativeLayout layoutBalance;
	private RelativeLayout layoutData;
	private RelativeLayout layoutOffer;
	private RelativeLayout layoutSplash;
	private RelativeLayout layoutTransaction;
	private RelativeLayout layoutVas;
	private String locate;
	private Context mContext;
	private SlidingMenu menu;
	private ViewPager pagerBanner;
	private SharedPreferences preferences;
	private MainReceiver receiver;
	private boolean showSplash;
	private TextView tvBalanceStatus;
	private TextView tvDataStatus;
	private TextView tvOffer;

	/* renamed from: com.vtg.app.1 */
	class C05581 implements OnClickListener {
		C05581() {
		}

		public void onClick(View v) {
			finish();
		}
	}

	/* renamed from: com.vtg.app.2 */
	class C05592 implements Runnable {
		C05592() {
		}

		public void run() {
			int current = pagerBanner.getCurrentItem();
			if (current < 3) {
				current++;
			} else {
				current = 0;
			}
			pagerBanner.setCurrentItem(current);
			changeBanner();
		}
	}

	/* renamed from: com.vtg.app.3 */
	class C05603 implements OnClickListener {
		C05603() {
		}

		public void onClick(View v) {
			if (menu.isMenuShowing()) {
				menu.showContent();
			} else {
				menu.showMenu();
			}
		}
	}

	private class AsyncTaskAccInfo extends AsyncTask<String, String, Boolean> {
		String message;

		private AsyncTaskAccInfo() {
			message = "";
		}

		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected Boolean doInBackground(String... p) {
			try {
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam(mXML.ISDN, new StringBuilder(
						CommonDefine.NUMBER_HEADER).append(
						preferences.getString(PreferenceKey.PHONE_NUMBER, ""))
						.toString()));
				params.add(new ModelParam("listId", "2000"));
				SOAPUtil soap = new SOAPUtil(WSCode.GET_SUB_ACC, tags, params);
				soap.makeSOAPRequest();
				if (soap.getError() == 0) {
					NodeList listId = soap.mDoc
							.getElementsByTagName("balanceId");
					basicAcc = new ModelBalance(
							FunctionHelper.formatDateTime(XMLParser
									.getElementValue(soap.mDoc
											.getElementsByTagName(
													"balanceExpire").item(0))),
							XMLParser.getElementValue(listId.item(0)),
							XMLParser.getElementValue(soap.mDoc
									.getElementsByTagName("balanceName")
									.item(0)),
							FunctionHelper.formatMoney(XMLParser
									.getElementValue(soap.mDoc
											.getElementsByTagName(
													"balanceValue").item(0))));
					return Boolean.valueOf(true);
				}
				message = FunctionHelper.getErrorMessage(mContext,
						soap.getError());
				return Boolean.valueOf(false);
			} catch (Exception e) {
				message = mContext.getString(R.string.err_connect);
				return Boolean.valueOf(false);
			}
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result.booleanValue()) {
				getBalance = true;
				Intent intent = new Intent();
				intent.setAction(CommonDefine.MY_PACKAGE);
				intent.putExtra(Action.ACTION, Action.DONE_RELOAD_ACCOUNT);
				sendBroadcast(intent);
			} else {
				basicAcc = new ModelBalance("", "", "", message);
			}
			setBasicAccount();
			new AsyncTaskCheck3G().execute(new String[0]);
		}
	}

	private class AsyncTaskCheck3G extends AsyncTask<String, String, Boolean> {
		String message;

		private AsyncTaskCheck3G() {
			message = "";
		}

		protected void onPreExecute() {
			super.onPreExecute();
			listMoreData = new ArrayList();
		}

		protected Boolean doInBackground(String... p) {
			try {
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam("msisdn", new StringBuilder(
						CommonDefine.NUMBER_HEADER).append(
						preferences.getString(PreferenceKey.PHONE_NUMBER, ""))
						.toString()));
				params.add(new ModelParam("send_sms", "0"));
				params.add(new ModelParam("requestId", FunctionHelper
						.formatCurrentTime()));
				SOAPUtil soap = new SOAPUtil(WSCode.CHECK_3G, tags, params);
				soap.makeSOAPRequest();
				if (soap.getError() != 0) {
					message = getString(R.string.data_unavalable);
					return Boolean.valueOf(false);
				} else if (soap.getValue(mXML.ERR_CODE).equals("0")) {
					message = soap.getValue(mXML.MESSAGE);
					dataErrCode = Integer
							.parseInt(soap.getValue(mXML.ERR_CODE));
					if (!message.equals("")) {
						return Boolean.valueOf(true);
					}
					message = getString(R.string.data_unavalable);
					return Boolean.valueOf(false);
				} else {
					message = getString(R.string.data_unavalable);
					return Boolean.valueOf(false);
				}
			} catch (Exception e) {
				message = getString(R.string.data_unavalable);
				return Boolean.valueOf(false);
			}
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			get3gData = true;
			getOffer = true;
			data3g = message;
			if (result.booleanValue()) {
				dataCode = FunctionHelper.subDataCode(message);
				try {
					data3g = message.substring(0, message.indexOf("("));
					// + "\n"
					// + message.substring(message.indexOf("(") + 1,
					// message.indexOf(")"));
					dataExpirity = FunctionHelper.parseExpirityData(message);
					new AsyncTaskDataCode().execute();
				} catch (Exception e) {

				}
			} else {
				set3gData();
				layoutSplash.setVisibility(8);
				dataCode = "";
				Intent intent = new Intent();
				intent.setAction(CommonDefine.MY_PACKAGE);
				intent.putExtra(Action.ACTION, Action.DONE_RELOAD_DATA);
				sendBroadcast(intent);
			}
		}
	}

	private class AsyncTaskDataCode extends AsyncTask<String, String, Boolean> {
		String message = "";

		protected void onPreExecute() {
			super.onPreExecute();
			listMoreData = new ArrayList<ModelMoreData>();
		}

		protected Boolean doInBackground(String... p) {
			try {
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam("msisdn", new StringBuilder(
						CommonDefine.NUMBER_HEADER).append(
						preferences.getString(PreferenceKey.PHONE_NUMBER, ""))
						.toString()));
				SOAPUtil soap = new SOAPUtil(WSCode.GET_DATA_CODE, tags, params);
				soap.makeSOAPRequest();
				if (soap.getError() != 0) {
//					message = getString(R.string.data_unavalable);
					return Boolean.valueOf(false);
				} else {
					dataCode = soap.getValue(mXML.DATA);
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return Boolean.valueOf(false);
			}
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			layoutSplash.setVisibility(8);
			set3gData();
			Intent intent = new Intent();
			intent.setAction(CommonDefine.MY_PACKAGE);
			intent.putExtra(Action.ACTION, Action.DONE_RELOAD_DATA);
			sendBroadcast(intent);
		}
	}

	private class AsyncTaskDebitInfo extends AsyncTask<String, String, Boolean> {
		String message;

		private AsyncTaskDebitInfo() {
			message = "";
		}

		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected Boolean doInBackground(String... p) {
			try {
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam(mXML.ISDN, preferences.getString(
						PreferenceKey.PHONE_NUMBER, "")));
				SOAPUtil soap = new SOAPUtil(WSCode.GET_DEBIT_INFO, tags,
						params);
				soap.makeSOAPRequest();
				if (soap.getError() != 0) {
					message = soap.getValue(mXML.DESCRIPTION);
					return Boolean.valueOf(false);
				} else if (soap.getValue(mXML.RESPONSE_CODE).equals("0")) {
					String v = soap.getValue(mXML.VALUE).trim();
					Log.v(TAG, "tiench value: " + v);
					String[] value = v.split("\\|");
					Log.d(TAG, "tiench value: " + value.length);
					debitAcc = new ModelDebit(
							FunctionHelper.formatMoney(value[0]),
							FunctionHelper.formatMoney(value[1]),
							FunctionHelper.formatMoney(value[2]),
							FunctionHelper.formatMoney(value[3]));
					return Boolean.valueOf(true);
				} else {
					message = soap.getValue(mXML.DESCRIPTION);
					return Boolean.valueOf(false);
				}
			} catch (Exception e) {
				message = mContext.getString(R.string.err_connect);
				return Boolean.valueOf(false);
			}
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			getBalance = true;
			if (result.booleanValue()) {
				Intent intent = new Intent();
				intent.setAction(CommonDefine.MY_PACKAGE);
				intent.putExtra(Action.ACTION, Action.DONE_RELOAD_ACCOUNT);
				sendBroadcast(intent);
			}
			setBasicAccount();
			new AsyncTaskCheck3G().execute(new String[0]);
		}
	}

	private class AsyncTaskSubInfo extends AsyncTask<String, String, Boolean> {
		String message;
		boolean reload;
		boolean errConnect = false;

		/* renamed from: com.vtg.app.AsyncTaskSubInfo.1 */
		class C05611 implements OnClickListener {
			C05611() {
			}

			public void onClick(View v) {
				finish();
			}
		}

		private AsyncTaskSubInfo() {
			message = "";
			reload = false;
		}

		protected Boolean doInBackground(String... p) {
			try {
				mProfile.papers = new ArrayList();
				reload = p.length > 0;
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam(mXML.ISDN, preferences.getString(
						PreferenceKey.PHONE_NUMBER, "")));
				SOAPUtil soap = new SOAPUtil(WSCode.GET_SUB_INFO, tags, params);
				soap.makeSOAPRequest();
				if (soap.getError() == 0) {
					try {
						mProfile.accountType = Integer.parseInt(soap
								.getValue(mXML.SERVICE_TYPE));
						mProfile.startTime = soap
								.getValue(mXML.START_DATE_TIME);
						mProfile.userUsing = soap.getValue(mXML.CUS_NAME);
						mProfile.address = soap.getValue(mXML.ADDRESS);
						mProfile.birthDay = soap.getValue(mXML.BIRTHDATE);
						mProfile.subId = soap.getValue(mXML.SUB_ID);
						mProfile.cusID = soap.getValue(mXML.CUST_ID);
						mProfile.productCode = soap.getValue(mXML.PRODUCT_CODE);
						try {
							NodeList listNo = soap.mDoc
									.getElementsByTagName(mXML.ID_NO);
							NodeList listType = soap.mDoc
									.getElementsByTagName(mXML.ID_TYPE);
							NodeList listDate = soap.mDoc
									.getElementsByTagName("issueDate");
							NodeList listPlace = soap.mDoc
									.getElementsByTagName(mXML.ISSUE_PLACE);
							for (int i = 0; i < 10; i++) {
								if (!XMLParser
										.getElementValue(listType.item(i))
										.trim().equals("")) {
									mProfile.papers
											.add(new ModelPaper(
													XMLParser
															.getElementValue(listNo
																	.item(i)),
													XMLParser
															.getElementValue(listType
																	.item(i)),
													XMLParser
															.getElementValue(listDate
																	.item(i)),
													FunctionHelper
															.formatMoney(XMLParser
																	.getElementValue(listPlace
																			.item(i)))));
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						return Boolean.valueOf(true);
					} catch (Exception ex) {
						message = soap.getValue(mXML.MESSAGE);
						return Boolean.valueOf(false);
					}
				}
			} catch (Exception e2) {
				message = mContext.getString(R.string.err_connect);
				errConnect = true;
				e2.printStackTrace();
				return Boolean.valueOf(false);
			}
			return false;
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (!result.booleanValue()) {
				DialogReport dlErr = new DialogReport(mContext, message);
				dlErr.show();
				dlErr.btnOK.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (errConnect) {
							finish();
						} else {
							preferences.edit()
									.putString(PreferenceKey.PHONE_NUMBER, "")
									.commit();
							startActivity(new Intent(mContext,
									ActivityFirst.class));
							finish();
						}
					}
				});
			} else if (reload) {
				Intent intent = new Intent();
				intent.setAction(CommonDefine.MY_PACKAGE);
				intent.putExtra(Action.ACTION, Action.RELOAD_PROFILE_DONE);
				sendBroadcast(intent);
			} else if (mProfile.accountType == 1) {
				new AsyncTaskAccInfo().execute(new String[0]);
			} else if (mProfile.accountType == 2) {
				new AsyncTaskDebitInfo().execute(new String[0]);
			}
		}
	}

	private class AsyntaskBanner extends AsyncTask<String, String, Boolean> {
		private AsyntaskBanner() {
		}

		protected void onPreExecute() {
			super.onPreExecute();
			listBanner = new ArrayList();
		}

		protected Boolean doInBackground(String... arg0) {
			try {
				JSONArray jsonArr = new JSONObject(
						new JSONParser().getJSONFromUrl(MyService.BANNER,
								CommonDefine.METHOD_GET, null))
						.getJSONArray("data");
				for (int i = 0; i < jsonArr.length(); i++) {
					ModelBanner banner = new ModelBanner();
					banner.id = jsonArr.getJSONObject(i).getString("id");
					banner.image = jsonArr.getJSONObject(i).getString("image");
					banner.action = jsonArr.getJSONObject(i).getString(
							Action.ACTION);
					banner.status = jsonArr.getJSONObject(i).getString(
							mXML.STATUS);
					listBanner.add(banner);
				}
				return Boolean.valueOf(true);
			} catch (JSONException e) {
				e.printStackTrace();
				return Boolean.valueOf(false);
			}
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result.booleanValue()) {
				initBanner();
			}
			new AsyncTaskSubInfo().execute(new String[0]);
		}
	}

	private class MainReceiver extends BroadcastReceiver {
		private MainReceiver() {
		}

		public void onReceive(Context context, Intent intent) {
			Bundle mBundle = intent.getExtras();
			if (mBundle.getString(Action.ACTION).equals(Action.RELOAD_ACCOUNT)) {
				if (mProfile.accountType == 1) {
					new AsyncTaskAccInfo().execute(new String[0]);
				} else if (mProfile.accountType == 2) {
					new AsyncTaskDebitInfo().execute(new String[0]);
				}
			} else if (mBundle.getString(Action.ACTION).equals(
					Action.RELOAD_DATA)) {
				new AsyncTaskCheck3G().executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, new String[0]);
			} else if (mBundle.getString(Action.ACTION).equals(
					Action.RELOAD_PROFILE)) {
				new AsyncTaskSubInfo().execute(new String[] { "reload" });
			}
		}
	}

	public static class mProfile {
		public static int accountType;
		public static String address;
		public static String birthDay;
		public static String cusID;
		public static List<ModelPaper> papers;
		public static String startTime;
		public static String subId;
		public static String userUsing;
		public static String productCode;

		static {
			accountType = 0;
			userUsing = "";
			startTime = "";
			address = "";
			birthDay = "";
			subId = "";
			cusID = "";
			productCode = "";
		}
	}

	public ActivityMain() {
		locate = "";
		showSplash = true;
		getBalance = false;
		get3gData = false;
		getOffer = false;
	}

	static {
		data3g = "";
		dataCode = "";
		dataErrCode = -1;
	}

	protected void onCreate(@Nullable Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(1);
		setRequestedOrientation(7);
		mContext = this;
		preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
		receiver = new MainReceiver();
		registerReceiver(receiver, new IntentFilter(CommonDefine.MY_PACKAGE));
		Log.v(TAG, "tiench onCreated");
		if (NetworkUtil.availableConnect(mContext)) {
			new AsyntaskBanner().execute(new String[0]);
			return;
		}
		DialogReport noConnect = new DialogReport(mContext,
				"Internet not found!");
		noConnect.btnOK.setOnClickListener(new C05581());
		noConnect.show();
	}

	private void initBanner() {
		pagerBanner = (ViewPager) findViewById(R.id.pager_banner);
		adapterBanner = new AdapterBanner(getSupportFragmentManager());
		pagerBanner.setAdapter(adapterBanner);
		changeBanner();
	}

	private void changeBanner() {
		new Handler().postDelayed(new C05592(), 10000);
	}

	private void activityCreate() {
		setContentView(R.layout.activity_main);
		initView();
		initMenu();
	}

	private void initMenu() {
		ivMenu = (ImageView) findViewById(R.id.ivMenu);
		ivMenu.setOnClickListener(new C05603());
		menu = new SlidingMenu(this);
		menu.setTouchModeAbove(0);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable((int) R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, 1);
		menu.setMenu((int) R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new FragmentMenu()).commit();
	}

	private void initView() {
		layoutSplash = (RelativeLayout) findViewById(R.id.layout_splash);
		if (showSplash) {
			layoutSplash.setVisibility(0);
			showSplash = false;
		}
		layoutOffer = (RelativeLayout) findViewById(R.id.layout_offer);
		layoutOffer.setOnClickListener(this);
		tvOffer = (TextView) findViewById(R.id.tvOfferStatus);
		layoutBalance = (RelativeLayout) findViewById(R.id.layout_balance);
		layoutBalance.setOnClickListener(this);
		tvBalanceStatus = (TextView) findViewById(R.id.tvBalanceStatus);
		if (getBalance) {
			setBasicAccount();
		}
		layoutData = (RelativeLayout) findViewById(R.id.layout_data);
		layoutData.setOnClickListener(this);
		tvDataStatus = (TextView) findViewById(R.id.tvDataStatus);
		if (get3gData) {
			set3gData();
		}
		layoutVas = (RelativeLayout) findViewById(R.id.layout_vas);
		layoutVas.setOnClickListener(this);
		layoutTransaction = (RelativeLayout) findViewById(R.id.layout_transaction);
		layoutTransaction.setOnClickListener(this);
		ivNoti = (ImageView) findViewById(R.id.ivNoti);
		ivNoti.setOnClickListener(this);
	}

	private void setBasicAccount() {
		if (mProfile.accountType == 1) {
			tvBalanceStatus.setText(getString(R.string.basic_account) + ": "
					+ basicAcc.value + " USD\n"
					+ getString(R.string.expity_date) + ": " + basicAcc.expire);
		} else if (mProfile.accountType == 2) {
			tvBalanceStatus.setText(getString(R.string.charge_debit) + ": "
					+ debitAcc.charge + " USD\n"
					+ getString(R.string.debit_start) + ": "
					+ debitAcc.debitStart + " USD\n"
					+ getString(R.string.payment) + ": " + debitAcc.payment
					+ " USD\n" + getString(R.string.credit) + ": "
					+ debitAcc.credit + " USD");
		}
	}

	private void set3gData() {
		if (dataCode.equals("")) {
			tvDataStatus.setText(getString(R.string.data_unavalable));
		} else {
			tvDataStatus.setText(data3g.split("\n")[0]);
		}
	}

	public void onBackPressed() {
		if (menu.isMenuShowing()) {
			menu.showContent();
		} else {
			finish();
		}
	}

	public void setLanguage() {
		Locale locale = new Locale(preferences.getString(PreferenceKey.LOCATE,
				"en"));
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
		activityCreate();
	}

	protected void onStart() {
		super.onStart();
		if (!preferences.getString(PreferenceKey.LOCATE, "en").equals(locate)) {
			locate = preferences.getString(PreferenceKey.LOCATE, "en");
			setLanguage();
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivNoti:
			Intent i = new Intent(mContext, ActivityNew.class);
			i.putExtra("login", true);
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_left);
			break;
		case R.id.layout_balance:
			if (!getBalance) {
				return;
			}
			if (mProfile.accountType == 1) {
				startActivity(new Intent(mContext, ActivityBasicAccount.class));
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_left);
			} else if (mProfile.accountType == 2) {
				startActivity(new Intent(mContext, ActivityBasicDebit.class));
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_left);
			}
			break;
		case R.id.layout_data:
			if (get3gData) {
				startActivity(new Intent(mContext, ActivityBasic3G.class));
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_left);
			}
			break;
		case R.id.layout_offer:
			if (getOffer) {
				startActivity(new Intent(mContext, ActivityBasicOffer.class));
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_left);
			}
			break;
		case R.id.layout_vas:
			startActivity(new Intent(mContext, ActivityVas.class));
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_left);
			break;
		case R.id.layout_transaction:
			if (!getBalance) {
				return;
			}
			if (mProfile.accountType == 1) {
				mContext.startActivity(new Intent(mContext,
						ActivityRefillHistory.class));
				((Activity) mContext).overridePendingTransition(
						R.anim.slide_in_left, R.anim.slide_out_left);
				return;
			}
			mContext.startActivity(new Intent(mContext,
					ActivityDebitHistory.class));
			((Activity) mContext).overridePendingTransition(
					R.anim.slide_in_left, R.anim.slide_out_left);
			break;
		default:
			break;
		}
	}
}
