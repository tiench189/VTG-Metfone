package com.vtg.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NodeList;

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
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vtg.app.component.AdapterTypeVas;
import com.vtg.app.component.AdapterVasUsed;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelTag;
import com.vtg.app.model.ModelTypeVas;
import com.vtg.app.model.ModelVas;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.JSONParser;
import com.vtg.app.util.SOAPUtil;
import com.vtg.app.util.XMLParser;
import com.vtg.app.util.CommonDefine.MyService;
import com.vtg.app.util.CommonDefine.PreferenceKey;

public class ActivityVas extends Activity implements CommonDefine {
	public static List<ModelTypeVas> listTypeVas;
	private AdapterTypeVas adapterTypeVas;
	private AdapterVasUsed adapterVasUsed;
	private List<ModelVas> listVasUsed;
	private ExpandableListView elvVas;
	private TextView tvTitle;
	private ListView lvVas;
	private Context mContext;
	private ProgressDialog pDialog;
	private SharedPreferences preferences;
	private VasReceiver receiver;
	private HashMap<String, String> hmUsedTime;
	public static List<String> listUsedCode;

	/* renamed from: com.vtg.app.ActivityVas.1 */
	class C05941 implements OnClickListener {
		C05941() {
		}

		public void onClick(View v) {
			actionBack();
		}
	}

	private class AsyncTaskSyncVas extends AsyncTask<String, String, String> {
		private AsyncTaskSyncVas() {
		}

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setMessage(mContext.getString(R.string.message_loading));
			pDialog.show();
			listUsedCode = new ArrayList<String>();
			hmUsedTime = new HashMap<String, String>();
		}

