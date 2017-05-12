package com.vtg.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import com.vtg.app.ActivityMain.mProfile;
import com.vtg.app.component.AdapterId;
import com.vtg.app.component.DialogReport;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelArea;
import com.vtg.app.model.ModelId;
import com.vtg.app.model.ModelPaper;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelTag;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.Action;
import com.vtg.app.util.CommonDefine.PreferenceKey;
import com.vtg.app.util.CommonDefine.SoapTag;
import com.vtg.app.util.CommonDefine.WSCode;
import com.vtg.app.util.CommonDefine.mXML;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.SOAPRegisProfile;
import com.vtg.app.util.SOAPUtil;
import com.vtg.app.util.XMLParser;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.w3c.dom.NodeList;

public class ActivityRegisterProfile extends Activity implements CommonDefine {
    private RelativeLayout btnCancel;
    private RelativeLayout btnDone;
    private String currentCode;
    private EditText currentEdt;
    private EditText edtAddress;
    private EditText edtBirthDate;
    private EditText edtBirthPlace;
    private EditText edtContactName;
    private EditText edtHome;
    private EditText edtIdNo;
    private EditText edtIsdn;
    private EditText edtIssueDate;
    private EditText edtIssuePlace;
    private EditText edtSerial;
    private ImageView ivBack;
    private List<ModelArea> listDistrict;
    private List<ModelId> listIds;
    private List<ModelArea> listPrecint;
    private List<ModelArea> listProvince;
    private Context mContext;
    private OnDateSetListener myDateListener;
    private ProgressDialog pDialog;
    private List<ModelPaper> papers;
    private SharedPreferences preferences;
    SimpleDateFormat sdf;
    private Spinner spDistrict;
    private Spinner spId;
    private Spinner spPrecint;
    private Spinner spProvince;
    private Spinner spSex;

