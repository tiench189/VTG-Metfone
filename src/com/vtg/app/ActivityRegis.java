package com.vtg.app;

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
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.vtg.app.ActivityMain.mProfile;
import com.vtg.app.component.DialogReport;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelTag;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.MyService;
import com.vtg.app.util.CommonDefine.PreferenceKey;
import com.vtg.app.util.CommonDefine.SoapTag;
import com.vtg.app.util.CommonDefine.WSCode;
import com.vtg.app.util.CommonDefine.mXML;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.JSONParser;
import com.vtg.app.util.SOAPUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class ActivityRegis extends Activity implements CommonDefine {
	private RelativeLayout btnCancel;
	private RelativeLayout btnOtp;
	private RelativeLayout btnRegister;
	private EditText edtNumber;
	private EditText edtOtp;
	private EditText edtPassword;
	private EditText edtRePassword;
	private Context mContext;
	private ProgressDialog pDialog;
	private SharedPreferences preferences;

	/* renamed from: com.vtg.app.ActivityRegis.1 */
	class C05751 implements OnClickListener {
		C05751() {
		}

		public void onClick(View v) {
			ActivityRegis.this.finish();
		}
	}

	/* renamed from: com.vtg.app.ActivityRegis.2 */
	class C05762 implements OnClickListener {
		C05762() {
		}

		public void onClick(View v) {
			String phone = ActivityRegis.this.edtNumber.getText().toString();
			phone = FunctionHelper.formatPhoneNumber(phone, mContext);
			String password = ActivityRegis.this.edtPassword.getText()
					.toString();
			String repassword = ActivityRegis.this.edtRePassword.getText()
					.toString();
			String otp = ActivityRegis.this.edtOtp.getText().toString();
			if (phone.equals("")) {
				new DialogReport(ActivityRegis.this.mContext,
						"Missing phone number").show();
				ActivityRegis.this.edtNumber.requestFocus();
			} else if (password.equals("")) {
				new DialogReport(ActivityRegis.this.mContext,
						"Missing password").show();
				ActivityRegis.this.edtPassword.requestFocus();
			} else if (repassword.equals("")) {
				new DialogReport(ActivityRegis.this.mContext,
						"Missing password").show();
				ActivityRegis.this.edtRePassword.requestFocus();
			} else if (otp.equals("")) {
				new DialogReport(ActivityRegis.this.mContext, "Missing otp")
						.show();
				ActivityRegis.this.edtOtp.requestFocus();
			} else if (password.equals(repassword)) {
				new AsyncTaskRegis().execute(new String[] { phone, password,
						otp });
			} else {
				new DialogReport(ActivityRegis.this.mContext,
						"Retype password again").show();
				ActivityRegis.this.edtRePassword.requestFocus();
			}
		}
	}

	/* renamed from: com.vtg.app.ActivityRegis.3 */
	class C05773 implements OnClickListener {
		C05773() {
		}

		public void onClick(View v) {
			String phone = ActivityRegis.this.edtNumber.getText().toString()
					.trim();
			if (phone.equals("")) {
				new DialogReport(ActivityRegis.this.mContext,
						"Missing phone number").show();
				return;
			}
			phone = FunctionHelper.formatPhoneNumber(phone,
					ActivityRegis.this.mContext);
			new AsyncTaskSubInfo().execute(new String[] { phone });
		}
	}

	private class AsyncTaskGetOtp extends AsyncTask<String, String, Boolean> {
		String msg;

		private AsyncTaskGetOtp() {
			this.msg = "";
		}

		protected void onPreExecute() {
			super.onPreExecute();
			ActivityRegis.this.pDialog.setMessage("Sending request");
			ActivityRegis.this.pDialog.show();
		}

		protected Boolean doInBackground(String... params) {
			try {
				String phone = params[0];
				JSONParser jParser = new JSONParser();
				HashMap<String, String> map = new HashMap();
				map.put("phone", phone);
				List<NameValuePair> nameValuePairs = new ArrayList();
				nameValuePairs.add(new BasicNameValuePair(
						CommonDefine.SIGNATURE, FunctionHelper
								.makeSignatureAPT(map)));
				Log.v("",
						"tiench sign: " + FunctionHelper.makeSignatureAPT(map));
				nameValuePairs.add(new BasicNameValuePair("phone", phone));
				String json = jParser.getJSONFromUrl(MyService.GET_OTP,
						CommonDefine.METHOD_POST, nameValuePairs);
				Log.v("", "tiench otp: " + json);
				JSONObject obj = new JSONObject(json);
				if (obj.getInt(mXML.STATUS) == 1) {
					this.msg = mContext.getString(R.string.message_success);
					return Boolean.valueOf(true);
				}
				this.msg = obj.getString("msg");
				return Boolean.valueOf(false);
			} catch (Exception e) {
				this.msg = mContext.getString(R.string.err_connect);
				return Boolean.valueOf(false);
			}
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			ActivityRegis.this.pDialog.dismiss();
			new DialogReport(ActivityRegis.this.mContext, this.msg).show();
		}
	}

	private class AsyncTaskRegis extends AsyncTask<String, String, Boolean> {
		String msg;
		String password;
		String phone;

		/* renamed from: com.vtg.app.ActivityRegis.AsyncTaskRegis.1 */
		class C05781 implements OnClickListener {
			C05781() {
			}

			public void onClick(View v) {
				ActivityRegis.this.finish();
			}
		}

		private AsyncTaskRegis() {
			this.msg = "";
		}

		protected Boolean doInBackground(String... params) {
			try {
				this.phone = params[0];
				this.password = params[1];
				String otp = params[2];
				JSONParser jParser = new JSONParser();
				HashMap<String, String> map = new HashMap();
				map.put("phone", this.phone);
				map.put(SoapTag.PASSWORD, this.password);
				map.put("otp", otp);
				List<NameValuePair> nameValuePairs = new ArrayList();
				nameValuePairs.add(new BasicNameValuePair(
						CommonDefine.SIGNATURE, FunctionHelper
								.makeSignatureAPT(map)));
				nameValuePairs.add(new BasicNameValuePair("phone", this.phone));
				nameValuePairs.add(new BasicNameValuePair(SoapTag.PASSWORD,
						this.password));
				nameValuePairs.add(new BasicNameValuePair("otp", otp));
				String json = jParser.getJSONFromUrl(MyService.REGIS,
						CommonDefine.METHOD_POST, nameValuePairs);
				Log.v("", "tiench regis: " + json);
				JSONObject obj = new JSONObject(json);
				if (obj.getInt(mXML.STATUS) == 1) {
					return Boolean.valueOf(true);
				}
				this.msg = obj.getString("msg");
				return Boolean.valueOf(false);
			} catch (Exception e) {
				this.msg = mContext.getString(R.string.err_connect);
				return Boolean.valueOf(false);
			}
		}

		protected void onPreExecute() {
			super.onPreExecute();
			ActivityRegis.this.pDialog.setMessage("Registing...");
			ActivityRegis.this.pDialog.show();
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			ActivityRegis.this.pDialog.dismiss();
			if (result.booleanValue()) {
				DialogReport rp = new DialogReport(ActivityRegis.this.mContext,
						"Register success!");
				ActivityFirst.edtNumber.setText(this.phone);
				rp.btnOK.setOnClickListener(new C05781());
				rp.show();
				return;
			}
			new DialogReport(ActivityRegis.this.mContext, this.msg).show();
		}
	}

	private class AsyncTaskSubInfo extends AsyncTask<String, String, Boolean> {
		String message;
		String phone;

		private AsyncTaskSubInfo() {
			this.message = "";
		}

		protected void onPreExecute() {
			super.onPreExecute();
			ActivityRegis.this.pDialog.setMessage("checking...");
			ActivityRegis.this.pDialog.show();
		}

		protected Boolean doInBackground(String... p) {
			try {
				this.phone = p[0];
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam(mXML.ISDN, this.phone));
				SOAPUtil soap = new SOAPUtil(WSCode.GET_SUB_INFO, tags, params);
				soap.makeSOAPRequest();
				if (soap.getError() == 0) {
					mProfile.accountType = Integer.parseInt(soap
							.getValue(mXML.SERVICE_TYPE));
					mProfile.startTime = "";
					mProfile.userUsing = "";
					return Boolean.valueOf(true);
				}
				this.message = soap.getValue(mXML.DESCRIPTION);
				return Boolean.valueOf(false);
			} catch (Exception e) {
				this.message = mContext.getString(R.string.err_connect);
				return Boolean.valueOf(false);
			}
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			ActivityRegis.this.pDialog.dismiss();
			if (result.booleanValue()) {
				new AsyncTaskGetOtp().execute(new String[] { this.phone });
				return;
			}
			new DialogReport(ActivityRegis.this.mContext, this.message).show();
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
		this.pDialog = new ProgressDialog(this.mContext);
		this.pDialog.setCancelable(false);
		activityCreate();
	}

	private void activityCreate() {
		setContentView(R.layout.activity_regis);
		initView();
	}

	private void initView() {
		this.edtNumber = (EditText) findViewById(R.id.edt_number);
		this.edtPassword = (EditText) findViewById(R.id.edt_password);
		this.edtRePassword = (EditText) findViewById(R.id.edt_retype_password);
		this.edtOtp = (EditText) findViewById(R.id.edt_otp);
		this.edtNumber.setText(ActivityFirst.edtNumber.getText().toString());
		this.btnCancel = (RelativeLayout) findViewById(R.id.btn_cancel);
		this.btnRegister = (RelativeLayout) findViewById(R.id.btn_register);
		this.btnCancel.setOnClickListener(new C05751());
		this.btnRegister.setOnClickListener(new C05762());
		this.btnOtp = (RelativeLayout) findViewById(R.id.btn_otp);
		this.btnOtp.setOnClickListener(new C05773());
	}
}