		protected String doInBackground(String... p) {
			try {
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam(mXML.ISDN, NUMBER_HEADER
						+ preferences.getString(PreferenceKey.PHONE_NUMBER, "")));
				SOAPUtil soap = new SOAPUtil(WSCode.SYNC_VAS, tags, params);
				soap.makeSOAPRequest();
				NodeList listCode = soap.mDoc.getElementsByTagName(mXML.CODE);
				NodeList listDate = soap.mDoc.getElementsByTagName("date");
				for (int i = 0; i < listCode.getLength(); i++) {
					if (!XMLParser.getElementValue(listCode.item(i)).trim()
							.equals("")) {
						String code = XMLParser.getElementValue(listCode
								.item(i));
						String time = FunctionHelper.convertDateFormat(
								XMLParser.getElementValue(listDate.item(i)),
								"yyyy-MM-dd HH:mm:ss.S", "dd/MM/yyyy HH:mm:ss");
						listUsedCode.add(code);
						hmUsedTime.put(code, time);
						// moveCodeUsed(code, time);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			new AsyncTaskUsedVas().execute();
		}
	}

	private class AsyncTaskUsedVas extends AsyncTask<String, String, Boolean> {
		protected void onPreExecute() {
			super.onPreExecute();
			listVasUsed = new ArrayList<ModelVas>();
		}

		protected Boolean doInBackground(String... arg0) {
			try {
				String codes = "";
				for (int i = 0; i < listUsedCode.size(); i++) {
					codes += listUsedCode.get(i);
					if (i < listUsedCode.size() - 1) {
						codes += ",";
					}
				}
				HashMap<String, String> map = new HashMap();
				map.put("code ", codes);
				String url = "";
				if (preferences.getString(PreferenceKey.LOCATE, "en").equals(
						"vi")) {
					map.put("lang ", "kh");
					url = MyService.USED_VAS + "?code=" + codes + "&lang=kh&"
							+ CommonDefine.SIGNATURE + "="
							+ FunctionHelper.makeSignatureAPT(map);
				} else {
					url = MyService.USED_VAS + "?code=" + codes + "&"
							+ CommonDefine.SIGNATURE + "="
							+ FunctionHelper.makeSignatureAPT(map);
				}
				Log.v("", "tiench url: " + url);
				JSONArray vArr = new JSONObject(
						new JSONParser().getJSONFromUrl(url,
								CommonDefine.METHOD_GET, null))
						.getJSONArray("data");
				for (int i = 0; i < vArr.length(); i++) {
					ModelVas vas = new ModelVas();
					vas.id = vArr.getJSONObject(i).getString("id");
					vas.name = vArr.getJSONObject(i).getString("vas_name");
					vas.code = vArr.getJSONObject(i).getString("vas_code");
					vas.description = vArr.getJSONObject(i).getString(
							mXML.DESCRIPTION);
					vas.guide = vArr.getJSONObject(i).getString("guide");
					vas.status = vArr.getJSONObject(i).getInt(mXML.STATUS);
					vas.receiverIsdn = vArr.getJSONObject(i).getString(
							"receiver_isdn");
					vas.regis = vArr.getJSONObject(i).getString(
							"fake_mo_register");
					vas.remove = vArr.getJSONObject(i).getString(
							"fake_mo_cancel");
					vas.fee = vArr.getJSONObject(i).getString("fee");
					vas.register_date = hmUsedTime.get(vas.code);
					listVasUsed.add(vas);
				}
				return Boolean.valueOf(true);
			} catch (Exception e) {
				e.printStackTrace();
				return Boolean.valueOf(false);
			}
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (listVasUsed.size() > 0) {
				pDialog.dismiss();
				adapterVasUsed = new AdapterVasUsed(mContext, listVasUsed);
				lvVas.setAdapter(adapterVasUsed);
				elvVas.setVisibility(View.GONE);
				lvVas.setVisibility(View.VISIBLE);
				tvTitle.setText(getString(R.string.you_use_vas));
				return;
			}
			new AsyntaskGetVas().execute();
		}
	}

	private class AsyntaskGetVas extends AsyncTask<String, String, Boolean> {
		private AsyntaskGetVas() {
		}

		protected void onPreExecute() {
			super.onPreExecute();
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
						if (!listUsedCode.contains(vas.code)) {
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
			adapterTypeVas = new AdapterTypeVas(mContext, listTypeVas, false);
			elvVas.setAdapter(adapterTypeVas);
			elvVas.setVisibility(View.VISIBLE);
			lvVas.setVisibility(View.GONE);
			for (int i = 0; i < listTypeVas.size(); i++) {
				elvVas.expandGroup(i);
			}
			tvTitle.setText(getString(R.string.value_added_services));
		}
	}

	private class VasReceiver extends BroadcastReceiver {
		private VasReceiver() {
		}

		public void onReceive(Context context, Intent intent) {
			Bundle mBundle = intent.getExtras();
			if (mBundle.containsKey(Action.ACTION)
					&& mBundle.getString(Action.ACTION).equals(
							Action.RELOAD_VAS)) {
				new AsyncTaskSyncVas().execute();
			}
		}
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(1);
		setRequestedOrientation(7);
		setContentView(R.layout.activity_vas);
		this.mContext = this;
		this.preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
		this.receiver = new VasReceiver();
		registerReceiver(this.receiver, new IntentFilter(
				CommonDefine.MY_PACKAGE));
		initView();
	}

	private void initView() {
		tvTitle = (TextView) findViewById(R.id.tv_title);

		this.pDialog = new ProgressDialog(this.mContext);
		this.pDialog.setCancelable(false);
		((ImageView) findViewById(R.id.ivBack))
				.setOnClickListener(new C05941());
		this.lvVas = (ListView) findViewById(R.id.lv_vas);
		elvVas = (ExpandableListView) findViewById(R.id.elv_vas);
		new AsyncTaskSyncVas().execute(new String[0]);
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

	private void moveCodeUsed(String code, String date) {
		for (int i = 0; i < listTypeVas.size(); i++) {
			for (int k = 0; k < listTypeVas.get(i).listVas.size(); k++) {
				if ((listTypeVas.get(i).listVas.get(k)).code.equals(code)) {
					listTypeVas.get(i).listVas.get(k).register_date = date;
					this.listVasUsed.add(listTypeVas.get(i).listVas.get(k));
					listTypeVas.get(i).listVas.remove(k);
				}
			}
		}

		for (int i = 0; i < listTypeVas.size(); i++) {
			if (listTypeVas.get(i).listVas.size() == 0) {
				listTypeVas.remove(i);
			}
		}

	}
}
