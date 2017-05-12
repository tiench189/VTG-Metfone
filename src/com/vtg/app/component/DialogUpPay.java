package com.vtg.app.component;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.vtg.app.ActivityMain;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelTag;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.Action;
import com.vtg.app.util.CommonDefine.SoapTag;
import com.vtg.app.util.CommonDefine.WSCode;
import com.vtg.app.util.CommonDefine.mXML;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.SOAPUtil;
import java.util.ArrayList;
import java.util.List;

public class DialogUpPay extends Dialog implements CommonDefine {
	private static final String TAG = "DialogUpPay";
	public RelativeLayout btnCancel;
	public RelativeLayout btnDone;
	public EditText edtNumber;
	public EditText edtPin;
	private boolean isDebit;
	private Context mContext;
	SharedPreferences preferences;
	public TextView tvTitle;

	private class AsyncTaskPay extends AsyncTask<String, String, SOAPUtil> {
		ProgressDialog pDialog;

		private AsyncTaskPay() {
		}

		protected void onPreExecute() {
			super.onPreExecute();
			this.pDialog = new ProgressDialog(DialogUpPay.this.mContext);
			this.pDialog.setMessage(DialogUpPay.this.mContext
					.getString(R.string.message_checking));
			this.pDialog.setCancelable(false);
			this.pDialog.show();
		}

		protected SOAPUtil doInBackground(String... p) {
			try {
				SOAPUtil soap;
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				if (DialogUpPay.this.isDebit) {
					params.add(new ModelParam(mXML.ISDN, new StringBuilder(
							CommonDefine.NUMBER_HEADER).append(p[0]).toString()));
					params.add(new ModelParam("calling", new StringBuilder(
							CommonDefine.NUMBER_HEADER).append(p[0]).toString()));
					params.add(new ModelParam("pin", p[1]));
					params.add(new ModelParam("serviceId", "1"));
					params.add(new ModelParam("sendSms", "1"));
					soap = new SOAPUtil(WSCode.TOP_UP_PAY_BY_CARD, tags, params);
				} else {
					params.add(new ModelParam(mXML.ISDN, p[0]));
					params.add(new ModelParam("calling", p[0]));
					params.add(new ModelParam("pin", p[1]));
					soap = new SOAPUtil(WSCode.PAY_BY_CARD, tags, params);
				}
				soap.makeSOAPRequest();
				return soap;
			} catch (Exception e) {
				return null;
			}
		}

		protected void onPostExecute(SOAPUtil result) {
			super.onPostExecute(result);
			this.pDialog.dismiss();
			if (result != null) {
				try {
					int errCode = result.getError();
					int responseCode = Integer.parseInt(result
							.getValue(mXML.RESPONSE_CODE));
					if (errCode != 0) {
						new DialogReport(DialogUpPay.this.mContext,
								result.getValue(mXML.MESSAGE)).show();
						return;
					} else if (responseCode == 0) {
						DialogUpPay.this.dismiss();
						Toast.makeText(
								DialogUpPay.this.mContext,
								DialogUpPay.this.mContext
										.getString(R.string.message_success), 1)
								.show();
						Intent intent = new Intent();
						intent.setAction(CommonDefine.MY_PACKAGE);
						intent.putExtra(Action.ACTION, Action.RELOAD_ACCOUNT);
						DialogUpPay.this.mContext.sendBroadcast(intent);
						return;
					} else {
						new DialogReport(DialogUpPay.this.mContext,
								result.getValue(mXML.DESCRIPTION)).show();
						return;
					}
				} catch (Exception e) {
					new DialogReport(DialogUpPay.this.mContext,
							DialogUpPay.this.mContext
									.getString(R.string.err_connect)).show();
					return;
				}
			}
			new DialogReport(DialogUpPay.this.mContext,
					DialogUpPay.this.mContext.getString(R.string.err_connect))
					.show();
		}
	}

	private class AsyncTaskSubInfo extends AsyncTask<String, String, Boolean> {
		String message;
		String number;
		String pin;

		private AsyncTaskSubInfo() {
			this.message = "";
		}

		protected Boolean doInBackground(String... p) {
			boolean z = true;
			try {
				this.number = p[0];
				this.pin = p[1];
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam(mXML.ISDN, this.number));
				SOAPUtil soap = new SOAPUtil(WSCode.GET_SUB_INFO, tags, params);
				soap.makeSOAPRequest();
				if (soap.getError() == 0) {
					int accType = Integer.parseInt(soap
							.getValue(mXML.SERVICE_TYPE));
					DialogUpPay dialogUpPay = DialogUpPay.this;
					if (accType != 1) {
						z = false;
					}
					dialogUpPay.isDebit = z;
					return Boolean.valueOf(true);
				}
				this.message = soap.getValue(mXML.MESSAGE);
				return Boolean.valueOf(false);
			} catch (Exception e) {
				this.message = DialogUpPay.this.mContext
						.getString(R.string.err_connect);
				return Boolean.valueOf(false);
			}
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result.booleanValue()) {
				new AsyncTaskPay().executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, new String[] {
								this.number, this.pin });
				return;
			}
			new DialogReport(DialogUpPay.this.mContext, this.message).show();
		}
	}

	public DialogUpPay(Context context, boolean isDebit) {
		super(context);
		this.isDebit = false;
		requestWindowFeature(1);
		setContentView(R.layout.dialog_up_pay);
		setCancelable(false);
		this.mContext = context;
		this.isDebit = isDebit;
		this.preferences = ((Activity) context).getSharedPreferences(
				CommonDefine.MY_PACKAGE, 0);
		initView();
	}

	private void initView() {
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		this.edtNumber = (EditText) findViewById(R.id.edt_number);
		this.edtPin = (EditText) findViewById(R.id.edt_pin);
		this.btnDone = (RelativeLayout) findViewById(R.id.btn_done);
		this.btnCancel = (RelativeLayout) findViewById(R.id.btn_cancel);
		this.btnDone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				submitPin();
				dismiss();
			}
		});
		this.btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	private void submitPin() {
		String pin = this.edtPin.getText().toString().trim();
		String number = this.edtNumber.getText().toString().trim();
		if (number.equals("")) {
			Toast.makeText(this.mContext,
					this.mContext.getString(R.string.message_empty_number), 1)
					.show();
		} else if (pin.equals("")) {
			Toast.makeText(this.mContext,
					this.mContext.getString(R.string.message_empty_pin), 1)
					.show();
		} else {
			dismiss();
			number = FunctionHelper.formatPhoneNumber(number, this.mContext);
			new AsyncTaskSubInfo().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR,
					new String[] { number, pin });
		}
	}
}
