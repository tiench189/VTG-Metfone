package com.vtg.app;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.load.Key;
import com.vtg.app.metfone.R;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.JSONParser;
import com.vtg.app.util.CommonDefine.MyService;
import com.vtg.app.util.CommonDefine.PreferenceKey;

public class ActivityTariff extends FragmentActivity implements CommonDefine,
		OnClickListener {
	private static final String TAG = "ActivityBasicAccount";
	String about;
	private ImageView ivBack;
	private Context mContext;
	private ProgressDialog pDialog;
	private SharedPreferences preferences;
	private WebView wv;

	private class AsyntaskAbout extends AsyncTask<String, String, Boolean> {
		private AsyntaskAbout() {
		}

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(mContext);
			pDialog.setCancelable(false);
			pDialog.setMessage(mContext.getString(R.string.message_loading));
			pDialog.show();
		}

		protected Boolean doInBackground(String... arg0) {
			try {
				String url = MyService.TARIFF;
				HashMap<String, String> map = new HashMap();
				if (preferences.getString(PreferenceKey.LOCATE, "en").equals(
						"vi")) {
					map.put("lang ", "kh");
					url += "?lang=kh&signature="
							+ FunctionHelper.makeSignatureAPT(map);
				} else {
					url += "?signature=" + FunctionHelper.makeSignatureAPT(map);
				}
				Log.v(TAG, "tiench url: " + url);
				JSONObject jsonObj = new JSONObject(
						new JSONParser().getJSONFromUrl(url,
								CommonDefine.METHOD_GET, null));
				about = jsonObj.getString("data");
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
				wv.getSettings().setJavaScriptEnabled(true);
				wv.loadDataWithBaseURL("", about, "text/html",
						Key.STRING_CHARSET_NAME, "");
			}
		}
	}

	public ActivityTariff() {
		about = "";
	}

	protected void onCreate(@Nullable Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(1);
		setRequestedOrientation(7);
		setContentView(R.layout.activity_tariff);
		mContext = this;
		preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
		initView();
		new AsyntaskAbout().execute(new String[0]);
	}

	private void initView() {
		((ImageView) findViewById(R.id.ivBack)).setOnClickListener(this);
		wv = (WebView) findViewById(R.id.wv);
	}

	private void actionBack() {
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

	public void onBackPressed() {
		actionBack();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivBack:
			actionBack();
			break;
		default:
			break;
		}
	}
}
