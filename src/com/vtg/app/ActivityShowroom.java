package com.vtg.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.vtg.app.component.AdapterProvince;
import com.vtg.app.component.AdapterShowroom;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelProvince;
import com.vtg.app.model.ModelShowroom;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.JSONParser;
import com.vtg.app.util.CommonDefine.MyService;
import com.vtg.app.util.CommonDefine.PreferenceKey;

public class ActivityShowroom extends Activity implements CommonDefine {
	public static ModelShowroom crShowroom;
	private List<ModelProvince> listProvince;
	private List<ModelShowroom> listShowroom;
	private ListView lvShowrrom;
	private Context mContext;
	private ProgressDialog pDialog;
	private SharedPreferences preferences;
	private Spinner spProvince;

	/* renamed from: com.vtg.app.ActivityShowroom.1 */
	class C05921 implements OnClickListener {
		C05921() {
		}

		public void onClick(View v) {
			ActivityShowroom.this.actionBack();
		}
	}

	/* renamed from: com.vtg.app.ActivityShowroom.2 */
	class C05932 implements OnItemSelectedListener {
		C05932() {
		}

		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {
			if (position == 0) {
				ActivityShowroom.this.listShowroom = new ArrayList();
				ActivityShowroom.this.lvShowrrom
						.setAdapter(new AdapterShowroom(
								ActivityShowroom.this.mContext,
								ActivityShowroom.this.listShowroom));
				return;
			}
			new AsyntaskGetShowroom()
					.execute(new String[] { ((ModelProvince) ActivityShowroom.this.listProvince
							.get(position)).id });
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
		}
	}

	private class AsyntaskGetProvince extends
			AsyncTask<String, String, Boolean> {
		private AsyntaskGetProvince() {
		}

		protected void onPreExecute() {
			super.onPreExecute();
			ActivityShowroom.this.pDialog.show();
			ActivityShowroom.this.listProvince = new ArrayList();
			ActivityShowroom.this.listProvince.add(new ModelProvince("0",
					getString(R.string.choose_province)));
		}

		protected Boolean doInBackground(String... arg0) {
			try {
				String url = MyService.GET_PROVINCE;
				HashMap<String, String> map = new HashMap();
				if (preferences.getString(PreferenceKey.LOCATE, "en").equals(
						"vi")) {
					map.put("lang ", "kh");
					url += "?lang=kh&signature="
							+ FunctionHelper.makeSignatureAPT(map);
				} else {
					url += "?signature=" + FunctionHelper.makeSignatureAPT(map);
				}
				JSONArray jsonArr = new JSONObject(
						new JSONParser().getJSONFromUrl(url,
								CommonDefine.METHOD_GET, null))
						.getJSONArray("data");
				Log.v("", "tiench province: " + jsonArr.toString());
				for (int i = 0; i < jsonArr.length(); i++) {
					ModelProvince province = new ModelProvince();
					province.id = jsonArr.getJSONObject(i).getString("id");
					province.name = jsonArr.getJSONObject(i).getString(
							"province_name");
					ActivityShowroom.this.listProvince.add(province);
				}
				return Boolean.valueOf(true);
			} catch (JSONException e) {
				e.printStackTrace();
				return Boolean.valueOf(false);
			}
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			ActivityShowroom.this.pDialog.dismiss();
			ActivityShowroom.this.spProvince.setAdapter(new AdapterProvince(
					ActivityShowroom.this.mContext,
					ActivityShowroom.this.listProvince));
		}
	}

	private class AsyntaskGetShowroom extends
			AsyncTask<String, String, Boolean> {
		private AsyntaskGetShowroom() {
		}

		protected void onPreExecute() {
			super.onPreExecute();
			ActivityShowroom.this.pDialog.show();
			ActivityShowroom.this.listShowroom = new ArrayList();
		}

		protected Boolean doInBackground(String... arg0) {
			try {
				String provinceID = arg0[0];
				JSONParser jParser = new JSONParser();
				HashMap<String, String> map = new HashMap();
				map.put("province ", provinceID);
				String url = "";
				if (preferences.getString(PreferenceKey.LOCATE, "en").equals(
						"vi")) {
					map.put("lang ", "kh");
					url = MyService.SHOWROOM + "?province=" + provinceID
							+ "&lang=kh&" + CommonDefine.SIGNATURE + "="
							+ FunctionHelper.makeSignatureAPT(map);
				} else {
					url = MyService.SHOWROOM + "?province=" + provinceID + "&"
							+ CommonDefine.SIGNATURE + "="
							+ FunctionHelper.makeSignatureAPT(map);
				}
				Log.v("", "tiench url: " + url);
				String json = jParser.getJSONFromUrl(url,
						CommonDefine.METHOD_GET, null);
				Log.d("", "tiench showroom: " + json);
				JSONArray jsonArr = new JSONObject(json).getJSONArray("data");
				for (int i = 0; i < jsonArr.length(); i++) {
					ModelShowroom sr = new ModelShowroom();
					sr.name = jsonArr.getJSONObject(i).getString(
							"showroom_name");
					sr.phone = jsonArr.getJSONObject(i).getString("phone");
					sr.address = jsonArr.getJSONObject(i).getString(
							"description");
					sr.latitude = Double.parseDouble(jsonArr.getJSONObject(i)
							.getString("lat"));
					sr.longtitude = Double.parseDouble(jsonArr.getJSONObject(i)
							.getString("long"));
					sr.image = jsonArr.getJSONObject(i).getString("image");
					Log.v("", "tiench sr image: " + sr.image);
					ActivityShowroom.this.listShowroom.add(sr);
				}
				return Boolean.valueOf(true);
			} catch (JSONException e) {
				e.printStackTrace();
				return Boolean.valueOf(false);
			}
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			ActivityShowroom.this.pDialog.dismiss();
			ActivityShowroom.this.lvShowrrom.setAdapter(new AdapterShowroom(
					ActivityShowroom.this.mContext,
					ActivityShowroom.this.listShowroom));
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

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(1);
		setRequestedOrientation(7);
		this.mContext = this;
		this.preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
		setLanguage();
		setContentView(R.layout.activity_showroom);
		this.pDialog = new ProgressDialog(this.mContext);
		this.pDialog.setCancelable(false);
		this.pDialog.setMessage(this.mContext
				.getString(R.string.message_loading));
		initView();
	}

	private void initView() {
		((ImageView) findViewById(R.id.ivBack))
				.setOnClickListener(new C05921());
		this.spProvince = (Spinner) findViewById(R.id.spProvine);
		this.spProvince.setOnItemSelectedListener(new C05932());
		this.lvShowrrom = (ListView) findViewById(R.id.lvShowroom);
		new AsyntaskGetProvince().execute(new String[0]);
	}

	private void actionBack() {
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

	public void onBackPressed() {
		actionBack();
	}
}
