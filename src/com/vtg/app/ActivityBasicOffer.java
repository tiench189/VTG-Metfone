package com.vtg.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import com.vtg.app.component.AdapterSubOffer;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelOffer;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.PreferenceKey;
import com.vtg.app.util.CommonDefine.mXML;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.JSONParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityBasicOffer extends Activity implements CommonDefine {
	private AdapterSubOffer adapterOffer;
	private List<ModelOffer> listOffer;
	private ListView lvPromotion;
	private Context mContext;
	private ProgressDialog pDialog;
	private SharedPreferences preferences;
	private PromotionReceiver receiver;

	/* renamed from: com.vtg.app.ActivityBasicOffer.1 */
	class C05441 implements OnClickListener {
		C05441() {
		}

		public void onClick(View v) {
			actionBack();
		}
	}

	private class AsyntaskGetPromotion extends
			AsyncTask<String, String, Boolean> {
		private AsyntaskGetPromotion() {
		}

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(mContext);
			pDialog.setCancelable(false);
			pDialog.setMessage(mContext.getString(R.string.message_loading));
			pDialog.show();
			listOffer = new ArrayList();
		}

		protected Boolean doInBackground(String... arg0) {
			JSONParser jsonParser = new JSONParser();
			HashMap<String, String> map = new HashMap();
			map.put("phone",
					new StringBuilder(CommonDefine.NUMBER_HEADER).append(
							preferences.getString(PreferenceKey.PHONE_NUMBER,
									"")).toString());
			String url = "";
			if (preferences.getString(PreferenceKey.LOCATE, "en").equals("vi")) {
				map.put("lang ", "kh");
				url = "http://api.truonglx.me/api/promote/list?phone=855"
						+ preferences.getString(PreferenceKey.PHONE_NUMBER, "")
						+ "&lang=kh" + "&signature="
						+ FunctionHelper.makeSignatureAPT(map);
			} else {
				url = "http://api.truonglx.me/api/promote/list?phone=855"
						+ preferences.getString(PreferenceKey.PHONE_NUMBER, "")
						+ "&signature=" + FunctionHelper.makeSignatureAPT(map);
			}
			Log.v("", "tiench offer api: " + url);
			String strJson = jsonParser.getJSONFromUrl(url,
					CommonDefine.METHOD_GET, null);
			Log.i("", "tiench offer: " + strJson);
			try {
				JSONArray jsonArr = new JSONObject(strJson)
						.getJSONArray("data");
				for (int i = 0; i < jsonArr.length(); i++) {
					ModelOffer prom = new ModelOffer();
					prom.name = jsonArr.getJSONObject(i).getString(
							"promote_name");
					prom.code = jsonArr.getJSONObject(i).getString(
							"promote_code");
					prom.description = jsonArr.getJSONObject(i).getString(
							mXML.DESCRIPTION);
					prom.guide = jsonArr.getJSONObject(i).getString("guide");
					prom.fakeMO = jsonArr.getJSONObject(i).getInt("fake_mo");
					prom.receiverIsdn = jsonArr.getJSONObject(i).getString(
							"receiver_isdn");
					prom.regis = jsonArr.getJSONObject(i).getString(
							"fake_mo_register");
					prom.remove = jsonArr.getJSONObject(i).getString(
							"fake_mo_cancel");
					prom.start = jsonArr.getJSONObject(i).getString(
							"start_date");
					prom.end = jsonArr.getJSONObject(i).getString("end_date");
					listOffer.add(prom);
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
				adapterOffer = new AdapterSubOffer(mContext, listOffer);
				lvPromotion.setAdapter(adapterOffer);
			}
		}
	}

	private class PromotionReceiver extends BroadcastReceiver {
		private PromotionReceiver() {
		}

		public void onReceive(Context context, Intent intent) {
			Bundle mBundle = intent.getExtras();
		}
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(1);
		setRequestedOrientation(7);
		setContentView(R.layout.activity_promotion);
		mContext = this;
		preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
		receiver = new PromotionReceiver();
		registerReceiver(receiver, new IntentFilter(CommonDefine.MY_PACKAGE));
		initView();
	}

	private void initView() {
		((ImageView) findViewById(R.id.ivBack))
				.setOnClickListener(new C05441());
		lvPromotion = (ListView) findViewById(R.id.lv_promotion);
		new AsyntaskGetPromotion().execute(new String[0]);
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
