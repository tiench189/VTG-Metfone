package com.vtg.app;

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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.vtg.app.component.AdapterBalanceInfo;
import com.vtg.app.component.AdapterBanner;
import com.vtg.app.component.DialogConfirm;
import com.vtg.app.component.DialogTranfer;
import com.vtg.app.component.DialogUpPay;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelBalance;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelTag;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.Action;
import com.vtg.app.util.CommonDefine.PreferenceKey;
import com.vtg.app.util.CommonDefine.SoapTag;
import com.vtg.app.util.CommonDefine.WSCode;
import com.vtg.app.util.CommonDefine.mXML;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.SOAPUtil;
import com.vtg.app.util.XMLParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.w3c.dom.NodeList;

public class ActivityBasicAccount extends FragmentActivity implements
		CommonDefine, OnClickListener {
	private static final String TAG = "ActivityBasicAccount";
	private AdapterBanner adapterBanner;
	private RelativeLayout btnTopUp;
	private RelativeLayout btnTranfer;
	Configuration config;
	private ImageView ivBack;
	private RelativeLayout layoutBasicAccount;
	private String locate;
	private ListView lvOther;
	private Context mContext;
	private ViewPager pagerBanner;
	private SharedPreferences preferences;
	private BasicAccountReceiver receiver;
	private TextView tvBasicExpire;
	private TextView tvBasicValue;

	/* renamed from: com.vtg.app.ActivityBasicAccount.1 */
	class C05381 implements Runnable {
		C05381() {
		}

		public void run() {
			int current = ActivityBasicAccount.this.pagerBanner
					.getCurrentItem();
			if (current < 3) {
				current++;
			} else {
				current = 0;
			}
			ActivityBasicAccount.this.pagerBanner.setCurrentItem(current);
			ActivityBasicAccount.this.changeBanner();
		}
	}

	/* renamed from: com.vtg.app.ActivityBasicAccount.2 */
	class C05392 implements OnClickListener {
		private final/* synthetic */DialogConfirm val$cfUpPay;

		C05392(DialogConfirm dialogConfirm) {
			this.val$cfUpPay = dialogConfirm;
		}

		public void onClick(View v) {
			this.val$cfUpPay.dismiss();
			ActivityBasicAccount.this.showDialogUpPay(true);
		}
	}

	/* renamed from: com.vtg.app.ActivityBasicAccount.3 */
	class C05403 implements OnClickListener {
		private final/* synthetic */DialogConfirm val$cfUpPay;

		C05403(DialogConfirm dialogConfirm) {
			this.val$cfUpPay = dialogConfirm;
		}

		public void onClick(View v) {
			this.val$cfUpPay.dismiss();
			ActivityBasicAccount.this.showDialogUpPay(false);
		}
	}

	private class AsyncTaskBalanceInfo extends
			AsyncTask<String, String, String> {
		List<ModelBalance> listBalances;

		private AsyncTaskBalanceInfo() {
		}

		protected void onPreExecute() {
			super.onPreExecute();
			this.listBalances = new ArrayList();
		}

		protected String doInBackground(String... p) {
			try {
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam(mXML.ISDN, new StringBuilder(
						CommonDefine.NUMBER_HEADER).append(
						ActivityBasicAccount.this.preferences.getString(
								PreferenceKey.PHONE_NUMBER, "")).toString()));
				params.add(new ModelParam(
						"listId",
						"4400,4046,4000,4001,4200,2500,2113,2100,2400,2501,2505,2504,2001,2004,2003,2002"));
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
					if (!XMLParser.getElementValue(listName.item(i)).trim()
							.equals("")
							&& Float.parseFloat(XMLParser
									.getElementValue(listValue.item(i))) > 0.0f) {
						ModelBalance balance = new ModelBalance();
						balance.id = XMLParser.getElementValue(listId.item(i));
						balance.expire = XMLParser.getElementValue(listExpire
								.item(i));
						balance.name = XMLParser.getElementValue(listName
								.item(i));
						balance.val = Float.parseFloat(XMLParser
								.getElementValue(listValue.item(i)));
						FunctionHelper.checkBalanceExist(listBalances, balance);
					}
					i++;
				}
				for (ModelBalance balance : listBalances) {
					balance.value = FunctionHelper
							.formatMoney("" + balance.val);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			ActivityBasicAccount.this.lvOther
					.setAdapter(new AdapterBalanceInfo(
							ActivityBasicAccount.this.mContext,
							this.listBalances));
			FunctionHelper
					.setListViewHeightBasedOnChildren(ActivityBasicAccount.this.lvOther);
		}
	}

	private class BasicAccountReceiver extends BroadcastReceiver {
		private BasicAccountReceiver() {
		}

		public void onReceive(Context context, Intent intent) {
			if (intent.getExtras().getString(Action.ACTION)
					.equals(Action.DONE_RELOAD_ACCOUNT)) {
				ActivityBasicAccount.this.setBasicAccount();
			}
		}
	}

	public ActivityBasicAccount() {
		this.locate = "";
	}

	protected void onCreate(@Nullable Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(1);
		setRequestedOrientation(7);
		this.mContext = this;
		this.preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
		this.receiver = new BasicAccountReceiver();
		registerReceiver(this.receiver, new IntentFilter(
				CommonDefine.MY_PACKAGE));
		Log.v(TAG, "tiench onCreated");
	}

	private void initBanner() {
		this.pagerBanner = (ViewPager) findViewById(R.id.pager_banner);
		this.adapterBanner = new AdapterBanner(getSupportFragmentManager());
		this.pagerBanner.setAdapter(this.adapterBanner);
		changeBanner();
	}

	private void changeBanner() {
		new Handler().postDelayed(new C05381(), 10000);
	}

	private void activityCreate() {
		setContentView(R.layout.activity_basic_account);
		initView();
		initBanner();
	}

	private void initView() {
		((ImageView) findViewById(R.id.ivBack)).setOnClickListener(this);
		this.tvBasicValue = (TextView) findViewById(R.id.tv_basic_value);
		this.tvBasicExpire = (TextView) findViewById(R.id.tv_basic_expire);
		this.btnTopUp = (RelativeLayout) findViewById(R.id.btn_top_up);
		this.btnTopUp.setOnClickListener(this);
		this.btnTranfer = (RelativeLayout) findViewById(R.id.btn_tranfer);
		this.btnTranfer.setOnClickListener(this);
		this.layoutBasicAccount = (RelativeLayout) findViewById(R.id.layout_basic_account);
		setBasicAccount();
		this.lvOther = (ListView) findViewById(R.id.lv_other_balance);
		new AsyncTaskBalanceInfo().execute(new String[0]);
	}

	private void setBasicAccount() {
		this.tvBasicValue.setText(new StringBuilder(String
				.valueOf(ActivityMain.basicAcc.value)).append(" USD")
				.toString());
		this.tvBasicExpire.setText(ActivityMain.basicAcc.expire);
	}

	private void actionBack() {
		try {
			unregisterReceiver(this.receiver);
		} catch (Exception e) {
		}
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

	public void onBackPressed() {
		actionBack();
	}

	public void setLanguage() {
		Locale locale = new Locale(this.preferences.getString(
				PreferenceKey.LOCATE, "en"));
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
		activityCreate();
	}

	protected void onStart() {
		super.onStart();
		if (!this.preferences.getString(PreferenceKey.LOCATE, "en").equals(
				this.locate)) {
			this.locate = this.preferences
					.getString(PreferenceKey.LOCATE, "en");
			setLanguage();
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivBack:
			actionBack();
			break;
		case R.id.btn_top_up:
			showConfirmUpPay();
			break;
		case R.id.btn_tranfer:
			showDialogTranfer();
			break;
		default:
			break;
		}
	}

	private void showDialogTranfer() {
		new DialogTranfer(this.mContext).show();
	}

	private void showConfirmUpPay() {
		DialogConfirm cfUpPay = new DialogConfirm(this.mContext,
				getString(R.string.confirm_up_pay));
		cfUpPay.tvYes.setText(getString(R.string.you));
		cfUpPay.tvNo.setText(getString(R.string.anyone));
		cfUpPay.btnYes.setOnClickListener(new C05392(cfUpPay));
		cfUpPay.btnNo.setOnClickListener(new C05403(cfUpPay));
		cfUpPay.show();
	}

	private void showDialogUpPay(boolean isMe) {
		DialogUpPay dlUpPay = new DialogUpPay(this.mContext, false);
		if (isMe) {
			dlUpPay.edtNumber.setText(this.preferences.getString(
					PreferenceKey.PHONE_NUMBER, ""));
			dlUpPay.edtNumber.setVisibility(8);
		}
		dlUpPay.show();
	}
}
