package com.vtg.app.component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.vtg.app.ActivityMain;
import com.vtg.app.ActivityMain.mProfile;
import com.vtg.app.ActivityProfiles;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelId;
import com.vtg.app.model.ModelPaper;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelTag;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.SOAPUtil;

public class DialogUpdateProfile extends Dialog implements CommonDefine {
	private RelativeLayout btnCancel;
	private RelativeLayout btnDone;
	private String currentCode;
	private EditText currentEdt;
	private EditText edtAddress;
	private EditText edtBirthday;
	private EditText edtIdNo;
	private EditText edtIssueDate;
	private EditText edtIssuePlace;
	private EditText edtUserusing;
	private Context mContext;
	private OnDateSetListener myDateListener;
	private List<ModelPaper> papers;
	private SharedPreferences preferences;
	SimpleDateFormat sdf;
	private Spinner spId;

	/* renamed from: com.vtg.app.component.DialogUpdateProfile.1 */
	class C06301 implements OnDateSetListener {
		C06301() {
		}

		public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
			Date d = new Date();
			Log.v("", "tiench: " + arg1);
			SimpleDateFormat sf = new SimpleDateFormat("dd/MM/");
			d.setYear(arg1);
			d.setMonth(arg2);
			d.setDate(arg3);
			DialogUpdateProfile.this.currentEdt.setText(sf.format(d) + arg1);
		}
	}

	/* renamed from: com.vtg.app.component.DialogUpdateProfile.4 */
	class C06334 implements OnItemSelectedListener {
		C06334() {
		}

		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {
			DialogUpdateProfile.this.updatePaper();
			DialogUpdateProfile.this.currentCode = ((ModelId) ActivityProfiles.listIds
					.get(position)).code;
			DialogUpdateProfile.this.setPaper(FunctionHelper.findPaper(
					DialogUpdateProfile.this.currentCode,
					DialogUpdateProfile.this.papers));
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
		}
	}

	private class AsyncTaskUpdate extends AsyncTask<String, String, Boolean> {
		String message;
		ProgressDialog pDialog;

		private AsyncTaskUpdate() {
			this.message = "";
		}

		protected Boolean doInBackground(String... prs) {
			try {
				String userusing = DialogUpdateProfile.this.edtUserusing
						.getText().toString().trim();
				String adress = DialogUpdateProfile.this.edtAddress.getText()
						.toString().trim();
				String birthday = DialogUpdateProfile.this.edtBirthday
						.getText().toString().trim();
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam(SoapTag.NAME, userusing));
				params.add(new ModelParam(mXML.BIRTHDATE, birthday));
				try {
					ModelPaper paper = FunctionHelper.findPaper(
							((ModelId) ActivityProfiles.listIds
									.get(DialogUpdateProfile.this.spId
											.getSelectedItemPosition())).code,
							DialogUpdateProfile.this.papers);
					params.add(new ModelParam(mXML.ID_TYPE, paper.type));
					params.add(new ModelParam(mXML.ID_NO, paper.no));
					params.add(new ModelParam("idIssuePlace", paper.place));
					params.add(new ModelParam("idIssueDate", paper.date));
				} catch (Exception e) {
				}
				params.add(new ModelParam(mXML.CUST_ID, mProfile.cusID));
				params.add(new ModelParam(mXML.ADDRESS, mProfile.address));
				SOAPUtil soap = new SOAPUtil(WSCode.UPDATE_CUS_INFO, tags,
						params);
				soap.makeSOAPRequest();
				if (soap.getError() != 0) {
					this.message = mContext.getString(R.string.message_fail);
					return Boolean.valueOf(false);
				} else if (Integer.parseInt(soap.getValue(mXML.RESPONSE_CODE)) == 1) {
					this.message = mContext.getString(R.string.message_success);
					return Boolean.valueOf(true);
				} else {
					this.message = soap.getValue(mXML.MESSAGE);
					return Boolean.valueOf(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
				message = mContext.getString(R.string.err_connect);
				return false;
			}
		}

		protected void onPreExecute() {
			super.onPreExecute();
			this.pDialog = new ProgressDialog(DialogUpdateProfile.this.mContext);
			this.pDialog.setMessage(DialogUpdateProfile.this.mContext
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
				intent.putExtra(Action.ACTION, Action.RELOAD_PROFILE);
				DialogUpdateProfile.this.mContext.sendBroadcast(intent);
			}
			new DialogReport(DialogUpdateProfile.this.mContext, this.message)
					.show();
			DialogUpdateProfile.this.dismiss();
		}
	}

	public DialogUpdateProfile(Context context) {
		super(context);
		this.currentCode = "";
		this.sdf = new SimpleDateFormat("dd/MM/yyyy");
		this.myDateListener = new C06301();
		requestWindowFeature(1);
		setContentView(R.layout.dialog_update_profile);
		this.preferences = ((Activity) context).getSharedPreferences(
				CommonDefine.MY_PACKAGE, 0);
		this.mContext = context;
		this.papers = mProfile.papers;
		this.edtUserusing = (EditText) findViewById(R.id.edt_user_using);
		this.edtUserusing.setText(mProfile.userUsing);
		this.edtAddress = (EditText) findViewById(R.id.edt_address);
		this.edtAddress.setText(mProfile.address);
		this.edtBirthday = (EditText) findViewById(R.id.edt_birthday);
		this.edtBirthday.setText(mProfile.birthDay);
		this.edtBirthday.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogUpdateProfile.this.currentEdt = DialogUpdateProfile.this.edtBirthday;
				int[] time = FunctionHelper
						.getTimeFromEdt(DialogUpdateProfile.this.edtBirthday);
				new DatePickerDialog(DialogUpdateProfile.this.mContext,
						DialogUpdateProfile.this.myDateListener, time[0],
						time[1], time[2]).show();

			}
		});
		this.spId = (Spinner) findViewById(R.id.sp_id);
		this.edtIdNo = (EditText) findViewById(R.id.edt_id_no);
		this.edtIssueDate = (EditText) findViewById(R.id.edt_issue_date);
		this.edtIssuePlace = (EditText) findViewById(R.id.edt_issue_place);
		this.spId.setAdapter(new AdapterId(this.mContext,
				ActivityProfiles.listIds));
		if (ActivityProfiles.listIds.size() > 0) {
			this.currentCode = ((ModelId) ActivityProfiles.listIds.get(0)).code;
			setPaper(FunctionHelper.findPaper(this.currentCode, this.papers));
		}
		this.edtIssueDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogUpdateProfile.this.currentEdt = DialogUpdateProfile.this.edtIssueDate;
				int[] time = FunctionHelper
						.getTimeFromEdt(DialogUpdateProfile.this.edtIssueDate);
				new DatePickerDialog(DialogUpdateProfile.this.mContext,
						DialogUpdateProfile.this.myDateListener, time[0],
						time[1], time[2]).show();
			}
		});
		this.spId.setOnItemSelectedListener(new C06334());
		this.btnDone = (RelativeLayout) findViewById(R.id.btn_done);
		this.btnCancel = (RelativeLayout) findViewById(R.id.btn_cancel);
		this.btnDone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogUpdateProfile.this.dismiss();
				new AsyncTaskUpdate().execute(new String[0]);

			}
		});
		this.btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();

			}
		});
	}

	private void updatePaper() {
		String no = this.edtIdNo.getText().toString().trim();
		String date = this.edtIssueDate.getText().toString().trim();
		String place = this.edtIssuePlace.getText().toString().trim();
		if (!no.equals("")) {
			if (FunctionHelper.findPaper(this.currentCode, this.papers).type
					.equals("")) {
				this.papers.add(new ModelPaper(no, this.currentCode, date,
						place));
				return;
			}
			for (int i = 0; i < this.papers.size(); i++) {
				if (((ModelPaper) this.papers.get(i)).type
						.equals(this.currentCode)) {
					((ModelPaper) this.papers.get(i)).no = no;
					((ModelPaper) this.papers.get(i)).date = date;
					((ModelPaper) this.papers.get(i)).place = place;
				}
			}
		}
	}

	private void setPaper(ModelPaper pp) {
		this.edtIdNo.setText(pp.no);
		this.edtIssueDate.setText(pp.date);
		this.edtIssuePlace.setText(pp.place);
	}
}
