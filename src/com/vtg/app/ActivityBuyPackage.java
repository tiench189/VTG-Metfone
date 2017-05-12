package com.vtg.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.vtg.app.component.AdapterSubData;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelSubData;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.JSONParser;
import com.vtg.app.util.CommonDefine.PreferenceKey;

public class ActivityBuyPackage extends Activity implements CommonDefine {
	private AdapterSubData adapterSubAddon;
	private AdapterSubData adapterSubLimit;
	private AdapterSubData adapterSubSocial;
	private AdapterSubData adapterSubUnlimit;
	private List<ModelSubData> listSubAddon;
	private List<ModelSubData> listSubLimit;
	private List<ModelSubData> listSubSocial;
	private List<ModelSubData> listSubUnlimit;
	private ListView lvSubAddon;
	private ListView lvSubLimit;
	private ListView lvSubSocial;
	private ListView lvSubUnlimit;
	private Context mContext;
	private ProgressDialog pDialog;
	private SharedPreferences preferences;
	private BasicDataReceiver receiver;

	/* renamed from: com.vtg.app.ActivityBuyPackage.1 */
	class C05481 implements OnClickListener {
		C05481() {
		}

		public void onClick(View v) {
			actionBack();
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
	}

	private class AsyntaskGetSubData extends AsyncTask<String, String, Boolean> {
		private AsyntaskGetSubData() {
		}

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(mContext);
			pDialog.setCancelable(false);
			pDialog.setMessage(mContext.getString(R.string.message_loading));
			pDialog.show();
			listSubLimit = new ArrayList();
			listSubUnlimit = new ArrayList();
			listSubAddon = new ArrayList();
			listSubSocial = new ArrayList();
		}

