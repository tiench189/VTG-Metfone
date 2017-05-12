package com.vtg.app;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.vtg.app.ActivityMain.mProfile;
import com.vtg.app.component.AdapterBanner;
import com.vtg.app.component.AdapterMap;
import com.vtg.app.component.DialogUpdateProfile;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelId;
import com.vtg.app.model.ModelMap;
import com.vtg.app.model.ModelTag;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.Action;
import com.vtg.app.util.CommonDefine.PreferenceKey;
import com.vtg.app.util.CommonDefine.SoapTag;
import com.vtg.app.util.CommonDefine.WSCode;
import com.vtg.app.util.SOAPUtil;
import com.vtg.app.util.XMLParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.w3c.dom.NodeList;

public class ActivityProfiles extends FragmentActivity implements CommonDefine {
	public static List<ModelId> listIds;
	private AdapterBanner adapterBanner;
	private RelativeLayout btnHistory;
	private RelativeLayout btnRegister;
	private ImageView ivBack;
	private ImageView ivUpdate;
	private List<ModelMap> listMap;
	private ListView lvProfile;
	private Context mContext;
	private ProgressDialog pDialog;
	private ViewPager pagerBanner;
	private SharedPreferences preferences;
	private ProfileReceiver receiver;

	/* renamed from: com.vtg.app.ActivityProfiles.1 */
	class C05661 implements Runnable {
		C05661() {
		}

		public void run() {
			int current = ActivityProfiles.this.pagerBanner.getCurrentItem();
			if (current < 3) {
				current++;
			} else {
				current = 0;
			}
			ActivityProfiles.this.pagerBanner.setCurrentItem(current);
			ActivityProfiles.this.changeBanner();
		}
	}

	/* renamed from: com.vtg.app.ActivityProfiles.2 */
	class C05672 implements OnClickListener {
		C05672() {
		}

		public void onClick(View v) {
			ActivityProfiles.this.actionBack();
		}
	}

	/* renamed from: com.vtg.app.ActivityProfiles.3 */
	class C05683 implements OnClickListener {
		C05683() {
		}

		public void onClick(View v) {
			if (ActivityProfiles.listIds.size() > 0) {
				new DialogUpdateProfile(ActivityProfiles.this.mContext).show();
			} else {
				new AsyncTaskGetId().execute(new String[0]);
			}
		}
	}

	/* renamed from: com.vtg.app.ActivityProfiles.4 */
	class C05694 implements OnClickListener {
		C05694() {
		}

		public void onClick(View v) {
			ActivityProfiles.this.startActivity(new Intent(
					ActivityProfiles.this.mContext,
					ActivityRegisterProfile.class));
			ActivityProfiles.this.overridePendingTransition(
					R.anim.slide_in_left, R.anim.slide_out_left);
		}
	}

	private class AsyncTaskGetId extends AsyncTask<String, String, Boolean> {
		private AsyncTaskGetId() {
		}

		protected Boolean doInBackground(String... p) {
			try {
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
				SOAPUtil soap = new SOAPUtil(WSCode.GET_LIST_ID, tags,
						new ArrayList());
				soap.makeSOAPRequest();
				NodeList listCode = soap.mDoc.getElementsByTagName("typeCode");
				NodeList listName = soap.mDoc.getElementsByTagName("typeName");
				for (int i = 0; i < listCode.getLength(); i++) {
					if (!XMLParser.getElementValue(listName.item(i)).trim()
							.equals("")) {
						ActivityProfiles.listIds.add(new ModelId(XMLParser
								.getElementValue(listCode.item(i)), XMLParser
								.getElementValue(listName.item(i))));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPreExecute() {
			super.onPreExecute();
			ActivityProfiles.this.pDialog.show();
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			ActivityProfiles.this.pDialog.dismiss();
			new DialogUpdateProfile(ActivityProfiles.this.mContext).show();
		}
	}

	private class ProfileReceiver extends BroadcastReceiver {
		private ProfileReceiver() {
		}

		public void onReceive(Context context, Intent intent) {
			if (intent.getExtras().getString(Action.ACTION)
					.equals(Action.RELOAD_PROFILE_DONE)) {
				ActivityProfiles.this.initView();
			}
		}
	}

	protected void onCreate(@Nullable Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(1);
		setRequestedOrientation(7);
		this.mContext = this;
		this.preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
		setLanguage();
		setContentView(R.layout.activity_profile);
		this.pDialog = new ProgressDialog(this.mContext);
		this.pDialog.setCancelable(false);
		this.pDialog.setMessage(getString(R.string.message_loading));
		listIds = new ArrayList();
		this.receiver = new ProfileReceiver();
		registerReceiver(this.receiver, new IntentFilter(
				CommonDefine.MY_PACKAGE));
		initView();
		initBanner();
	}

	private void initBanner() {
		this.pagerBanner = (ViewPager) findViewById(R.id.pager_banner);
		this.adapterBanner = new AdapterBanner(getSupportFragmentManager());
		this.pagerBanner.setAdapter(this.adapterBanner);
		changeBanner();
	}

	private void changeBanner() {
		new Handler().postDelayed(new C05661(), 10000);
	}

	public void initView() {
		this.ivBack = (ImageView) findViewById(R.id.ivBack);
		this.ivBack.setOnClickListener(new C05672());
		this.lvProfile = (ListView) findViewById(R.id.lv_profile);
		this.listMap = new ArrayList();
		this.listMap.add(new ModelMap(getString(R.string.number),
				this.preferences.getString(PreferenceKey.PHONE_NUMBER, "")));
		this.listMap.add(new ModelMap(getString(R.string.full_name),
				mProfile.userUsing));
		// if (mProfile.accountType == 1) {
		// this.listMap.add(new ModelMap(getString(R.string.account_type),
		// getString(R.string.prepaid)));
		// } else {
		// this.listMap.add(new ModelMap(getString(R.string.account_type),
		// getString(R.string.postpaid)));
		// }
		this.listMap.add(new ModelMap(getString(R.string.account_type),
				mProfile.productCode));
		this.listMap.add(new ModelMap(getString(R.string.start_datetime),
				mProfile.startTime));
		this.listMap.add(new ModelMap(getString(R.string.address),
				mProfile.address));
		this.listMap.add(new ModelMap(getString(R.string.birthdate),
				mProfile.birthDay));
		this.lvProfile.setAdapter(new AdapterMap(this.mContext, this.listMap));
		this.ivUpdate = (ImageView) findViewById(R.id.iv_edit);
		this.ivUpdate.setOnClickListener(new C05683());
		this.btnRegister = (RelativeLayout) findViewById(R.id.btn_register);
		this.btnRegister.setOnClickListener(new C05694());
		if (mProfile.cusID.equals("")) {
			if (mProfile.accountType == 1) {
				this.btnRegister.setVisibility(0);
			} else {
				this.btnRegister.setVisibility(8);
			}
			this.ivUpdate.setVisibility(8);
			return;
		}
		this.btnRegister.setVisibility(8);
		this.ivUpdate.setVisibility(0);
	}

	private void actionBack() {
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

	public void setLanguage() {
		Locale locale = new Locale(this.preferences.getString(
				PreferenceKey.LOCATE, "en"));
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
	}

	protected void onStart() {
		super.onStart();
	}

	public void onBackPressed() {
		actionBack();
	}

	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(this.receiver);
		} catch (Exception e) {
		}
	}
}
