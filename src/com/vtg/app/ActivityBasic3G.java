package com.vtg.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;
import org.w3c.dom.NodeList;

import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vtg.app.component.AdapterBalanceInfo;
import com.vtg.app.component.AdapterBanner;
import com.vtg.app.component.DialogConfirm;
import com.vtg.app.component.DialogReport;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelBalance;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelSubData;
import com.vtg.app.model.ModelTag;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.JSONParser;
import com.vtg.app.util.SOAPUtil;
import com.vtg.app.util.XMLParser;
import com.vtg.app.util.CommonDefine.mXML;

public class ActivityBasic3G extends FragmentActivity implements CommonDefine,
		OnClickListener {
	private static final String TAG = "ActivityBasic3G";
	public static String packCode;
	public static String dataType = "";
	private AdapterBanner adapterBanner;
	private RelativeLayout btnCancel;
	private RelativeLayout btnDataDetail;
	private RelativeLayout btnOther;
	Configuration config;
	private ImageView ivBack;
	LinearLayout layoutOther;
	private String locate;
	ListView lvOther;
	private Context mContext;
	private ViewPager pagerBanner;
	private SharedPreferences preferences;
	private BasicDataReceiver receiver;
	private TextView tvDataValue;
	ProgressDialog pDialog;
	private RelativeLayout layoutHighSpeed;
	private TextView tvHighSpeed;
	private TextView tvHighExpirity;

	/* renamed from: com.vtg.app.1 */
	class C05361 implements Runnable {
		C05361() {
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

	/* renamed from: com.vtg.app.2 */
	class C05372 implements OnClickListener {
		private final/* synthetic */DialogConfirm val$cfDialog;

		C05372(DialogConfirm dialogConfirm) {
			val$cfDialog = dialogConfirm;
		}

		public void onClick(View v) {
			val$cfDialog.dismiss();
			new AsyncTaskDeactive().execute(new String[0]);
		}
	}

	private class AsyncTaskBalanceInfo extends
			AsyncTask<String, String, String> {
		List<ModelBalance> listBalances;

		private AsyncTaskBalanceInfo() {
		}

		protected void onPreExecute() {
			super.onPreExecute();
			listBalances = new ArrayList();
		}

		protected String doInBackground(String... p) {
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
				params.add(new ModelParam("listId", "4561,4560,4500"));
				SOAPUtil soap = new SOAPUtil(WSCode.GET_SUB_ACC, tags, params);
				soap.makeSOAPRequest();
				NodeList listId = soap.mDoc.getElementsByTagName("balanceId");
				NodeList listExpire = soap.mDoc
						.getElementsByTagName("balanceExpire");
				NodeList listName = soap.mDoc
						.getElementsByTagName("balanceName");
				NodeList listValue = soap.mDoc
						.getElementsByTagName("balanceValue");
				int i = 0;
				while (i < 10) {
					if (!XMLParser.getElementValue(listId.item(i)).trim()
							.equals("")) {
						ModelBalance balance = new ModelBalance();
						balance.id = XMLParser.getElementValue(listId.item(i));
						balance.expire = XMLParser.getElementValue(listExpire
								.item(i));
						balance.name = XMLParser.getElementValue(listName
								.item(i));
						balance.val = Float.parseFloat(XMLParser
								.getElementValue(listValue.item(i)));

						if (balance.id.equals("4560")
								|| balance.val > 0) {
							FunctionHelper.checkBalanceExist(listBalances,
									balance);
						}
					}
					i++;
				}
				for (ModelBalance balance : listBalances) {
					balance.value = FunctionHelper.formatFloatData(balance.val
							/ (1024 * 1024));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (listBalances.size() > 0) {
				lvOther.setAdapter(new AdapterBalanceInfo(mContext,
						listBalances));
				FunctionHelper.setListViewHeightBasedOnChildren(lvOther);
				// layoutOther.setVisibility(0);
			} else {
				layoutOther.setVisibility(8);
			}
			if (dataType.equals(ModelSubData.UNLIMITED)) {
				layoutHighSpeed.setVisibility(View.VISIBLE);
				new AsyncTaskHighSpeedData().execute();
			} else {
				layoutHighSpeed.setVisibility(View.GONE);
				pDialog.dismiss();
			}
		}
	}

	private class AsyncTaskHighSpeedData extends
			AsyncTask<String, String, String> {
		List<ModelBalance> listBalances;

		private AsyncTaskHighSpeedData() {
		}

		protected void onPreExecute() {
			super.onPreExecute();
			listBalances = new ArrayList();
		}

		protected String doInBackground(String... p) {
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
				SOAPUtil soap = new SOAPUtil(WSCode.HIGH_SPEED, tags, params);
				soap.makeSOAPRequest();
				String highData = soap.getValue("qtBalance");
				String[] spl = highData.split("\\&");
				int result = 0;
				for (int i = 0; i < spl.length; i++) {
					try {
						result += Integer.parseInt(spl[i]);
					} catch (Exception e) {

					}
				}
				return FunctionHelper.formatFloatData((float) result / 1024)
						+ "MB";
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			tvHighSpeed.setText(result);
			tvHighExpirity.setText(ActivityMain.dataExpirity);
		}
	}

	private class AsyncTaskGetSubInfo extends
			AsyncTask<String, String, Boolean> {
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				JSONParser jParser = new JSONParser();
				HashMap<String, String> map = new HashMap();
				map.put("code ", ActivityMain.dataCode);
				String url = MyService.SUB_DATA_INFO + "?code="
						+ ActivityMain.dataCode + "&" + CommonDefine.SIGNATURE
						+ "=" + FunctionHelper.makeSignatureAPT(map);
				Log.v("", "tiench url: " + url);
				String json = jParser.getJSONFromUrl(url,
						CommonDefine.METHOD_GET, null);
				Log.d("", "tiench data type: " + json);
				dataType = new JSONObject(json).getJSONObject("data")
						.getString("data_type");
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			new AsyncTaskBalanceInfo().execute();
		}

	}

	private class AsyncTaskDeactive extends AsyncTask<String, String, Boolean> {
		String message;

		private AsyncTaskDeactive() {
			message = mContext.getString(R.string.err_connect);
		}

		protected Boolean doInBackground(String... prs) {
			try {
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam("msisdn", new StringBuilder(
						CommonDefine.NUMBER_HEADER).append(
						preferences.getString(PreferenceKey.PHONE_NUMBER, ""))
						.toString()));
				params.add(new ModelParam("send_sms", "0"));
				params.add(new ModelParam("requestId", FunctionHelper
						.formatCurrentTime()));
				params.add(new ModelParam("command", "OFF"));
				SOAPUtil soap = new SOAPUtil(WSCode.REMOVE_MI, tags, params);
				soap.makeSOAPRequest();
				if (soap.getError() != 0) {
					message = mContext.getString(R.string.message_fail);
					return Boolean.valueOf(false);
				} else if (Integer.parseInt(soap.getValue(mXML.ERR_CODE)) == 0) {
					message = soap.getValue(mXML.MESSAGE);
					return Boolean.valueOf(true);
				} else {
					message = soap.getValue(mXML.MESSAGE);
					return Boolean.valueOf(false);
				}
			} catch (Exception e) {
				return Boolean.valueOf(false);
			}
		}

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.show();
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if (result.booleanValue()) {
				Intent intent = new Intent();
				intent.setAction(CommonDefine.MY_PACKAGE);
				intent.putExtra(Action.ACTION, Action.RELOAD_DATA);
				mContext.sendBroadcast(intent);
			}
			new DialogReport(mContext, message).show();
		}
	}

	private class BasicDataReceiver extends BroadcastReceiver {
		private BasicDataReceiver() {
		}

		public void onReceive(Context context, Intent intent) {
			if (intent.getExtras().getString(Action.ACTION)
					.equals(Action.DONE_RELOAD_DATA)) {
				setBasicAccount();
			}
		}
	}

	public ActivityBasic3G() {
		locate = "";
	}

	static {
		packCode = "";
	}

	protected void onCreate(@Nullable Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(1);
		setRequestedOrientation(7);
		mContext = this;
		preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
		receiver = new BasicDataReceiver();
		registerReceiver(receiver, new IntentFilter(CommonDefine.MY_PACKAGE));

		pDialog = new ProgressDialog(mContext);
		pDialog.setMessage(mContext.getString(R.string.message_loading));
		pDialog.setCancelable(false);
		Log.v(TAG, "tiench onCreated");
	}

	private void initBanner() {
		pagerBanner = (ViewPager) findViewById(R.id.pager_banner);
		adapterBanner = new AdapterBanner(getSupportFragmentManager());
		pagerBanner.setAdapter(adapterBanner);
		changeBanner();
	}

	private void changeBanner() {
		new Handler().postDelayed(new C05361(), 10000);
	}

	private void activityCreate() {
		setContentView(R.layout.activity_basic_3g);
		initView();
		initBanner();
	}

	private void initView() {
		ivBack = (ImageView) findViewById(R.id.ivBack);
		ivBack.setOnClickListener(this);
		tvDataValue = (TextView) findViewById(R.id.tv_data_value);
		btnCancel = (RelativeLayout) findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(this);
		btnDataDetail = (RelativeLayout) findViewById(R.id.btn_data_detail);
		btnDataDetail.setOnClickListener(this);
		btnOther = (RelativeLayout) findViewById(R.id.btn_other_plan);
		btnOther.setOnClickListener(this);
		layoutOther = (LinearLayout) findViewById(R.id.layout_other);
		lvOther = (ListView) findViewById(R.id.lv_other_balance);
		layoutHighSpeed = (RelativeLayout) findViewById(R.id.layout_high_speed);
		tvHighSpeed = (TextView) findViewById(R.id.tv_high_speed_data);
		tvHighExpirity = (TextView) findViewById(R.id.tv_high_speed_expirity);
		setBasicAccount();
		new AsyncTaskGetSubInfo().execute();
	}

	private void setBasicAccount() {
		packCode = ActivityMain.dataCode;
		Log.v(TAG, "tiench data: " + packCode);
		tvDataValue.setText(ActivityMain.data3g);
		if (packCode.equals("")) {
			btnCancel.setVisibility(8);
		} else {
			btnCancel.setVisibility(0);
		}
	}

	private void actionBack() {
		try {
			unregisterReceiver(receiver);
		} catch (Exception e) {
		}
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

	public void onBackPressed() {
		actionBack();
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
		case R.id.ivBack:
			actionBack();
			break;
		case R.id.btn_data_detail:
			break;
		case R.id.btn_cancel:
			Log.v(TAG, "tiench pack code: " + packCode);
			if (packCode.equals("")) {
				new DialogReport(mContext,
						"You have not been register Mobile Internet Service. Thank you!")
						.show();
			} else {
				removeData();
			}
			break;
		case R.id.btn_other_plan:
			startActivity(new Intent(mContext, ActivityBuyPackage.class));
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_left);
			break;
		default:
			break;
		}
	}

	private void removeData() {
		DialogConfirm cfDialog = new DialogConfirm(mContext,
				getString(R.string.cf_unregis) + " " + packCode + "?");
		cfDialog.btnYes.setOnClickListener(new C05372(cfDialog));
		cfDialog.show();
	}
}
