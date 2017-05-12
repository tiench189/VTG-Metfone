package com.vtg.app.component;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.vtg.app.ActivityMain;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelTag;
import com.vtg.app.model.ModelVas;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.Action;
import com.vtg.app.util.CommonDefine.PreferenceKey;
import com.vtg.app.util.CommonDefine.SoapTag;
import com.vtg.app.util.CommonDefine.WSCode;
import com.vtg.app.util.CommonDefine.mXML;
import com.vtg.app.util.SOAPUtil;
import java.util.ArrayList;
import java.util.List;

public class AdapterVasUnused extends BaseAdapter implements CommonDefine {
	private LayoutInflater inflater;
	private List<ModelVas> listVas;
	private Context mContext;
	private SharedPreferences preferences;
	private boolean isActivity;

	/* renamed from: com.vtg.app.component.AdapterVasUnused.1 */
	class C06141 implements OnClickListener {
		private final/* synthetic */ModelVas val$vas;

		/* renamed from: com.vtg.app.component.AdapterVasUnused.1.1 */
		class C06131 implements OnClickListener {
			private final/* synthetic */DialogConfirm val$cfDialog;
			private final/* synthetic */ModelVas val$vas;

			C06131(DialogConfirm dialogConfirm, ModelVas modelVas) {
				this.val$cfDialog = dialogConfirm;
				this.val$vas = modelVas;
			}

			public void onClick(View v) {
				this.val$cfDialog.dismiss();
				new AsyncTaskRegister()
						.execute(new ModelVas[] { this.val$vas });
			}
		}

		C06141(ModelVas modelVas) {
			this.val$vas = modelVas;
		}

		public void onClick(View v) {
			DialogConfirm cfDialog = new DialogConfirm(
					AdapterVasUnused.this.mContext,
					mContext.getString(R.string.cf_regis) + " "
							+ this.val$vas.name + "?");
			cfDialog.btnYes.setOnClickListener(new C06131(cfDialog,
					this.val$vas));
			cfDialog.show();
		}
	}

	private class AsyncTaskRegister extends
			AsyncTask<ModelVas, String, Boolean> {
		String message;
		ProgressDialog pDialog;

		/*
		 * renamed from:
		 * com.vtg.app.component.AdapterVasUnused.AsyncTaskRegister.1
		 */
		class C06151 implements OnClickListener {
			private final/* synthetic */DialogReport val$sc;

			C06151(DialogReport dialogReport) {
				this.val$sc = dialogReport;
			}

			public void onClick(View v) {
				this.val$sc.dismiss();
				if (isActivity) {
					((Activity) AdapterVasUnused.this.mContext).finish();
				}
			}
		}

		private AsyncTaskRegister() {
			this.message = mContext.getString(R.string.fail_fake_mo);
		}

		protected Boolean doInBackground(ModelVas... prs) {
			try {
				ModelVas vas = prs[0];
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam(mXML.ISDN,
						AdapterVasUnused.this.preferences.getString(
								PreferenceKey.PHONE_NUMBER, "")));
				params.add(new ModelParam("receiverIsdn", vas.receiverIsdn));
				params.add(new ModelParam("command", vas.regis));
				SOAPUtil soap = new SOAPUtil(WSCode.REGIS_VAS_FAKE_MO, tags,
						params);
				soap.makeSOAPRequest();
				if (soap.getError() != 0) {
					this.message = mContext.getString(R.string.fail_fake_mo);
					return Boolean.valueOf(false);
				} else if (Integer.parseInt(soap.getValue(mXML.CODE)) == 1) {
					this.message = mContext.getString(R.string.success_fake_mo);
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
			this.pDialog = new ProgressDialog(AdapterVasUnused.this.mContext);
			this.pDialog.setMessage(AdapterVasUnused.this.mContext
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
				intent.putExtra(Action.ACTION, Action.RELOAD_VAS);
				AdapterVasUnused.this.mContext.sendBroadcast(intent);
				DialogReport sc = new DialogReport(
						AdapterVasUnused.this.mContext, this.message);
				sc.btnOK.setOnClickListener(new C06151(sc));
				sc.show();
				return;
			}
			new DialogReport(AdapterVasUnused.this.mContext, this.message)
					.show();
		}
	}

	private class Holder {
		RelativeLayout btnRegister;
		TextView tvGuide;
		TextView tvName;
		TextView tvPrice;

		private Holder() {
		}
	}

	public AdapterVasUnused(Context context, List<ModelVas> listVas,
			boolean isActivity) {
		this.listVas = listVas;
		this.isActivity = isActivity;
		this.mContext = context;
		this.inflater = ((Activity) context).getLayoutInflater();
		this.preferences = ((Activity) context).getSharedPreferences(
				CommonDefine.MY_PACKAGE, 0);
	}

	public int getCount() {
		return this.listVas.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		View view = convertView;
		if (view == null) {
			view = this.inflater.inflate(R.layout.row_vas_unused, null);
			holder = new Holder();
			holder.tvName = (TextView) view.findViewById(R.id.tv_name);
			holder.tvPrice = (TextView) view.findViewById(R.id.tv_price);
			holder.tvGuide = (TextView) view.findViewById(R.id.tv_guide);
			holder.btnRegister = (RelativeLayout) view
					.findViewById(R.id.btn_register);
			view.setTag(holder);
		}
		ModelVas vas = (ModelVas) this.listVas.get(position);
		holder = (Holder) view.getTag();
		holder.tvName.setText(vas.name);
		holder.tvPrice.setText(vas.fee);
		holder.tvGuide.setText(vas.guide);
		holder.btnRegister.setOnClickListener(new C06141(vas));
		return view;
	}
}
