package com.vtg.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.vtg.app.ActivityMain.mProfile;
import com.vtg.app.component.AdapterCharge;
import com.vtg.app.component.AdapterSMSHistory;
import com.vtg.app.component.AdapterVasHistory;
import com.vtg.app.component.DialogReport;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelCharge;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelSMSHistory;
import com.vtg.app.model.ModelTag;
import com.vtg.app.model.ModelVasHistory;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.SOAPUtil;
import com.vtg.app.util.XMLParser;
import com.vtg.app.util.CommonDefine.PreferenceKey;

public class ActivityDebitHistory extends Activity implements CommonDefine {
	private RelativeLayout btnSearch;
	private EditText currentEdt;
	private EditText edtFrom;
	private EditText edtTo;
	private ListView lvHistory;
	private Context mContext;
	private OnDateSetListener myDateListener;
	private SharedPreferences preferences;
	SimpleDateFormat sdf;
	private Spinner spType;

	public void setLanguage() {
		Locale locale = new Locale(preferences.getString(PreferenceKey.LOCATE,
				"en"));
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
	}

	/* renamed from: com.vtg.app.ActivityDebitHistory.1 */
	class C05491 implements OnDateSetListener {
		C05491() {
		}

		public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
			Date d = new Date();
			Log.v("", "tiench: " + arg1);
			SimpleDateFormat sf = new SimpleDateFormat("dd/MM/");
			d.setYear(arg1);
			d.setMonth(arg2);
			d.setDate(arg3);
			ActivityDebitHistory.this.currentEdt.setText(sf.format(d) + arg1);
		}
	}

	/* renamed from: com.vtg.app.ActivityDebitHistory.2 */
	class C05502 implements OnClickListener {
		C05502() {
		}

		public void onClick(View v) {
			ActivityDebitHistory.this.actionBack();
		}
	}

	/* renamed from: com.vtg.app.ActivityDebitHistory.3 */
	class C05513 implements OnClickListener {
		C05513() {
		}

		public void onClick(View v) {
			ActivityDebitHistory.this.currentEdt = ActivityDebitHistory.this.edtFrom;
			int[] time = FunctionHelper
					.getTimeFromEdt(ActivityDebitHistory.this.edtFrom);
			new DatePickerDialog(ActivityDebitHistory.this.mContext,
					ActivityDebitHistory.this.myDateListener, time[0], time[1],
					time[2]).show();
		}
	}

	/* renamed from: com.vtg.app.ActivityDebitHistory.4 */
	class C05524 implements OnClickListener {
		C05524() {
		}

		public void onClick(View v) {
			ActivityDebitHistory.this.currentEdt = ActivityDebitHistory.this.edtTo;
			int[] time = FunctionHelper
					.getTimeFromEdt(ActivityDebitHistory.this.edtTo);
			new DatePickerDialog(ActivityDebitHistory.this.mContext,
					ActivityDebitHistory.this.myDateListener, time[0], time[1],
					time[2]).show();
		}
	}

	/* renamed from: com.vtg.app.ActivityDebitHistory.5 */
	class C05535 implements OnClickListener {
		C05535() {
		}

		public void onClick(View v) {
			ActivityDebitHistory.this.submitSearch();
		}
	}

	private class AsyncTaskCharge extends AsyncTask<String, String, String> {
		ArrayList<ModelCharge> listCharge;
		ProgressDialog pDialog;

		private AsyncTaskCharge() {
			this.listCharge = new ArrayList();
		}

		protected void onPreExecute() {
			super.onPreExecute();
			this.pDialog = new ProgressDialog(
					ActivityDebitHistory.this.mContext);
			this.pDialog.setCancelable(false);
			this.pDialog.setMessage(ActivityDebitHistory.this.mContext
					.getString(R.string.message_loading));
			this.pDialog.show();
			this.listCharge.add(new ModelCharge("Charge date", "Type",
					"Duration", "Amount", "Number"));
		}

		protected String doInBackground(String... p) {
			try {
				String dateTo = p[1];
				String dateFrom = p[0];
				Log.v("", "tiench date: " + dateFrom + " - " + dateTo);
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam("callType", "1"));
				params.add(new ModelParam(mXML.SUB_ID, mProfile.subId));
				params.add(new ModelParam("fromDate", dateFrom));
				params.add(new ModelParam("toDate", dateTo));
				SOAPUtil soap = new SOAPUtil(
						WSCode.CALL_DETAIL_HISTORY_POSTPAID, tags, params);
				soap.makeSOAPRequest();
				NodeList listDate = soap.mDoc.getElementsByTagName("callDate");
				NodeList listDuration = soap.mDoc
						.getElementsByTagName("callDuration");
				NodeList listType = soap.mDoc.getElementsByTagName("itemName");
				NodeList listAmount = soap.mDoc.getElementsByTagName("amount");
//				NodeList listNumber = soap.mDoc.getElementsByTagName("calledNumber");
				for (int i = 0; i < listDate.getLength(); i++) {
					if (!XMLParser.getElementValue(listDate.item(i)).trim()
							.equals("")) {
						ModelCharge charge = new ModelCharge();
						charge.date = XMLParser.getElementValue(listDate
								.item(i));
						charge.type = XMLParser.getElementValue(listType
								.item(i));
						charge.duration = XMLParser
								.getElementValue(listDuration.item(i));
						charge.amount = XMLParser.getElementValue(listAmount
								.item(i));
						charge.number = "";
						this.listCharge.add(charge);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			this.pDialog.dismiss();
			ActivityDebitHistory.this.lvHistory.setAdapter(new AdapterCharge(
					ActivityDebitHistory.this.mContext, this.listCharge));
		}
	}

	private class AsyncTaskSMS extends AsyncTask<String, String, String> {
		ArrayList<ModelSMSHistory> listSMS;
		ProgressDialog pDialog;

		private AsyncTaskSMS() {
			this.listSMS = new ArrayList();
		}

		protected void onPreExecute() {
			super.onPreExecute();
			this.pDialog = new ProgressDialog(
					ActivityDebitHistory.this.mContext);
			this.pDialog.setCancelable(false);
			this.pDialog.setMessage(ActivityDebitHistory.this.mContext
					.getString(R.string.message_loading));
			this.pDialog.show();
			this.listSMS.add(new ModelSMSHistory("Date", "Amount", "Number"));
		}

		protected String doInBackground(String... p) {
			try {
				String dateTo = p[1];
				String dateFrom = p[0];
				Log.v("", "tiench date: " + dateFrom + " - " + dateTo);
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam("callType", "2"));
				params.add(new ModelParam(mXML.SUB_ID, mProfile.subId));
				params.add(new ModelParam("fromDate", dateFrom));
				params.add(new ModelParam("toDate", dateTo));
				SOAPUtil soap = new SOAPUtil(
						WSCode.CALL_DETAIL_HISTORY_POSTPAID, tags, params);
				soap.makeSOAPRequest();
				NodeList listDate = soap.mDoc.getElementsByTagName("callDate");
				NodeList listNumber = soap.mDoc.getElementsByTagName("called");
				NodeList listAmount = soap.mDoc.getElementsByTagName("amount");
				for (int i = 0; i < listDate.getLength(); i++) {
					if (!XMLParser.getElementValue(listDate.item(i)).trim()
							.equals("")) {
						ModelSMSHistory sms = new ModelSMSHistory();
						sms.date = XMLParser.getElementValue(listDate.item(i));
						sms.number = XMLParser.getElementValue(listNumber
								.item(i));
						sms.cost = XMLParser
								.getElementValue(listAmount.item(i));
						this.listSMS.add(sms);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			this.pDialog.dismiss();
			ActivityDebitHistory.this.lvHistory
					.setAdapter(new AdapterSMSHistory(
							ActivityDebitHistory.this.mContext, this.listSMS));
		}
	}

	private class AsyncTaskVas extends AsyncTask<String, String, String> {
		ArrayList<ModelVasHistory> listVas;
		ProgressDialog pDialog;

		private AsyncTaskVas() {
			this.listVas = new ArrayList();
		}

		protected void onPreExecute() {
			super.onPreExecute();
			this.pDialog = new ProgressDialog(
					ActivityDebitHistory.this.mContext);
			this.pDialog.setCancelable(false);
			this.pDialog.setMessage(ActivityDebitHistory.this.mContext
					.getString(R.string.message_loading));
			this.pDialog.show();
			this.listVas.add(new ModelVasHistory("Date", "Type", "Service",
					"Amount"));
		}

		protected String doInBackground(String... p) {
			try {
				String dateTo = p[1];
				String dateFrom = p[0];
				Log.v("", "tiench date: " + dateFrom + " - " + dateTo);
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam("callType", "0"));
				params.add(new ModelParam(mXML.SUB_ID, mProfile.subId));
				params.add(new ModelParam("fromDate", dateFrom));
				params.add(new ModelParam("toDate", dateTo));
				SOAPUtil soap = new SOAPUtil(
						WSCode.CALL_DETAIL_HISTORY_POSTPAID, tags, params);
				soap.makeSOAPRequest();
				NodeList listDate = soap.mDoc.getElementsByTagName("callDate");
				NodeList listType = soap.mDoc.getElementsByTagName("itemName");
				NodeList listAmount = soap.mDoc.getElementsByTagName("amount");
				for (int i = 0; i < listDate.getLength(); i++) {
					if (!XMLParser.getElementValue(listDate.item(i)).trim()
							.equals("")) {
						ModelVasHistory vas = new ModelVasHistory();
						vas.date = XMLParser.getElementValue(listDate.item(i));
						vas.type = "VAS";
						vas.service = XMLParser.getElementValue(listType
								.item(i));
						vas.amount = XMLParser.getElementValue(listAmount
								.item(i));
						this.listVas.add(vas);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			this.pDialog.dismiss();
			ActivityDebitHistory.this.lvHistory
					.setAdapter(new AdapterVasHistory(
							ActivityDebitHistory.this.mContext, this.listVas));
		}
	}

	public ActivityDebitHistory() {
		this.sdf = new SimpleDateFormat("dd/MM/yyyy");
		this.myDateListener = new C05491();
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(1);
		setRequestedOrientation(7);
		this.mContext = this;
		this.preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
		setLanguage();
		setContentView(R.layout.activity_debit_history);
		initView();
	}

	private void initView() {
		((ImageView) findViewById(R.id.ivBack))
				.setOnClickListener(new C05502());
		this.lvHistory = (ListView) findViewById(R.id.lv_history);
		this.spType = (Spinner) findViewById(R.id.sp_type);
		ArrayList<String> types = new ArrayList();
		types.add("Choose history");
		types.add("Charge");
		types.add("SMS");
		types.add("VAS");
		this.spType.setAdapter(new ArrayAdapter(this,
				R.layout.row_item_spinner, types));
		this.edtFrom = (EditText) findViewById(R.id.edt_from);
		this.edtTo = (EditText) findViewById(R.id.edt_to);
		Date date = new Date();
		String dateTo = this.sdf.format(date);
		addDays(date, -10);
		this.edtFrom.setText(this.sdf.format(date));
		this.edtTo.setText(dateTo);
		this.edtFrom.setOnClickListener(new C05513());
		this.edtTo.setOnClickListener(new C05524());
		this.btnSearch = (RelativeLayout) findViewById(R.id.btn_search);
		this.btnSearch.setOnClickListener(new C05535());
	}

	private void submitSearch() {
		if (this.spType.getSelectedItemPosition() == 0) {
			new DialogReport(this.mContext, "You need to select type history")
					.show();
			return;
		}
		try {
			String dateFrom = this.edtFrom.getText().toString().trim();
			String dateTo = this.edtTo.getText().toString().trim();
			Date fr = this.sdf.parse(dateFrom);
			addDays(fr, 10);
			if (this.sdf.parse(dateTo).after(fr)) {
				new DialogReport(this.mContext,
						"Please select time in range 10 days").show();
				return;
			}
			switch (this.spType.getSelectedItemPosition()) {
			case 1:
				new AsyncTaskCharge()
						.execute(new String[] { dateFrom, dateTo });
				break;
			case 2:
				new AsyncTaskSMS().execute(new String[] { dateFrom, dateTo });
				break;
			case 3:
				new AsyncTaskVas().execute(new String[] { dateFrom, dateTo });
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			new DialogReport(this.mContext, "Invalid date").show();
		}
	}

	private void actionBack() {
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

	public void onBackPressed() {
		actionBack();
	}

	public static void addDays(Date d, int days) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(5, days);
		d.setTime(c.getTime().getTime());
	}
}
