package com.vtg.app.component;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.vtg.app.model.ModelOffer;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelTag;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.PreferenceKey;
import com.vtg.app.util.CommonDefine.SoapTag;
import com.vtg.app.util.CommonDefine.WSCode;
import com.vtg.app.util.CommonDefine.mXML;
import com.vtg.app.util.SOAPUtil;
import java.util.ArrayList;
import java.util.List;

public class AdapterSubOffer extends BaseAdapter implements CommonDefine {
	private LayoutInflater inflater;
	private List<ModelOffer> listSub;
	private Context mContext;
	private SharedPreferences preferences;

	/* renamed from: com.vtg.app.component.AdapterSubOffer.1 */
	class C06061 implements OnClickListener {
		private final/* synthetic */ModelOffer val$sub;

		/* renamed from: com.vtg.app.component.AdapterSubOffer.1.1 */
		class C06051 implements OnClickListener {
			private final/* synthetic */DialogConfirm val$cfDialog;
			private final/* synthetic */ModelOffer val$sub;

			C06051(DialogConfirm dialogConfirm, ModelOffer modelOffer) {
				this.val$cfDialog = dialogConfirm;
				this.val$sub = modelOffer;
			}

			public void onClick(View v) {
				this.val$cfDialog.dismiss();
				new AsyncTaskRegister()
						.execute(new ModelOffer[] { this.val$sub });
			}
		}

		C06061(ModelOffer modelOffer) {
			this.val$sub = modelOffer;
		}

		public void onClick(View v) {
			DialogConfirm cfDialog = new DialogConfirm(
					AdapterSubOffer.this.mContext,
					mContext.getString(R.string.cf_regis) + " "
							+ this.val$sub.name + "?");
			cfDialog.btnYes.setOnClickListener(new C06051(cfDialog,
					this.val$sub));
			cfDialog.show();
		}
	}

	/* renamed from: com.vtg.app.component.AdapterSubOffer.2 */
	class C06072 implements OnClickListener {
		C06072() {
		}

		public void onClick(View v) {
			AdapterSubOffer.this.showConfirmUpPay();
		}
	}

	/* renamed from: com.vtg.app.component.AdapterSubOffer.3 */
	class C06083 implements OnClickListener {
		private final/* synthetic */DialogConfirm val$cfUpPay;

		C06083(DialogConfirm dialogConfirm) {
			this.val$cfUpPay = dialogConfirm;
		}

		public void onClick(View v) {
			this.val$cfUpPay.dismiss();
			AdapterSubOffer.this.showDialogUpPay(true);
		}
	}

	/* renamed from: com.vtg.app.component.AdapterSubOffer.4 */
	class C06094 implements OnClickListener {
		private final/* synthetic */DialogConfirm val$cfUpPay;

		C06094(DialogConfirm dialogConfirm) {
			this.val$cfUpPay = dialogConfirm;
		}

		public void onClick(View v) {
			this.val$cfUpPay.dismiss();
			AdapterSubOffer.this.showDialogUpPay(false);
		}
	}

	private class AsyncTaskRegister extends
			AsyncTask<ModelOffer, String, Boolean> {
		String message;
		ProgressDialog pDialog;

		/*
		 * renamed from:
		 * com.vtg.app.component.AdapterSubOffer.AsyncTaskRegister.1
		 */
		class C06101 implements OnClickListener {
			private final/* synthetic */DialogReport val$sc;

			C06101(DialogReport dialogReport) {
				this.val$sc = dialogReport;
			}

			public void onClick(View v) {
				this.val$sc.dismiss();
			}
		}

		private AsyncTaskRegister() {
			this.message = mContext.getString(R.string.fail_fake_mo);
		}

		protected Boolean doInBackground(ModelOffer... prs) {
			try {
				ModelOffer offer = prs[0];
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam(mXML.ISDN,
						AdapterSubOffer.this.preferences.getString(
								PreferenceKey.PHONE_NUMBER, "")));
				params.add(new ModelParam("receiverIsdn", offer.receiverIsdn));
				params.add(new ModelParam("command", offer.regis));
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
			this.pDialog = new ProgressDialog(AdapterSubOffer.this.mContext);
			this.pDialog.setMessage(AdapterSubOffer.this.mContext
					.getString(R.string.message_loading));
			this.pDialog.setCancelable(false);
			this.pDialog.show();
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			this.pDialog.dismiss();
			if (result.booleanValue()) {
				DialogReport sc = new DialogReport(
						AdapterSubOffer.this.mContext, this.message);
				sc.btnOK.setOnClickListener(new C06101(sc));
				sc.show();
				return;
			}
			new DialogReport(AdapterSubOffer.this.mContext, this.message)
					.show();
		}
	}

	private class Holder {
		private RelativeLayout btnRegis;
		private RelativeLayout btnTopUp;
		private TextView tvExpire;
		private TextView tvGuide;
		private TextView tvName;

		private Holder() {
		}
	}

	public AdapterSubOffer(Context context, List<ModelOffer> listSub) {
		this.mContext = context;
		this.listSub = listSub;
		this.inflater = ((Activity) context).getLayoutInflater();
		this.preferences = ((Activity) context).getSharedPreferences(
				CommonDefine.MY_PACKAGE, 0);
	}

	public int getCount() {
		return this.listSub.size();
	}

	public ModelOffer getItem(int position) {
		return (ModelOffer) this.listSub.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		View view = convertView;
		if (view == null) {
			view = this.inflater.inflate(R.layout.row_offer, null);
			holder = new Holder();
			holder.tvName = (TextView) view.findViewById(R.id.tv_name);
			holder.tvGuide = (TextView) view.findViewById(R.id.tv_guide);
			holder.tvExpire = (TextView) view.findViewById(R.id.tv_expire);
			holder.btnRegis = (RelativeLayout) view
					.findViewById(R.id.btn_register);
			holder.btnTopUp = (RelativeLayout) view
					.findViewById(R.id.btn_top_up);
			view.setTag(holder);
		}
		holder = (Holder) view.getTag();
		ModelOffer sub = getItem(position);
		holder.tvName.setText(sub.name);
		holder.tvGuide.setText(sub.description);
		holder.tvExpire.setText("(" + sub.start + " - " + sub.end + ")");
		if (sub.fakeMO == 1) {
			holder.btnRegis.setVisibility(0);
			holder.btnTopUp.setVisibility(8);
			holder.btnRegis.setOnClickListener(new C06061(sub));
		} else {
			holder.btnRegis.setVisibility(8);
			holder.btnTopUp.setVisibility(0);
			holder.btnTopUp.setOnClickListener(new C06072());
		}
		return view;
	}

	private void showConfirmUpPay() {
		DialogConfirm cfUpPay = new DialogConfirm(this.mContext,
				this.mContext.getString(R.string.confirm_up_pay));
		cfUpPay.tvYes.setText(this.mContext.getString(R.string.you));
		cfUpPay.tvNo.setText(this.mContext.getString(R.string.anyone));
		cfUpPay.btnYes.setOnClickListener(new C06083(cfUpPay));
		cfUpPay.btnNo.setOnClickListener(new C06094(cfUpPay));
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
