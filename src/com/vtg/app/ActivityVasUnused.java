package com.vtg.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.vtg.app.component.AdapterTypeVas;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelTypeVas;
import com.vtg.app.model.ModelVas;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.JSONParser;
import com.vtg.app.util.CommonDefine.MyService;
import com.vtg.app.util.CommonDefine.PreferenceKey;

public class ActivityVasUnused extends Activity implements CommonDefine {
	private ExpandableListView elvVas;
	private Context mContext;
	private List<ModelTypeVas> listTypeVas;
	private ProgressDialog pDialog;
	private SharedPreferences preferences;

	/* renamed from: com.vtg.app.ActivityVasUnused.1 */
	class C05951 implements OnClickListener {
		C05951() {
		}

		public void onClick(View v) {
			ActivityVasUnused.this.actionBack();
		}
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(1);
		setRequestedOrientation(7);
		setContentView(R.layout.activity_vas);
		this.mContext = this;
		preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
		initView();
	}

	private void initView() {
		pDialog = new ProgressDialog(this.mContext);
		pDialog.setCancelable(false);
		((ImageView) findViewById(R.id.ivBack))
				.setOnClickListener(new C05951());
		elvVas = (ExpandableListView) findViewById(R.id.elv_vas);
		new AsyntaskGetVas().execute();
	}

	private class AsyntaskGetVas extends AsyncTask<String, String, Boolean> {
		private AsyntaskGetVas() {
		}

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setMessage(mContext.getString(R.string.message_loading));
			pDialog.show();
			listTypeVas = new ArrayList<ModelTypeVas>();
		}

		protected Boolean doInBackground(String... arg0) {
			try {
				String url = MyService.VAS;
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
				for (int i = 0; i < jsonArr.length(); i++) {
					ModelTypeVas type = new ModelTypeVas();
					type.name = jsonArr.getJSONObject(i).getString("name");
					type.listVas = new ArrayList<ModelVas>();
					JSONArray vArr = jsonArr.getJSONObject(i).getJSONArray(
							"content");
					for (int k = 0; k < vArr.length(); k++) {
						ModelVas vas = new ModelVas();
						vas.id = vArr.getJSONObject(k).getString("id");
						vas.name = vArr.getJSONObject(k).getString("vas_name");
						vas.code = vArr.getJSONObject(k).getString("vas_code");
						vas.description = vArr.getJSONObject(k).getString(
								mXML.DESCRIPTION);
						vas.guide = vArr.getJSONObject(k).getString("guide");
						vas.status = vArr.getJSONObject(k).getInt(mXML.STATUS);
						vas.receiverIsdn = vArr.getJSONObject(k).getString(
								"receiver_isdn");
						vas.regis = vArr.getJSONObject(k).getString(
								"fake_mo_register");
						vas.remove = vArr.getJSONObject(k).getString(
								"fake_mo_cancel");
						vas.fee = vArr.getJSONObject(k).getString("fee");
						if (!ActivityVas.listUsedCode.contains(vas.code)) {
							type.listVas.add(vas);
						}
					}
					if (type.listVas.size() > 0) {
						listTypeVas.add(type);
					}
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
			elvVas.setAdapter(new AdapterTypeVas(mContext, listTypeVas, true));
			for (int i = 0; i < listTypeVas.size(); i++) {
				elvVas.expandGroup(i);
			}
		}
	}

	private void actionBack() {
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

	public void onBackPressed() {
		actionBack();
	}
}
