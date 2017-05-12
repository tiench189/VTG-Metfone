package com.vtg.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.vtg.app.ActivityMain.mProfile;
import com.vtg.app.component.DialogChangePass;
import com.vtg.app.component.DialogConfirm;
import com.vtg.app.component.DialogReport;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelTag;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.IPUtils;
import com.vtg.app.util.JSONParser;
import com.vtg.app.util.SOAPUtil;

public class ActivityFirst extends Activity implements CommonDefine {
	public static EditText edtNumber;
	public static EditText edtPassword;
	private TextView tvForgot;
	private RelativeLayout btnLogin;
	private RelativeLayout btnRegister;
	private RelativeLayout layoutContinue;
	private RelativeLayout layoutSplash;
	private Context mContext;
	private ProgressDialog pDialog;
	private SharedPreferences preferences;

	/* renamed from: com.vtg.app.1 */
	class C05541 implements OnEditorActionListener {
		C05541() {
		}

		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == 6) {
				actionSubmit();
			}
			return false;
		}
	}

	/* renamed from: com.vtg.app.2 */
	class C05552 implements OnClickListener {
		C05552() {
		}

		public void onClick(View v) {
			actionSubmit();
		}
	}

	/* renamed from: com.vtg.app.3 */
	class C05563 implements OnClickListener {
		C05563() {
		}

		public void onClick(View v) {
			startActivity(new Intent(mContext, ActivityRegis.class));
		}
	}

	/* renamed from: com.vtg.app.4 */
	class C05574 implements OnClickListener {
		C05574() {
		}

		public void onClick(View v) {
			startActivity(new Intent(mContext, ActivityWithoutLogin.class));
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_left);
		}
	}

	private class AsyncTaskDetect extends AsyncTask<String, String, Boolean> {
		String message;
		String phone;

		private AsyncTaskDetect() {
			message = "";
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.show();
		}

		protected Boolean doInBackground(String... p) {
			try {
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam("ip", p[0]));
				SOAPUtil soap = new SOAPUtil(WSCode.DETECT_ISDN, tags, params);
				soap.makeSOAPRequest();
				if (soap.getError() != 0) {
					return Boolean.valueOf(false);
				}
				phone = soap.getValue(mXML.DESC);
				if (soap.getValue(mXML.CODE).equals("0")) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				return Boolean.valueOf(false);
			}
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			layoutSplash.setVisibility(8);
			if (result.booleanValue() && !phone.equals("")) {
				edtNumber.setText(phone);
			} else {
				edtNumber.setText(FunctionHelper.getPhoneNumber(mContext));
			}
			if (!edtNumber.getText().toString().equals("")) {
				edtPassword.requestFocus();
			}
		}
	}

	private class AsyncTaskLogin extends AsyncTask<String, String, Boolean> {
		String msg;
		String phone;

		private AsyncTaskLogin() {
			msg = "";
		}

		protected Boolean doInBackground(String... params) {
			try {
				phone = params[0];
				String pass = params[1];
				Log.v("", "tiench: " + phone + "/" + pass);
				JSONParser jParser = new JSONParser();
				HashMap<String, String> map = new HashMap();
				map.put("phone", phone);
				map.put(SoapTag.PASSWORD, pass);
				List<NameValuePair> nameValuePairs = new ArrayList();
				nameValuePairs.add(new BasicNameValuePair(
						CommonDefine.SIGNATURE, FunctionHelper
								.makeSignatureAPT(map)));
				nameValuePairs.add(new BasicNameValuePair("phone", phone));
				nameValuePairs.add(new BasicNameValuePair(SoapTag.PASSWORD,
						pass));
				Log.v("",
						"tiench param login: "
								+ FunctionHelper.makeSignatureAPT(map));
				String json = jParser.getJSONFromUrl(MyService.LOGIN,
						CommonDefine.METHOD_POST, nameValuePairs);
				Log.v("", "tiench login: " + json);
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
			pDialog.setMessage("Login...");
			pDialog.show();
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if (result.booleanValue()) {
				preferences.edit().putString(PreferenceKey.PHONE_NUMBER, phone)
						.commit();
				startActivity(new Intent(mContext, ActivityMain.class));
				finish();
				return;
			}
			new DialogReport(mContext, msg).show();
		}
	}

	protected void onCreate(@Nullable Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(1);
		setRequestedOrientation(7);
		preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
		mContext = this;
		setLanguage();
		setContentView(R.layout.activity_first);
		pDialog = new ProgressDialog(mContext);
		pDialog.setCancelable(false);
		initView();
	}

	private void initView() {
		layoutSplash = (RelativeLayout) findViewById(R.id.layout_splash);
		edtNumber = (EditText) findViewById(R.id.edt_number);
		edtPassword = (EditText) findViewById(R.id.edt_password);
		edtPassword.setOnEditorActionListener(new C05541());
		btnLogin = (RelativeLayout) findViewById(R.id.btn_login);
		btnRegister = (RelativeLayout) findViewById(R.id.btn_register);
		btnLogin.setOnClickListener(new C05552());
		btnRegister.setOnClickListener(new C05563());
		layoutContinue = (RelativeLayout) findViewById(R.id.layout_continue);
		layoutContinue.setOnClickListener(new C05574());
		Log.v("", "tiench ip: " + IPUtils.getIPAddress(true));
		new AsyncTaskDetect().execute(IPUtils.getIPAddress(true));
		layoutSplash.setVisibility(8);
		edtNumber.setText(FunctionHelper.getPhoneNumber(mContext));

		tvForgot = (TextView) findViewById(R.id.tv_forgot);
		tvForgot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String number = FunctionHelper.formatPhoneNumber(
						edtNumber.getText().toString().trim(), mContext);
				String pass = edtPassword.getText().toString();
				if (number.equals("")) {
					new DialogReport(mContext, getString(R.string.miss_phone))
							.show();
					edtNumber.requestFocus();
				} else {
					final DialogConfirm cf = new DialogConfirm(mContext,
							getString(R.string.cf_reset_pass));
					cf.btnYes.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							cf.dismiss();
							// new AsyncTaskSubInfo().execute(number);
							new AsyncTaskGetOTPChangePass().execute(number);
						}
					});
					cf.show();
				}
			}
		});
	}

	private void actionSubmit() {
		String number = FunctionHelper.formatPhoneNumber(edtNumber.getText()
				.toString().trim(), mContext);
		String pass = edtPassword.getText().toString();
		if (number.equals("")) {
			new DialogReport(mContext, "Missing phone number").show();
			edtNumber.requestFocus();
		} else if (pass.equals("")) {
			new DialogReport(mContext, "Missing password").show();
			edtPassword.requestFocus();
		} else {
			new AsyncTaskLogin().execute(new String[] { number, pass });
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

	protected void onStart() {
		super.onStart();
		setLanguage();
	}

	private class AsyncTaskSubInfo extends AsyncTask<String, String, Boolean> {
		String message;
		String phone;

		private AsyncTaskSubInfo() {
			this.message = "";
		}

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setMessage("checking...");
			pDialog.show();
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
			pDialog.dismiss();
			if (result.booleanValue()) {
				new AsyncTaskGetOTPChangePass().execute(phone);
				return;
			}
			new DialogReport(mContext, this.message).show();
		}
	}

	private class AsyncTaskGetOTPChangePass extends
			AsyncTask<String, String, Boolean> {
		String msg = "";
		String phone;

		protected Boolean doInBackground(String... params) {
			try {
				phone = params[0];
				JSONParser jParser = new JSONParser();
				HashMap<String, String> map = new HashMap();
				map.put("phone", phone);
				List<NameValuePair> nameValuePairs = new ArrayList();
				nameValuePairs.add(new BasicNameValuePair(
						CommonDefine.SIGNATURE, FunctionHelper
								.makeSignatureAPT(map)));
				nameValuePairs.add(new BasicNameValuePair("phone", phone));
				Log.v("",
						"tiench param login: "
								+ FunctionHelper.makeSignatureAPT(map));
				String json = jParser.getJSONFromUrl(
						MyService.GET_OTP_CHANGE_PASS,
						CommonDefine.METHOD_POST, nameValuePairs);
				Log.v("", "tiench otp: " + json);
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
			pDialog.setMessage("Loading...");
			pDialog.show();
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if (result.booleanValue()) {
				final DialogReport rp = new DialogReport(
						mContext,
						"We have sent OTP to "
								+ phone
								+ ". Now you cant use OTP to change your password");
				rp.btnOK.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						rp.dismiss();
						new DialogChangePass(mContext, phone).show();
					}
				});
				rp.show();
				return;
			}
			new DialogReport(mContext, msg).show();
		}
	}
}
