package com.vtg.app.component;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.vtg.app.ActivityMain;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelTag;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.SOAPUtil;

public class DialogTranfer extends Dialog implements CommonDefine {
	private static final String TAG = "DialogTranfer";
	public RelativeLayout btnCancel;
	public RelativeLayout btnDone;
	public EditText edtAmount;
	public EditText edtNumber;
	public EditText edtPassword;
	// public Spinner spAmount;
	private Context mContext;
	SharedPreferences preferences;
	public TextView tvTitle;

	public DialogTranfer(Context context) {
		super(context);
		requestWindowFeature(1);
		setContentView(R.layout.dialog_tranfer);
		this.mContext = context;
		preferences = ((Activity) context).getSharedPreferences(
				CommonDefine.MY_PACKAGE, 0);
		initView();
	}

	private void initView() {
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		edtNumber = (EditText) findViewById(R.id.edt_number);
		edtAmount = (EditText) findViewById(R.id.edt_amount);
		edtPassword = (EditText) findViewById(R.id.edt_password);
		// spAmount = (Spinner) findViewById(R.id.sp_amount);
		// spAmount.setAdapter(new ArrayAdapter<String>(mContext,
		// R.layout.row_item_spinner, listAmount));
		// edtNumber.requestFocus();
		btnDone = (RelativeLayout) findViewById(R.id.btn_done);
		btnCancel = (RelativeLayout) findViewById(R.id.btn_cancel);
		btnDone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String number = FunctionHelper.formatPhoneNumber(edtNumber
						.getText().toString().trim(), mContext);
				String pass = edtPassword.getText().toString().trim();
				if (edtAmount.getText().toString().trim().equals("")) {
					new DialogReport(mContext, "Missing amount").show();
					edtAmount.requestFocus();
				} else if (number.equals("")) {
					new DialogReport(mContext, "Missing number receiver")
							.show();
					edtNumber.requestFocus();
				} else if (pass.equals("")) {
					new DialogReport(mContext, "Missing password").show();
					edtPassword.requestFocus();
				} else {
					int amount = Integer.parseInt(edtAmount.getText()
							.toString().trim());
					if (amount < 20 || amount > 1000) {
						new DialogReport(mContext,
								"Amount tranfer must be in range 20cents to 10$(1000cents)")
								.show();
					} else {
						new AsyncTaskRegister().execute(new String[] { number,
								amount * 10000 + "", pass });
					}
				}
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();

			}
		});
	}

	private class AsyncTaskRegister extends AsyncTask<String, String, Boolean> {
		String message;
		ProgressDialog pDialog;

		private AsyncTaskRegister() {
			this.message = mContext.getString(R.string.err_connect);
		}

		protected Boolean doInBackground(String... prs) {
			try {
				String number = prs[0];
				String amount = prs[1];
				String pass = prs[2];
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam("msisdn", NUMBER_HEADER
						+ preferences.getString(PreferenceKey.PHONE_NUMBER, "")));
				params.add(new ModelParam("receivePhone", NUMBER_HEADER
						+ number));
				params.add(new ModelParam("amount", amount));
				params.add(new ModelParam("requestId", FunctionHelper
						.formatCurrentTime()));
				params.add(new ModelParam("passwordTransfer", pass));
				params.add(new ModelParam("balanceId", "2000"));
				SOAPUtil soap = new SOAPUtil(WSCode.TRANFER_MONEY, tags, params);
				soap.makeSOAPRequest();
				if (soap.getError() != 0) {
					this.message = mContext.getString(R.string.message_fail);
					return Boolean.valueOf(false);
				} else if (Integer.parseInt(soap.getValue(mXML.ERROR_CODE)) == 0) {
					this.message = mContext.getString(R.string.message_success);
					return Boolean.valueOf(true);
				} else {
					this.message = soap.getValue(mXML.DESCRIPTION);
					return Boolean.valueOf(false);
				}
			} catch (Exception e) {
				return Boolean.valueOf(false);
			}
		}

		protected void onPreExecute() {
			super.onPreExecute();
			this.pDialog = new ProgressDialog(mContext);
			this.pDialog.setMessage(mContext
					.getString(R.string.message_loading));
			this.pDialog.setCancelable(false);
			this.pDialog.show();
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			this.pDialog.dismiss();
			if (result.booleanValue()) {
				Intent intent = new Intent();
				intent.setAction(Action.FILTER);
				intent.putExtra(Action.ACTION, Action.RELOAD_ACCOUNT);
				mContext.sendBroadcast(intent);
				final DialogReport sc = new DialogReport(mContext, this.message);
				sc.btnOK.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						sc.dismiss();
					}
				});
				sc.show();
				dismiss();
				return;
			}
			new DialogReport(mContext, this.message).show();
		}
	}
}