    /* renamed from: com.vtg.app.ActivityRegisterProfile.1 */
    class C05791 implements OnDateSetListener {
        C05791() {
        }

        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            Date d = new Date();
            Log.v("", "tiench: " + arg1);
            SimpleDateFormat sf = new SimpleDateFormat("dd/MM/");
            d.setYear(arg1);
            d.setMonth(arg2);
            d.setDate(arg3);
            ActivityRegisterProfile.this.currentEdt.setText(sf.format(d) + arg1);
        }
    }

    /* renamed from: com.vtg.app.ActivityRegisterProfile.2 */
    class C05802 implements OnClickListener {
        C05802() {
        }

        public void onClick(View v) {
            ActivityRegisterProfile.this.actionBack();
        }
    }

    /* renamed from: com.vtg.app.ActivityRegisterProfile.3 */
    class C05813 implements OnClickListener {
        C05813() {
        }

        public void onClick(View v) {
            ActivityRegisterProfile.this.currentEdt = ActivityRegisterProfile.this.edtBirthDate;
            int[] time = FunctionHelper.getTimeFromEdt(ActivityRegisterProfile.this.edtBirthDate);
            new DatePickerDialog(ActivityRegisterProfile.this.mContext, ActivityRegisterProfile.this.myDateListener, time[0], time[1], time[2]).show();
        }
    }

    /* renamed from: com.vtg.app.ActivityRegisterProfile.4 */
    class C05824 implements OnClickListener {
        C05824() {
        }

        public void onClick(View v) {
            ActivityRegisterProfile.this.currentEdt = ActivityRegisterProfile.this.edtIssueDate;
            int[] time = FunctionHelper.getTimeFromEdt(ActivityRegisterProfile.this.edtIssueDate);
            new DatePickerDialog(ActivityRegisterProfile.this.mContext, ActivityRegisterProfile.this.myDateListener, time[0], time[1], time[2]).show();
        }
    }

    /* renamed from: com.vtg.app.ActivityRegisterProfile.5 */
    class C05835 implements OnItemSelectedListener {
        C05835() {
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            ActivityRegisterProfile.this.updatePaper();
            ActivityRegisterProfile.this.currentCode = ((ModelId) ActivityRegisterProfile.this.listIds.get(position)).code;
            ActivityRegisterProfile.this.setPaper(FunctionHelper.findPaper(ActivityRegisterProfile.this.currentCode, ActivityRegisterProfile.this.papers));
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: com.vtg.app.ActivityRegisterProfile.6 */
    class C05846 implements OnItemSelectedListener {
        C05846() {
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            ActivityRegisterProfile.this.listDistrict = new ArrayList();
            ActivityRegisterProfile.this.listPrecint = new ArrayList();
            ActivityRegisterProfile.this.spDistrict.setAdapter(new ArrayAdapter(ActivityRegisterProfile.this.mContext, R.layout.row_item_spinner, FunctionHelper.convertArea(ActivityRegisterProfile.this.listDistrict)));
            ActivityRegisterProfile.this.spPrecint.setAdapter(new ArrayAdapter(ActivityRegisterProfile.this.mContext, R.layout.row_item_spinner, FunctionHelper.convertArea(ActivityRegisterProfile.this.listPrecint)));
            if (position > 0) {
                new AsyncTaskDistrict().execute(new String[]{((ModelArea) ActivityRegisterProfile.this.listProvince.get(position)).code});
            }
            ActivityRegisterProfile.this.edtAddress.setText(ActivityRegisterProfile.this.getAddress());
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: com.vtg.app.ActivityRegisterProfile.7 */
    class C05857 implements OnItemSelectedListener {
        C05857() {
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            ActivityRegisterProfile.this.listPrecint = new ArrayList();
            ActivityRegisterProfile.this.spPrecint.setAdapter(new ArrayAdapter(ActivityRegisterProfile.this.mContext, R.layout.row_item_spinner, FunctionHelper.convertArea(ActivityRegisterProfile.this.listPrecint)));
            if (position > 0) {
                new AsyncTaskPrecint().execute(new String[]{((ModelArea) ActivityRegisterProfile.this.listProvince.get(ActivityRegisterProfile.this.spProvince.getSelectedItemPosition())).code, ((ModelArea) ActivityRegisterProfile.this.listDistrict.get(position)).district});
            }
            ActivityRegisterProfile.this.edtAddress.setText(ActivityRegisterProfile.this.getAddress());
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: com.vtg.app.ActivityRegisterProfile.8 */
    class C05868 implements OnItemSelectedListener {
        C05868() {
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            ActivityRegisterProfile.this.edtAddress.setText(ActivityRegisterProfile.this.getAddress());
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: com.vtg.app.ActivityRegisterProfile.9 */
    class C05879 implements OnClickListener {
        C05879() {
        }

        public void onClick(View v) {
            ActivityRegisterProfile.this.actionBack();
        }
    }

    private class AsyncTaskDistrict extends AsyncTask<String, String, Boolean> {
        private AsyncTaskDistrict() {
        }

        protected Boolean doInBackground(String... p) {
            try {
                List<ModelTag> tags = new ArrayList();
                tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
                tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
                List<ModelParam> params = new ArrayList();
                SOAPUtil soap = new SOAPUtil(WSCode.GET_DISTRICT, tags, params);
                params.add(new ModelParam("provinceCode", p[0]));
                soap.makeSOAPRequest();
                NodeList listCode = soap.mDoc.getElementsByTagName("areaCode");
                NodeList listName = soap.mDoc.getElementsByTagName("fullName");
                NodeList listDt = soap.mDoc.getElementsByTagName("district");
                for (int i = 0; i < listCode.getLength(); i++) {
                    if (!XMLParser.getElementValue(listName.item(i)).trim().equals("")) {
                        ModelArea area = new ModelArea(XMLParser.getElementValue(listCode.item(i)), XMLParser.getElementValue(listName.item(i)));
                        area.district = XMLParser.getElementValue(listDt.item(i));
                        ActivityRegisterProfile.this.listDistrict.add(area);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            ActivityRegisterProfile.this.pDialog.show();
            ActivityRegisterProfile.this.listDistrict.add(new ModelArea("", ""));
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            ActivityRegisterProfile.this.pDialog.dismiss();
            ActivityRegisterProfile.this.spDistrict.setAdapter(new ArrayAdapter(ActivityRegisterProfile.this.mContext, R.layout.row_item_spinner, FunctionHelper.convertArea(ActivityRegisterProfile.this.listDistrict)));
        }
    }

    private class AsyncTaskPrecint extends AsyncTask<String, String, Boolean> {
        private AsyncTaskPrecint() {
        }

        protected Boolean doInBackground(String... p) {
            try {
                List<ModelTag> tags = new ArrayList();
                tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
                tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
                List<ModelParam> params = new ArrayList();
                SOAPUtil soap = new SOAPUtil(WSCode.GET_PRECINT, tags, params);
                params.add(new ModelParam("provinceCode", p[0]));
                params.add(new ModelParam("districtCode", p[1]));
                soap.makeSOAPRequest();
                NodeList listCode = soap.mDoc.getElementsByTagName("areaCode");
                NodeList listName = soap.mDoc.getElementsByTagName("fullName");
                for (int i = 0; i < listCode.getLength(); i++) {
                    if (!XMLParser.getElementValue(listName.item(i)).trim().equals("")) {
                        ActivityRegisterProfile.this.listPrecint.add(new ModelArea(XMLParser.getElementValue(listCode.item(i)), XMLParser.getElementValue(listName.item(i))));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            ActivityRegisterProfile.this.pDialog.show();
            ActivityRegisterProfile.this.listPrecint.add(new ModelArea("", ""));
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            ActivityRegisterProfile.this.pDialog.dismiss();
            ActivityRegisterProfile.this.spPrecint.setAdapter(new ArrayAdapter(ActivityRegisterProfile.this.mContext, R.layout.row_item_spinner, FunctionHelper.convertArea(ActivityRegisterProfile.this.listPrecint)));
        }
    }

    private class AsyncTaskProvince extends AsyncTask<String, String, Boolean> {
        private AsyncTaskProvince() {
        }

        protected Boolean doInBackground(String... p) {
            try {
                List<ModelTag> tags = new ArrayList();
                tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
                tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
                SOAPUtil soap = new SOAPUtil(WSCode.GET_PROVINCE, tags, new ArrayList());
                soap.makeSOAPRequest();
                NodeList listCode = soap.mDoc.getElementsByTagName("areaCode");
                NodeList listName = soap.mDoc.getElementsByTagName("fullName");
                for (int i = 0; i < listCode.getLength(); i++) {
                    if (!XMLParser.getElementValue(listName.item(i)).trim().equals("")) {
                        ActivityRegisterProfile.this.listProvince.add(new ModelArea(XMLParser.getElementValue(listCode.item(i)), XMLParser.getElementValue(listName.item(i))));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            ActivityRegisterProfile.this.pDialog.show();
            ActivityRegisterProfile.this.listProvince.add(new ModelArea("", ""));
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            ActivityRegisterProfile.this.pDialog.dismiss();
            ActivityRegisterProfile.this.spProvince.setAdapter(new ArrayAdapter(ActivityRegisterProfile.this.mContext, R.layout.row_item_spinner, FunctionHelper.convertArea(ActivityRegisterProfile.this.listProvince)));
        }
    }

    private class AsyncTaskRegis extends AsyncTask<String, String, Boolean> {
        String message;
        ProgressDialog pDialog;

        /* renamed from: com.vtg.app.ActivityRegisterProfile.AsyncTaskRegis.1 */
        class C05881 implements OnClickListener {
            C05881() {
            }

            public void onClick(View v) {
                ActivityRegisterProfile.this.finish();
            }
        }

        private AsyncTaskRegis() {
            this.message = "";
        }

        protected Boolean doInBackground(String... prs) {
            SOAPRegisProfile soap = new SOAPRegisProfile(prs[0]);
            soap.makeSOAPRequest();
            if (soap.getError() == 0) {
                int errCode = -1;
                try {
                    errCode = Integer.parseInt(soap.getValue(mXML.RESPONSE_CODE));
                } catch (Exception e) {
                }
                if (errCode == 1) {
                    this.message = mContext.getString(R.string.message_success);
                    return Boolean.valueOf(true);
                }
                this.message = soap.getValue(mXML.DESCRIPTION);
                return Boolean.valueOf(false);
            }
            this.message = mContext.getString(R.string.message_fail);
            return Boolean.valueOf(false);
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog = new ProgressDialog(ActivityRegisterProfile.this.mContext);
            this.pDialog.setMessage(ActivityRegisterProfile.this.mContext.getString(R.string.message_loading));
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
                ActivityRegisterProfile.this.mContext.sendBroadcast(intent);
                DialogReport sc = new DialogReport(ActivityRegisterProfile.this.mContext, this.message);
                sc.btnOK.setOnClickListener(new C05881());
                sc.show();
                return;
            }
            new DialogReport(ActivityRegisterProfile.this.mContext, this.message).show();
        }
    }

    public ActivityRegisterProfile() {
        this.currentCode = "";
        this.sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.myDateListener = new C05791();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setRequestedOrientation(7);
        setContentView(R.layout.activity_regis_prfile);
        this.mContext = this;
        this.preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
        this.pDialog = new ProgressDialog(this.mContext);
        this.pDialog.setCancelable(false);
        this.pDialog.setMessage(getString(R.string.message_loading));
        this.listProvince = new ArrayList();
        this.listDistrict = new ArrayList();
        this.listPrecint = new ArrayList();
        this.listIds = new ArrayList();
        this.listIds.add(new ModelId("", ""));
        this.listIds.add(new ModelId("1", "ID Card"));
        this.listIds.add(new ModelId("2", "Driver license"));
        this.listIds.add(new ModelId("3", "Passport"));
        this.listIds.add(new ModelId("4", "Army ID card"));
        this.listIds.add(new ModelId("5", "Others"));
        this.papers = new ArrayList();
        initView();
    }

    public void initView() {
        this.ivBack = (ImageView) findViewById(R.id.ivBack);
        this.ivBack.setOnClickListener(new C05802());
        this.edtIsdn = (EditText) findViewById(R.id.edt_isdn);
        this.edtIsdn.setText(this.preferences.getString(PreferenceKey.PHONE_NUMBER, ""));
        this.edtContactName = (EditText) findViewById(R.id.edt_contact_name);
        this.edtContactName.requestFocus();
        this.edtContactName.setText(mProfile.userUsing);
        this.edtHome = (EditText) findViewById(R.id.edt_home);
        this.edtHome.setText(mProfile.address);
        this.edtBirthDate = (EditText) findViewById(R.id.edt_birthday);
        this.edtBirthDate.setText(mProfile.birthDay);
        this.edtBirthDate.setOnClickListener(new C05813());
        this.edtBirthPlace = (EditText) findViewById(R.id.edt_birth_place);
        this.edtIdNo = (EditText) findViewById(R.id.edt_id_no);
        this.edtIssueDate = (EditText) findViewById(R.id.edt_issue_date);
        this.edtIssueDate.setOnClickListener(new C05824());
        this.edtIssuePlace = (EditText) findViewById(R.id.edt_issue_place);
        this.edtSerial = (EditText) findViewById(R.id.edt_serial);
        this.spId = (Spinner) findViewById(R.id.sp_id);
        this.spProvince = (Spinner) findViewById(R.id.sp_province);
        this.spDistrict = (Spinner) findViewById(R.id.sp_district);
        this.spPrecint = (Spinner) findViewById(R.id.sp_precint);
        this.spSex = (Spinner) findViewById(R.id.sp_sex);
        this.spId.setAdapter(new AdapterId(this.mContext, this.listIds));
        this.spId.setOnItemSelectedListener(new C05835());
        this.edtAddress = (EditText) findViewById(R.id.edt_address);
        ArrayList<String> sex = new ArrayList();
        sex.add("Male");
        sex.add("Female");
        this.spSex.setAdapter(new ArrayAdapter(this.mContext, R.layout.row_item_spinner, sex));
        this.spProvince.setOnItemSelectedListener(new C05846());
        this.spDistrict.setOnItemSelectedListener(new C05857());
        this.spPrecint.setOnItemSelectedListener(new C05868());
        new AsyncTaskProvince().execute(new String[0]);
        this.btnCancel = (RelativeLayout) findViewById(R.id.btn_cancel);
        this.btnCancel.setOnClickListener(new C05879());
        this.btnDone = (RelativeLayout) findViewById(R.id.btn_done);
        this.btnDone.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ActivityRegisterProfile.this.submitRegister();
            }
        });
    }

    private void updatePaper() {
        String no = this.edtIdNo.getText().toString().trim();
        String date = this.edtIssueDate.getText().toString().trim();
        String place = this.edtIssuePlace.getText().toString().trim();
        if (!no.equals("")) {
            if (FunctionHelper.findPaper(this.currentCode, this.papers).type.equals("")) {
                this.papers.add(new ModelPaper(no, this.currentCode, date, place));
                return;
            }
            for (int i = 0; i < this.papers.size(); i++) {
                if (((ModelPaper) this.papers.get(i)).type.equals(this.currentCode)) {
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

    private void actionBack() {
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void onBackPressed() {
        actionBack();
    }

    private String getAddress() {
        String address = "";
        if (this.spPrecint.getSelectedItemPosition() > 0) {
            return new StringBuilder(String.valueOf(((ModelArea) this.listPrecint.get(this.spPrecint.getSelectedItemPosition())).name)).append(", ").append(((ModelArea) this.listDistrict.get(this.spDistrict.getSelectedItemPosition())).name).append(", ").append(((ModelArea) this.listProvince.get(this.spProvince.getSelectedItemPosition())).name).toString();
        }
        if (this.spDistrict.getSelectedItemPosition() > 0) {
            return new StringBuilder(String.valueOf(((ModelArea) this.listDistrict.get(this.spDistrict.getSelectedItemPosition())).name)).append(", ").append(((ModelArea) this.listProvince.get(this.spProvince.getSelectedItemPosition())).name).toString();
        }
        return ((ModelArea) this.listProvince.get(this.spProvince.getSelectedItemPosition())).name;
    }

    private void submitRegister() {
        String idNo = this.edtIdNo.getText().toString();
        String idType = ((ModelId) this.listIds.get(this.spId.getSelectedItemPosition())).code;
        String issueDate = this.edtIssueDate.getText().toString();
        String issuePlace = this.edtIssuePlace.getText().toString();
        String address = "";
        String areaCode = "";
        if (this.spPrecint.getSelectedItemPosition() > 0) {
            areaCode = ((ModelArea) this.listPrecint.get(this.spPrecint.getSelectedItemPosition())).code;
            address = new StringBuilder(String.valueOf(((ModelArea) this.listPrecint.get(this.spPrecint.getSelectedItemPosition())).name)).append(", ").append(((ModelArea) this.listDistrict.get(this.spDistrict.getSelectedItemPosition())).name).append(", ").append(((ModelArea) this.listProvince.get(this.spProvince.getSelectedItemPosition())).name).toString();
        } else {
            if (this.spDistrict.getSelectedItemPosition() > 0) {
                areaCode = ((ModelArea) this.listDistrict.get(this.spDistrict.getSelectedItemPosition())).code;
                address = new StringBuilder(String.valueOf(((ModelArea) this.listDistrict.get(this.spDistrict.getSelectedItemPosition())).name)).append(", ").append(((ModelArea) this.listProvince.get(this.spProvince.getSelectedItemPosition())).name).toString();
            } else {
                areaCode = ((ModelArea) this.listProvince.get(this.spProvince.getSelectedItemPosition())).code;
                address = ((ModelArea) this.listProvince.get(this.spProvince.getSelectedItemPosition())).name;
            }
        }
        String birthDate = this.edtBirthDate.getText().toString();
        String birthPlace = this.edtBirthPlace.getText().toString();
        String contactName = this.edtContactName.getText().toString();
        String district = "";
        try {
            district = ((ModelArea) this.listDistrict.get(this.spDistrict.getSelectedItemPosition())).code;
        } catch (Exception e) {
        }
        String precinct = "";
        try {
            precinct = ((ModelArea) this.listPrecint.get(this.spPrecint.getSelectedItemPosition())).code;
        } catch (Exception e2) {
        }
        String province = "";
        try {
            province = ((ModelArea) this.listProvince.get(this.spProvince.getSelectedItemPosition())).code;
        } catch (Exception e3) {
        }
        String home = this.edtHome.getText().toString();
        String serial = this.edtSerial.getText().toString();
        String sex = this.spSex.getSelectedItemPosition() == 0 ? "M" : "F";
        if (serial.equals("")) {
            new DialogReport(this.mContext, "Serial cannot empty").show();
            return;
        }
        String request = ("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.bccsgw.viettel.com/\"><soapenv:Header/><soapenv:Body><web:gwOperation><Input><username>f319dce9a403a11a4687ccba21e07165</username><password>f319dce9a403a11a1755ef423296a346</password><wscode>SCregisterCustForSelfCare</wscode><rawData><![CDATA[<ws:registerCustForSelfCare><registerMobileForm><customerForm><idNo>" + idNo + "</idNo>" + "<idType>" + idType + "</idType>" + "<issueDate>" + issueDate + "</issueDate>" + "<issuePlace>" + issuePlace + "</issuePlace>" + "<address>" + address + "</address>" + "<areaCode>" + areaCode + "</areaCode>" + "<birthDate>" + birthDate + "</birthDate>" + "<birthPlace>" + birthPlace + "</birthPlace>" + "<busType>INDN</busType>" + "<contactName>" + contactName + "</contactName>" + "<district>" + district + "</district>" + "<home>" + home + "</home>" + "<isdn>" + this.preferences.getString(PreferenceKey.PHONE_NUMBER, "") + "</isdn>" + "<name>" + contactName + "</name>" + "<precinct>" + precinct + "</precinct>" + "<province>" + province + "</province>" + "<serial>" + serial + "</serial>" + "<sex>" + sex + "</sex>" + "</customerForm>" + "</registerMobileForm>" + "<wsUsername>1</wsUsername>" + "<wsPassword>1</wsPassword>" + "</ws:registerCustForSelfCare>]]></rawData>" + "</Input>" + "</web:gwOperation>" + "</soapenv:Body>" + "</soapenv:Envelope>");
        Log.v("", "tiench request: " + request);
        new AsyncTaskRegis().execute(new String[]{request});
    }
}
