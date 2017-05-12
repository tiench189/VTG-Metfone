package com.vtg.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.vtg.app.component.AdapterCharge;
import com.vtg.app.component.AdapterInternet;
import com.vtg.app.component.AdapterRefill;
import com.vtg.app.component.AdapterSMSHistory;
import com.vtg.app.component.AdapterVasHistory;
import com.vtg.app.component.DialogReport;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelCharge;
import com.vtg.app.model.ModelInternet;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelRefill;
import com.vtg.app.model.ModelSMSHistory;
import com.vtg.app.model.ModelTag;
import com.vtg.app.model.ModelVasHistory;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.SOAPUtil;
import com.vtg.app.util.XMLParser;

public class ActivityRefillHistory extends Activity implements CommonDefine {
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

	/* renamed from: com.vtg.app.ActivityRefillHistory.1 */
	class C05701 implements OnDateSetListener {
		C05701() {
		}

		public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
			Date d = new Date();
			Log.v("", "tiench: " + arg1);
			SimpleDateFormat sf = new SimpleDateFormat("dd/MM/");
			d.setYear(arg1);
			d.setMonth(arg2);
			d.setDate(arg3);
			ActivityRefillHistory.this.currentEdt.setText(sf.format(d) + arg1);
		}
	}

	/* renamed from: com.vtg.app.ActivityRefillHistory.2 */
	class C05712 implements OnClickListener {
		C05712() {
		}

		public void onClick(View v) {
			ActivityRefillHistory.this.actionBack();
		}
	}

	/* renamed from: com.vtg.app.ActivityRefillHistory.3 */
	class C05723 implements OnClickListener {
		C05723() {
		}

		public void onClick(View v) {
			ActivityRefillHistory.this.currentEdt = ActivityRefillHistory.this.edtFrom;
			int[] time = FunctionHelper
					.getTimeFromEdt(ActivityRefillHistory.this.edtFrom);
			new DatePickerDialog(ActivityRefillHistory.this.mContext,
					ActivityRefillHistory.this.myDateListener, time[0],
					time[1], time[2]).show();
		}
	}

	/* renamed from: com.vtg.app.ActivityRefillHistory.4 */
	class C05734 implements OnClickListener {
		C05734() {
		}

		public void onClick(View v) {
			ActivityRefillHistory.this.currentEdt = ActivityRefillHistory.this.edtTo;
			int[] time = FunctionHelper
					.getTimeFromEdt(ActivityRefillHistory.this.edtTo);
			new DatePickerDialog(ActivityRefillHistory.this.mContext,
					ActivityRefillHistory.this.myDateListener, time[0],
					time[1], time[2]).show();
		}
	}

	/* renamed from: com.vtg.app.ActivityRefillHistory.5 */
	class C05745 implements OnClickListener {
		C05745() {
		}

		public void onClick(View v) {
			ActivityRefillHistory.this.submitSearch();
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
					ActivityRefillHistory.this.mContext);
			this.pDialog.setCancelable(false);
			this.pDialog.setMessage(ActivityRefillHistory.this.mContext
					.getString(R.string.message_loading));
			this.pDialog.show();
			this.listCharge.add(new ModelCharge("Date", "Type",
					"Duration (Second)", "Amount (USD)", "Number"));
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
				params.add(new ModelParam(mXML.ISDN, new StringBuilder(
						CommonDefine.NUMBER_HEADER).append(
						ActivityRefillHistory.this.preferences.getString(
								PreferenceKey.PHONE_NUMBER, "")).toString()));
				params.add(new ModelParam("fromDate", dateFrom));
				params.add(new ModelParam("toDate", dateTo));
				SOAPUtil soap = new SOAPUtil(WSCode.GET_CHARGE_DETAIL, tags,
						params);
				soap.makeSOAPRequest();
				NodeList listDate = soap.mDoc
						.getElementsByTagName(mXML.START_DATE_TIME);
				NodeList listDuration = soap.mDoc
						.getElementsByTagName("duration");
				NodeList listAmount = soap.mDoc
						.getElementsByTagName("callBasicCost");
				NodeList listNumber = soap.mDoc
						.getElementsByTagName("calledNumber");
				for (int i = 0; i < listDate.getLength(); i++) {
					if (!XMLParser.getElementValue(listDate.item(i)).trim()
							.equals("")) {
						ModelCharge charge = new ModelCharge();
						charge.date = XMLParser.getElementValue(listDate
								.item(i));
						charge.type = "CALL";
						charge.duration = XMLParser
								.getElementValue(listDuration.item(i));
						charge.amount = FunctionHelper
								.formatFloatMoney(Float.parseFloat(XMLParser
										.getElementValue(listAmount.item(i))
										.replaceAll(",", ""))
										/ DIVIDER_MONEY);
						charge.number = XMLParser.getElementValue(listNumber
								.item(i));
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
			ActivityRefillHistory.this.lvHistory.setAdapter(new AdapterCharge(
					ActivityRefillHistory.this.mContext, this.listCharge));
		}
	}

	private class AsyncTaskRefill extends AsyncTask<String, String, String> {
		ArrayList<ModelRefill> listRefill;
		ProgressDialog pDialog;

		private AsyncTaskRefill() {
			this.listRefill = new ArrayList();
		}

		protected void onPreExecute() {
			super.onPreExecute();
			this.pDialog = new ProgressDialog(
					ActivityRefillHistory.this.mContext);
			this.pDialog.setCancelable(false);
			this.pDialog.setMessage(ActivityRefillHistory.this.mContext
					.getString(R.string.message_loading));
			this.pDialog.show();
			this.listRefill.add(new ModelRefill("Date", "Serial No", "Type",
					"Amount (USD)"));
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
				params.add(new ModelParam(mXML.ISDN,
						ActivityRefillHistory.this.preferences.getString(
								PreferenceKey.PHONE_NUMBER, "")));
				params.add(new ModelParam("fromDate", dateFrom));
				params.add(new ModelParam("toDate", dateTo));
				SOAPUtil soap = new SOAPUtil(WSCode.GET_CARD_HISTORY, tags,
						params);
				soap.makeSOAPRequest();
				NodeList listDate = soap.mDoc
						.getElementsByTagName("refillDate");
				NodeList listCode = soap.mDoc
						.getElementsByTagName("seriNumber");
				NodeList listAmount = soap.mDoc
						.getElementsByTagName("refillAmount");
				for (int i = 0; i < listDate.getLength(); i++) {
					if (!XMLParser.getElementValue(listDate.item(i)).trim()
							.equals("")) {
						ModelRefill refill = new ModelRefill();
						refill.date = XMLParser.getElementValue(listDate
								.item(i));
						refill.type = "card";
						String code = XMLParser.getElementValue(listCode
								.item(i));
						// refill.code = "xxx" + code.substring(code.length() -
						// 3);
						refill.code = code;
						refill.amount = XMLParser.getElementValue(listAmount
								.item(i));
						this.listRefill.add(refill);
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
			ActivityRefillHistory.this.lvHistory.setAdapter(new AdapterRefill(
					ActivityRefillHistory.this.mContext, this.listRefill));
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
					ActivityRefillHistory.this.mContext);
			this.pDialog.setCancelable(false);
			this.pDialog.setMessage(ActivityRefillHistory.this.mContext
					.getString(R.string.message_loading));
			this.pDialog.show();
			this.listSMS.add(new ModelSMSHistory("Date", "Amount (USD)",
					"Number"));
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
				params.add(new ModelParam(mXML.ISDN, new StringBuilder(
						CommonDefine.NUMBER_HEADER).append(
						ActivityRefillHistory.this.preferences.getString(
								PreferenceKey.PHONE_NUMBER, "")).toString()));
				params.add(new ModelParam("fromDate", dateFrom));
				params.add(new ModelParam("toDate", dateTo));
				SOAPUtil soap = new SOAPUtil(WSCode.GET_SMS_DETAIL, tags,
						params);
				soap.makeSOAPRequest();
				NodeList listDate = soap.mDoc
						.getElementsByTagName("staDatetime");
				NodeList listNumber = soap.mDoc
						.getElementsByTagName("calledNumber");
				NodeList listAmount = soap.mDoc.getElementsByTagName("smsCost");
				for (int i = 0; i < listDate.getLength(); i++) {
					if (!XMLParser.getElementValue(listDate.item(i)).trim()
							.equals("")) {
						ModelSMSHistory sms = new ModelSMSHistory();
						sms.date = XMLParser.getElementValue(listDate.item(i));
						sms.number = XMLParser.getElementValue(listNumber
								.item(i));
						sms.cost = FunctionHelper.formatFloatMoney(Float
								.parseFloat(XMLParser
										.getElementValue(listAmount.item(i)))
								/ DIVIDER_MONEY);
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
			ActivityRefillHistory.this.lvHistory
					.setAdapter(new AdapterSMSHistory(
							ActivityRefillHistory.this.mContext, this.listSMS));
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
					ActivityRefillHistory.this.mContext);
			this.pDialog.setCancelable(false);
			this.pDialog.setMessage(ActivityRefillHistory.this.mContext
					.getString(R.string.message_loading));
			this.pDialog.show();
			this.listVas.add(new ModelVasHistory("Date", "Type", "Service",
					"Amount (USD)"));
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
				params.add(new ModelParam(mXML.ISDN, new StringBuilder(
						CommonDefine.NUMBER_HEADER).append(
						ActivityRefillHistory.this.preferences.getString(
								PreferenceKey.PHONE_NUMBER, "")).toString()));
				params.add(new ModelParam("fromDate", dateFrom));
				params.add(new ModelParam("toDate", dateTo));
				params.add(new ModelParam("type", "1"));
				SOAPUtil soap = new SOAPUtil(WSCode.GET_VAS_DETAIL, tags,
						params);
				soap.makeSOAPRequest();
				NodeList listDate = soap.mDoc
						.getElementsByTagName("staDatetime");
				NodeList listType = soap.mDoc
						.getElementsByTagName("calledNumber");
				NodeList listService = soap.mDoc.getElementsByTagName("type");
				NodeList listAmount = soap.mDoc
						.getElementsByTagName("callBasicCost");
				for (int i = 0; i < listDate.getLength(); i++) {
					if (!XMLParser.getElementValue(listDate.item(i)).trim()
							.equals("")) {
						ModelVasHistory vas = new ModelVasHistory();
						vas.date = XMLParser.getElementValue(listDate.item(i));
						vas.type = XMLParser.getElementValue(listType.item(i));
						vas.service = XMLParser.getElementValue(listService
								.item(i));
						vas.amount = FunctionHelper.formatFloatMoney(Float
								.parseFloat(XMLParser
										.getElementValue(listAmount.item(i)))
								/ DIVIDER_MONEY);
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
			ActivityRefillHistory.this.lvHistory
					.setAdapter(new AdapterVasHistory(
							ActivityRefillHistory.this.mContext, this.listVas));
		}
	}

	private class AsyncTaskInternet extends AsyncTask<String, String, String> {
		ArrayList<ModelInternet> listInternet;
		ProgressDialog pDialog;

		private AsyncTaskInternet() {
			this.listInternet = new ArrayList();
		}

		protected void onPreExecute() {
			super.onPreExecute();
			this.pDialog = new ProgressDialog(
					ActivityRefillHistory.this.mContext);
			this.pDialog.setCancelable(false);
			this.pDialog.setMessage(ActivityRefillHistory.this.mContext
					.getString(R.string.message_loading));
			this.pDialog.show();
			this.listInternet.add(new ModelInternet("Date", "Type",
					"Data using (MB)", "Amount (USD)"));
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
				params.add(new ModelParam(mXML.ISDN, new StringBuilder(
						CommonDefine.NUMBER_HEADER).append(
						ActivityRefillHistory.this.preferences.getString(
								PreferenceKey.PHONE_NUMBER, "")).toString()));
				params.add(new ModelParam("fromDate", dateFrom));
				params.add(new ModelParam("toDate", dateTo));
				params.add(new ModelParam("type", "0"));
				SOAPUtil soap = new SOAPUtil(WSCode.GET_VAS_DETAIL, tags,
						params);
				soap.makeSOAPRequest();
				NodeList listDate = soap.mDoc
						.getElementsByTagName("staDatetime");
				NodeList listType = soap.mDoc.getElementsByTagName("productId");
				NodeList listData = soap.mDoc.getElementsByTagName("volumeUp");
				NodeList listAmount = soap.mDoc
						.getElementsByTagName("callBasicCost");
				for (int i = 0; i < listDate.getLength(); i++) {
					if (!XMLParser.getElementValue(listDate.item(i)).trim()
							.equals("")) {
						ModelInternet vas = new ModelInternet();
						vas.date = XMLParser.getElementValue(listDate.item(i));
						vas.type = XMLParser.getElementValue(listType.item(i));
						Log.v("",
								"tiench data "
										+ XMLParser.getElementValue(listData
												.item(i)));
						String data = XMLParser.getElementValue(
								listData.item(i)).replace(",", "");
						if (!data.equals("")) {
							vas.dataUsing = FunctionHelper
									.formatFloatData((float) Integer
											.parseInt(data) / 1024 / 1024);
						} else {
							vas.dataUsing = "";
						}
						vas.amount = FunctionHelper.formatFloatMoney(Float
								.parseFloat(XMLParser
										.getElementValue(listAmount.item(i)))
								/ DIVIDER_MONEY);
						this.listInternet.add(vas);
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
			lvHistory.setAdapter(new AdapterInternet(mContext,
					this.listInternet));
		}
	}

	public ActivityRefillHistory() {
		this.sdf = new SimpleDateFormat("dd/MM/yyyy");
		this.myDateListener = new C05701();
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(1);
		setRequestedOrientation(7);
		setContentView(R.layout.activity_refill_history);
		this.mContext = this;
		this.preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
		initView();
	}

	private void initView() {
		((ImageView) findViewById(R.id.ivBack))
				.setOnClickListener(new C05712());
		this.lvHistory = (ListView) findViewById(R.id.lv_history);
		this.spType = (Spinner) findViewById(R.id.sp_type);
		ArrayList<String> types = new ArrayList();
		types.add("Choose history");
		types.add("Top-up");
		types.add("Call");
		types.add("SMS");
		types.add("Internet");
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
		this.edtFrom.setOnClickListener(new C05723());
		this.edtTo.setOnClickListener(new C05734());
		this.btnSearch = (RelativeLayout) findViewById(R.id.btn_search);
		this.btnSearch.setOnClickListener(new C05745());
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
				new AsyncTaskRefill()
						.execute(new String[] { dateFrom, dateTo });
				break;
			case 2:
				new AsyncTaskCharge()
						.execute(new String[] { dateFrom, dateTo });
				break;
			case 3:
				new AsyncTaskSMS().execute(new String[] { dateFrom, dateTo });
				break;
			case 4:
				new AsyncTaskInternet()
						.execute(new String[] { dateFrom, dateTo });
				break;
			case 5:
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
