package com.vtg.app.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vtg.app.ActivityMain;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelMoreData;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelSubData;
import com.vtg.app.model.ModelTag;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.JSONParser;
import com.vtg.app.util.SOAPUtil;

public class AdapterSubData extends BaseAdapter implements CommonDefine {
	private Context context;
	private LayoutInflater inflater;
	private List<ModelSubData> listSub;
	private SharedPreferences preferences;

	/* renamed from: com.vtg.app.component.AdapterSubData.1 */
	class C06031 implements OnClickListener {
		private final/* synthetic */ModelSubData val$sub;

		/* renamed from: com.vtg.app.component.AdapterSubData.1.1 */
		class C06021 implements OnClickListener {
			private final/* synthetic */DialogConfirm val$cfDialog;
			private final/* synthetic */ModelSubData val$sub;

			C06021(DialogConfirm dialogConfirm, ModelSubData modelSubData) {
				this.val$cfDialog = dialogConfirm;
				this.val$sub = modelSubData;
			}

			public void onClick(View v) {
				this.val$cfDialog.dismiss();
				Log.v("", "tiench regis: " + this.val$sub.data_type);
				if (this.val$sub.data_type.equals(ModelSubData.ADD_ON_PLAN) || this.val$sub.data_type.equals(ModelSubData.SOCIAL_PACKAGE)) {
					new AsyncTaskAddOn()
							.execute(new String[] { this.val$sub.code });
				} else {
					new AsyncTaskActive()
							.execute(new String[] { this.val$sub.code });
				}
			}
		}

		C06031(ModelSubData modelSubData) {
			this.val$sub = modelSubData;
		}

		public void onClick(View v) {
			DialogConfirm cfDialog = new DialogConfirm(
					AdapterSubData.this.context,
					context.getString(R.string.cf_regis) + " "
							+ this.val$sub.name + "?");
			cfDialog.btnYes.setOnClickListener(new C06021(cfDialog,
					this.val$sub));
			cfDialog.show();
		}
	}

	/* renamed from: com.vtg.app.component.AdapterSubData.2 */
	class C06042 implements OnClickListener {
		private final/* synthetic */ModelSubData val$sub;

		C06042(ModelSubData modelSubData) {
			this.val$sub = modelSubData;
		}

		public void onClick(View v) {
			new AsyncTaskMore().execute(new String[] { this.val$sub.code });
		}
	}

	private class AsyncTaskAddOn extends AsyncTask<String, String, Boolean> {
		String message;
		ProgressDialog pDialog;

		private AsyncTaskAddOn() {
			this.message = context.getString(R.string.err_connect);
		}

		protected Boolean doInBackground(String... prs) {
			try {
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam(mXML.ISDN, preferences.getString(
						PreferenceKey.PHONE_NUMBER, "")));
				params.add(new ModelParam("receiverIsdn", "133"));
				params.add(new ModelParam("command", prs[0]));
				SOAPUtil soap = new SOAPUtil(WSCode.REGIS_VAS_FAKE_MO, tags,
						params);
				soap.makeSOAPRequest();
				if (soap.getError() != 0) {
					this.message = context.getString(R.string.fail_fake_mo);
					return Boolean.valueOf(false);
				} else if (Integer.parseInt(soap.getValue(mXML.CODE)) == 1) {
					this.message = context.getString(R.string.success_fake_mo);
					return Boolean.valueOf(true);
				} else {
					this.message = soap.getValue(mXML.MESSAGE);
					return Boolean.valueOf(false);
				}
			} catch (Exception e) {
				return Boolean.valueOf(false);
			}
		}

