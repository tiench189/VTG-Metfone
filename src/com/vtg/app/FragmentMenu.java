package com.vtg.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vtg.app.component.DialogChangePass;
import com.vtg.app.component.DialogConfirm;
import com.vtg.app.component.DialogReport;
import com.vtg.app.metfone.R;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.JSONParser;

public class FragmentMenu extends Fragment implements OnClickListener,
		CommonDefine {
	private RelativeLayout btnLogout;
	private RelativeLayout layoutAbout;
	private RelativeLayout layoutHelp;
	private RelativeLayout layoutNotification;
	private RelativeLayout layoutProfile;
	private RelativeLayout layoutSetting;
	private RelativeLayout layoutShowroom;
	private RelativeLayout layoutTariff;
	private RelativeLayout layoutChangePass;
	private SharedPreferences preferences;
	private TextView tvNumber;
	private ProgressDialog pDialog;
	private Context mContext;

	/* renamed from: com.vtg.app.FragmentMenu.1 */
	class C05981 implements OnClickListener {
		C05981() {
		}

		public void onClick(View v) {
			preferences.edit().putString(PreferenceKey.PHONE_NUMBER, "")
					.commit();
			getActivity().startActivity(
					new Intent(getActivity(), ActivityFirst.class));
			getActivity().finish();
		}
	}

	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = getActivity().getSharedPreferences(
				CommonDefine.MY_PACKAGE, 0);
		mContext = getActivity();
		pDialog = new ProgressDialog(mContext);
		pDialog.setCancelable(false);
	}

	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_menu, null);
		initView(view);
		return view;
	}

	private void initView(View view) {
		layoutNotification = (RelativeLayout) view
				.findViewById(R.id.layout_notification);
		layoutNotification.setOnClickListener(this);
		layoutProfile = (RelativeLayout) view.findViewById(R.id.layout_profile);
		layoutProfile.setOnClickListener(this);
		layoutAbout = (RelativeLayout) view.findViewById(R.id.layout_about);
		layoutAbout.setOnClickListener(this);
		layoutTariff = (RelativeLayout) view.findViewById(R.id.layout_tariff);
		layoutTariff.setOnClickListener(this);
		layoutShowroom = (RelativeLayout) view
				.findViewById(R.id.layout_showroom);
		layoutShowroom.setOnClickListener(this);
		layoutSetting = (RelativeLayout) view.findViewById(R.id.layout_setting);
		layoutSetting.setOnClickListener(this);
		layoutHelp = (RelativeLayout) view.findViewById(R.id.layout_help);
		layoutHelp.setOnClickListener(this);
		tvNumber = (TextView) view.findViewById(R.id.tv_number);
		tvNumber.setText(preferences.getString(PreferenceKey.PHONE_NUMBER, ""));
		btnLogout = (RelativeLayout) view.findViewById(R.id.btn_logout);
		btnLogout.setOnClickListener(this);
		layoutChangePass = (RelativeLayout) view
				.findViewById(R.id.layout_change_pass);
		layoutChangePass.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_notification:
			startActivity(new Intent(getActivity(), ActivityNew.class));
			getActivity().overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_left);
			break;
		case R.id.layout_profile:
			startActivity(new Intent(getActivity(), ActivityProfiles.class));
			getActivity().overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_left);
			break;
		case R.id.layout_about:
			startActivity(new Intent(getActivity(), ActivityAbout.class));
			getActivity().overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_left);
			break;
		case R.id.layout_tariff:
			startActivity(new Intent(getActivity(), ActivityTariff.class));
			getActivity().overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_left);
			break;
		case R.id.layout_showroom:
			startActivity(new Intent(getActivity(), ActivityShowroom.class));
			getActivity().overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_left);
			break;
		case R.id.layout_setting:
			startActivity(new Intent(getActivity(), ActivitySetting.class));
			getActivity().overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_left);
			break;
		case R.id.layout_change_pass:
			final DialogConfirm cf = new DialogConfirm(getActivity(),
					getString(R.string.cf_reset_pass));
			cf.btnYes.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					cf.dismiss();
					// new AsyncTaskSubInfo().execute(number);
					new AsyncTaskGetOTPChangePass().execute(preferences
							.getString(PreferenceKey.PHONE_NUMBER, ""));
				}
			});
			cf.show();
			break;
		case R.id.btn_logout:
			actionLogout();
			break;
		default:
			break;
		}
	}

	private void actionLogout() {
		DialogConfirm cfLogout = new DialogConfirm(getActivity(),
				getString(R.string.confirm_logout));
		cfLogout.btnYes.setOnClickListener(new C05981());
		cfLogout.show();
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
