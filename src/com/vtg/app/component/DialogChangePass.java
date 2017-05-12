package com.vtg.app.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.vtg.app.metfone.R;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.JSONParser;

public class DialogChangePass extends Dialog implements CommonDefine,
		android.view.View.OnClickListener {

	public String number;
	private EditText edtNumber, edtOTP, edtPassword;
	private RelativeLayout btnChange, btnCancel;
	private Context mContext;

	public DialogChangePass(Context context, String number) {
		super(context);
		requestWindowFeature(1);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));
		setContentView(R.layout.dialog_change_pass);
		this.number = number;
		this.mContext = context;

		initView();
	}

	private void initView() {
		edtNumber = (EditText) findViewById(R.id.edt_number);
		edtOTP = (EditText) findViewById(R.id.edt_otp);
		edtPassword = (EditText) findViewById(R.id.edt_new_password);
		edtPassword.requestFocus();
		edtNumber.setText(number);

		btnChange = (RelativeLayout) findViewById(R.id.btn_done);
		btnChange.setOnClickListener(this);
		btnCancel = (RelativeLayout) findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_done:
			submitChange();
			break;
		case R.id.btn_cancel:
			dismiss();
			break;

		default:
			break;
		}
	}

	private void submitChange() {
		String number = FunctionHelper.formatPhoneNumber(edtNumber.getText()
				.toString().trim(), mContext);
		String pass = edtPassword.getText().toString();
		String otp = edtOTP.getText().toString();
		if (number.equals("")) {
			new DialogReport(mContext, "Missing phone number").show();
			edtNumber.requestFocus();
		} else if (pass.equals("")) {
			new DialogReport(mContext, "Missing password").show();
			edtPassword.requestFocus();
		} else if (otp.equals("")) {
			new DialogReport(mContext, "Missing OTP").show();
			edtOTP.requestFocus();
		} else {
			new AsyncTaskGetOTPChangePass().execute(new String[] { number,
					pass, otp });
		}
	}

	private class AsyncTaskGetOTPChangePass extends
			AsyncTask<String, String, Boolean> {
		String msg = "";
		String phone;
		ProgressDialog pDialog;

		protected Boolean doInBackground(String... params) {
			try {
				phone = params[0];
				String pass = params[1];
				String otp = params[2];
				JSONParser jParser = new JSONParser();
				HashMap<String, String> map = new HashMap();
				map.put("phone", phone);
				map.put("newpassword", pass);
				map.put("otp", otp);
				List<NameValuePair> nameValuePairs = new ArrayList();
				nameValuePairs.add(new BasicNameValuePair(
						CommonDefine.SIGNATURE, FunctionHelper
								.makeSignatureAPT(map)));
				nameValuePairs.add(new BasicNameValuePair("phone", phone));
				nameValuePairs.add(new BasicNameValuePair("newpassword", pass));
				nameValuePairs.add(new BasicNameValuePair("otp", otp));
				Log.v("",
						"tiench param change: "
								+ FunctionHelper.makeSignatureAPT(map));
				String json = jParser.getJSONFromUrl(MyService.CHANGE_PASS,
						CommonDefine.METHOD_POST, nameValuePairs);
				Log.v("", "tiench change: " + json);
				JSONObject obj = new JSONObject(json);
				if (obj.getInt(mXML.STATUS) == 1) {
					return Boolean.valueOf(true);
				}
				msg = obj.getString("msg");
				return Boolean.valueOf(false);
			} catch (Exception e) {
				msg = mContext.getString(R.string.err_connect);
				return Boolean.valueOf(false);
			}
		}

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(mContext);
			pDialog.setCancelable(false);
			pDialog.setMessage("Loading...");
			pDialog.show();
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if (result.booleanValue()) {
				dismiss();
				final DialogReport rp = new DialogReport(mContext, mContext.getString(R.string.message_success));
				rp.show();
				return;
			}
			new DialogReport(mContext, msg).show();
		}
	}
}