		protected void onPreExecute() {
			super.onPreExecute();
			this.pDialog = new ProgressDialog(AdapterSubData.this.context);
			this.pDialog.setMessage(AdapterSubData.this.context
					.getString(R.string.message_loading));
			this.pDialog.setCancelable(false);
			this.pDialog.show();
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			this.pDialog.dismiss();
			if (result.booleanValue()) {
				Intent intent = new Intent();
				intent.setAction(CommonDefine.MY_PACKAGE);
				intent.putExtra(Action.ACTION, Action.RELOAD_DATA);
				AdapterSubData.this.context.sendBroadcast(intent);
			}
			new DialogReport(AdapterSubData.this.context, this.message).show();
		}
	}

	private class AsyncTaskActive extends AsyncTask<String, String, Boolean> {
		String message;
		ProgressDialog pDialog;

		private AsyncTaskActive() {
			this.message = context.getString(R.string.err_connect);
		}

		protected Boolean doInBackground(String... prs) {
			try {
				String code = prs[0];
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam("msisdn", new StringBuilder(
						CommonDefine.NUMBER_HEADER).append(
						AdapterSubData.this.preferences.getString(
								PreferenceKey.PHONE_NUMBER, "")).toString()));
				params.add(new ModelParam("send_sms", "1"));
				params.add(new ModelParam("requestId", FunctionHelper
						.formatCurrentTime()));
				params.add(new ModelParam("command", code));
				params.add(new ModelParam(Action.ACTION, "1"));
				SOAPUtil soap = new SOAPUtil(WSCode.REGISTER_MI, tags, params);
				soap.makeSOAPRequest();
				if (soap.getError() != 0) {
					this.message = context.getString(R.string.message_fail);
					return Boolean.valueOf(false);
				} else if (Integer.parseInt(soap.getValue(mXML.ERR_CODE)) == 0) {
					this.message = soap.getValue(mXML.MESSAGE);
					return Boolean.valueOf(true);
				} else {
					this.message = soap.getValue(mXML.MESSAGE);
					return Boolean.valueOf(false);
				}
			} catch (Exception e) {
				return Boolean.valueOf(false);
			}
		}

		protected void onPreExecute() {
			super.onPreExecute();
			this.pDialog = new ProgressDialog(AdapterSubData.this.context);
			this.pDialog.setMessage(AdapterSubData.this.context
					.getString(R.string.message_loading));
			this.pDialog.setCancelable(false);
			this.pDialog.show();
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			this.pDialog.dismiss();
			if (result.booleanValue()) {
				Intent intent = new Intent();
				intent.setAction(CommonDefine.MY_PACKAGE);
				intent.putExtra(Action.ACTION, Action.RELOAD_DATA);
				AdapterSubData.this.context.sendBroadcast(intent);
			}
			new DialogReport(AdapterSubData.this.context, this.message).show();
		}
	}

	private class AsyncTaskMore extends AsyncTask<String, String, Boolean> {
		List<ModelMoreData> listMoreData;
		ProgressDialog pDialog;

		private AsyncTaskMore() {
		}

		protected Boolean doInBackground(String... params) {
			try {
				this.listMoreData = new ArrayList();
				String subName = params[0];
				JSONParser jParser = new JSONParser();
				HashMap<String, String> map = new HashMap();
				map.put("pack", subName);
				String url = "http://api.truonglx.me/api/packextent/list?pack="
						+ subName + "&signature="
						+ FunctionHelper.makeSignatureAPT(map);
				Log.v("", "tiench get extend: " + url);
				JSONObject obj = new JSONObject(jParser.getJSONFromUrl(url,
						CommonDefine.METHOD_GET, null));
				if (obj.getInt(mXML.STATUS) != 1) {
					return Boolean.valueOf(false);
				}
				JSONArray arr = obj.getJSONArray("data");
				for (int i = 0; i < arr.length(); i++) {
					JSONObject more = arr.getJSONObject(i);
					ModelMoreData data = new ModelMoreData();
					data.code = more.getString(mXML.CODE);
					data.price = more.getString("price");
					data.value = more.getString("data");
					this.listMoreData.add(data);
				}
				return Boolean.valueOf(true);
			} catch (Exception e) {
				e.printStackTrace();
				return Boolean.valueOf(false);
			}
		}

		protected void onPreExecute() {
			super.onPreExecute();
			this.pDialog = new ProgressDialog(AdapterSubData.this.context);
			this.pDialog.setCancelable(false);
			this.pDialog.show();
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			this.pDialog.dismiss();
			if (result.booleanValue()) {
				new DialogMoreData(AdapterSubData.this.context,
						this.listMoreData).show();
			} else {
				new DialogReport(AdapterSubData.this.context,
						context.getString(R.string.err_connect)).show();
			}
		}
	}

	private class Holder {
		private RelativeLayout btnMore;
		private RelativeLayout btnRegister;
		private TextView tvName;
		private TextView tvPrice;
		private TextView tvTime;
		private TextView tvAction;

		private Holder() {
		}
	}

	public AdapterSubData(Context context, List<ModelSubData> listSub) {
		this.context = context;
		this.listSub = listSub;
		this.inflater = ((Activity) context).getLayoutInflater();
		this.preferences = ((Activity) context).getSharedPreferences(
				CommonDefine.MY_PACKAGE, 0);
	}

	public int getCount() {
		return this.listSub.size();
	}

	public ModelSubData getItem(int position) {
		return (ModelSubData) this.listSub.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		View view = convertView;
		if (view == null) {
			view = this.inflater.inflate(R.layout.row_sub_data, null);
			holder = new Holder();
			holder.tvName = (TextView) view.findViewById(R.id.tv_name);
			holder.tvPrice = (TextView) view.findViewById(R.id.tv_price);
			holder.tvTime = (TextView) view.findViewById(R.id.tv_time);
			holder.tvAction = (TextView) view.findViewById(R.id.tv_action);
			holder.btnMore = (RelativeLayout) view.findViewById(R.id.btn_more);
			holder.btnRegister = (RelativeLayout) view
					.findViewById(R.id.btn_register);
			view.setTag(holder);
		}
		holder = (Holder) view.getTag();
		ModelSubData sub = getItem(position);
		if (sub.data_type.equals(ModelSubData.LIMITED)) {
			holder.tvName
					.setText(sub.name + "   "
							+ context.getString(R.string.data_volume) + ": "
							+ sub.data);
		} else if (sub.data_type.equals(ModelSubData.UNLIMITED)
				|| sub.data_type.equals(ModelSubData.ADD_ON_PLAN)) {
			holder.tvName.setText(sub.name + "   "
					+ context.getString(R.string.high_speed_data) + ": "
					+ sub.data);
		} else if (sub.data_type.equals(ModelSubData.SOCIAL_PACKAGE)) {
			holder.tvName.setText(sub.name + "   " + sub.data);
		}
		holder.tvPrice.setText(context.getString(R.string.fee) + ": "
				+ sub.price);
		if (sub.data_type.equals(ModelSubData.ADD_ON_PLAN)
				|| sub.time.equals("")) {
			holder.tvTime.setText("");
		} else {
			holder.tvTime.setText(context.getString(R.string.validity) + ": "
					+ sub.time);
		}
		// if (sub.code.equals(ActivityMain.dataCode)) {
		// holder.btnMore.setVisibility(0);
		// holder.btnRegister.setVisibility(8);
		// } else {
		holder.btnMore.setVisibility(8);
		holder.btnRegister.setVisibility(0);
		// }
		if (sub.data_type.equals(ModelSubData.ADD_ON_PLAN)) {
			holder.tvAction.setText(context.getString(R.string.buy_more));
		} else {
			holder.tvAction.setText(context.getString(R.string.register));
		}

		holder.btnRegister.setOnClickListener(new C06031(sub));
		holder.btnMore.setOnClickListener(new C06042(sub));
		return view;
	}
}