		protected Boolean doInBackground(String... arg0) {
			String strJson = new JSONParser().getJSONFromUrl(
					MyService.GET_SUB_DATA, CommonDefine.METHOD_GET, null);
			Log.v("JSON PROVINCE", strJson);
			try {
				int i;
				ModelSubData sub;
				JSONObject jsonObj = new JSONObject(strJson);
				JSONArray jsonArrLimit = jsonObj.getJSONObject("data")
						.getJSONArray(ModelSubData.LIMITED);
				for (i = 0; i < jsonArrLimit.length(); i++) {
					sub = new ModelSubData();
					sub.name = jsonArrLimit.getJSONObject(i).getString(
							"pack_name");
					sub.code = jsonArrLimit.getJSONObject(i)
							.getString(mXML.CODE).trim();
					sub.value = jsonArrLimit.getJSONObject(i).getString(
							mXML.DESCRIPTION);
					sub.price = jsonArrLimit.getJSONObject(i)
							.getString("price");
					sub.type = jsonArrLimit.getJSONObject(i).getString("type")
							.trim();
					sub.data_type = jsonArrLimit.getJSONObject(i).getString(
							"data_type");
					sub.data = jsonArrLimit.getJSONObject(i).getString("data");
					sub.time = jsonArrLimit.getJSONObject(i).getString("time");
					if (!sub.code.equals(ActivityMain.dataCode)) {
						listSubLimit.add(sub);
					}
				}
				JSONArray jsonArrUnlimit = jsonObj.getJSONObject("data")
						.getJSONArray(ModelSubData.UNLIMITED);
				for (i = 0; i < jsonArrUnlimit.length(); i++) {
					sub = new ModelSubData();
					sub.name = jsonArrUnlimit.getJSONObject(i).getString(
							"pack_name");
					sub.code = jsonArrUnlimit.getJSONObject(i)
							.getString(mXML.CODE).trim();
					sub.value = jsonArrUnlimit.getJSONObject(i).getString(
							mXML.DESCRIPTION);
					sub.price = jsonArrUnlimit.getJSONObject(i).getString(
							"price");
					sub.type = jsonArrUnlimit.getJSONObject(i)
							.getString("type").trim();
					sub.data_type = jsonArrUnlimit.getJSONObject(i).getString(
							"data_type");
					sub.data = jsonArrUnlimit.getJSONObject(i)
							.getString("data");
					sub.time = jsonArrUnlimit.getJSONObject(i)
							.getString("time");
					listSubUnlimit.add(sub);
				}
				JSONArray jsonArrAddon = jsonObj.getJSONObject("data")
						.getJSONArray(ModelSubData.ADD_ON_PLAN);
				for (i = 0; i < jsonArrAddon.length(); i++) {
					sub = new ModelSubData();
					sub.name = jsonArrAddon.getJSONObject(i).getString(
							"pack_name");
					sub.code = jsonArrAddon.getJSONObject(i)
							.getString(mXML.CODE).trim();
					sub.value = jsonArrAddon.getJSONObject(i).getString(
							mXML.DESCRIPTION);
					sub.price = jsonArrAddon.getJSONObject(i)
							.getString("price");
					sub.type = jsonArrAddon.getJSONObject(i).getString("type")
							.trim();
					sub.data_type = jsonArrAddon.getJSONObject(i).getString(
							"data_type");
					sub.data = jsonArrAddon.getJSONObject(i).getString("data");
					sub.time = jsonArrAddon.getJSONObject(i).getString("time");
					listSubAddon.add(sub);
				}
				JSONArray jsonArrSocial = jsonObj.getJSONObject("data")
						.getJSONArray(ModelSubData.SOCIAL_PACKAGE);
				for (i = 0; i < jsonArrSocial.length(); i++) {
					sub = new ModelSubData();
					sub.name = jsonArrSocial.getJSONObject(i).getString(
							"pack_name");
					sub.code = jsonArrSocial.getJSONObject(i)
							.getString(mXML.CODE).trim();
					sub.value = jsonArrSocial.getJSONObject(i).getString(
							mXML.DESCRIPTION);
					sub.price = jsonArrSocial.getJSONObject(i).getString(
							"price");
					sub.type = jsonArrSocial.getJSONObject(i).getString("type")
							.trim();
					sub.data_type = jsonArrSocial.getJSONObject(i).getString(
							"data_type");
					sub.data = jsonArrSocial.getJSONObject(i).getString("data");
					sub.time = jsonArrSocial.getJSONObject(i).getString("time");
					listSubSocial.add(sub);
				}
				return Boolean.valueOf(true);
			} catch (JSONException e) {
				e.printStackTrace();
				return Boolean.valueOf(false);
			}
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if (result.booleanValue()) {
				Log.v("", "tiench: " + listSubLimit.size() + "/"
						+ listSubUnlimit.size());
				adapterSubLimit = new AdapterSubData(mContext, listSubLimit);
				lvSubLimit.setAdapter(adapterSubLimit);
				FunctionHelper.setListViewHeightBasedOnChildren(lvSubLimit);
				adapterSubUnlimit = new AdapterSubData(mContext, listSubUnlimit);
				lvSubUnlimit.setAdapter(adapterSubUnlimit);
				FunctionHelper.setListViewHeightBasedOnChildren(lvSubUnlimit);
				adapterSubAddon = new AdapterSubData(mContext, listSubAddon);
				lvSubAddon.setAdapter(adapterSubAddon);
				FunctionHelper.setListViewHeightBasedOnChildren(lvSubAddon);
				adapterSubSocial = new AdapterSubData(mContext, listSubSocial);
				lvSubSocial.setAdapter(adapterSubSocial);
				FunctionHelper.setListViewHeightBasedOnChildren(lvSubSocial);
			}
		}
	}

	private class BasicDataReceiver extends BroadcastReceiver {
		private BasicDataReceiver() {
		}

		public void onReceive(Context context, Intent intent) {
			if (intent.getExtras().getString(Action.ACTION)
					.equals(Action.DONE_RELOAD_DATA)) {
				try {
					adapterSubLimit.notifyDataSetChanged();
					adapterSubUnlimit.notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(1);
		setRequestedOrientation(7);
		mContext = this;
		preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
		setLanguage();
		setContentView(R.layout.activity_buy_data);

		receiver = new BasicDataReceiver();
		registerReceiver(receiver, new IntentFilter(CommonDefine.MY_PACKAGE));
		initView();
	}

	private void initView() {
		((ImageView) findViewById(R.id.ivBack))
				.setOnClickListener(new C05481());
		lvSubLimit = (ListView) findViewById(R.id.lv_sub_limit);
		lvSubUnlimit = (ListView) findViewById(R.id.lv_sub_unlimit);
		lvSubAddon = (ListView) findViewById(R.id.lv_sub_aop);
		lvSubSocial = (ListView) findViewById(R.id.lv_sub_social);
		new AsyntaskGetSubData().execute(new String[0]);
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
}
