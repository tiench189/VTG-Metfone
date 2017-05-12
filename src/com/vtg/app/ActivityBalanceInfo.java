package com.vtg.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import com.vtg.app.component.AdapterBalanceInfo;
import com.vtg.app.component.AdapterBanner;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelBalance;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelTag;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.PreferenceKey;
import com.vtg.app.util.CommonDefine.SoapTag;
import com.vtg.app.util.CommonDefine.WSCode;
import com.vtg.app.util.CommonDefine.mXML;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.SOAPUtil;
import com.vtg.app.util.XMLParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.w3c.dom.NodeList;

public class ActivityBalanceInfo extends FragmentActivity implements CommonDefine {
    private AdapterBalanceInfo adapterBalance;
    private AdapterBanner adapterBanner;
    private ImageView ivBack;
    private List<ModelBalance> listBalances;
    private ListView lvBalance;
    private Context mContext;
    private ViewPager pagerBanner;
    private SharedPreferences preferences;

    /* renamed from: com.vtg.app.ActivityBalanceInfo.1 */
    class C05341 implements Runnable {
        C05341() {
        }

        public void run() {
            int current = ActivityBalanceInfo.this.pagerBanner.getCurrentItem();
            if (current < 3) {
                current++;
            } else {
                current = 0;
            }
            ActivityBalanceInfo.this.pagerBanner.setCurrentItem(current);
            ActivityBalanceInfo.this.changeBanner();
        }
    }

    /* renamed from: com.vtg.app.ActivityBalanceInfo.2 */
    class C05352 implements OnClickListener {
        C05352() {
        }

        public void onClick(View v) {
            ActivityBalanceInfo.this.actionBack();
        }
    }

    private class AsyncTaskBalanceInfo extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;

        private AsyncTaskBalanceInfo() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            ActivityBalanceInfo.this.listBalances = new ArrayList();
            this.pDialog = new ProgressDialog(ActivityBalanceInfo.this.mContext);
            this.pDialog.setCancelable(false);
            this.pDialog.setMessage(ActivityBalanceInfo.this.mContext.getString(R.string.message_loading));
            this.pDialog.show();
        }

        protected String doInBackground(String... p) {
            List<ModelTag> tags = new ArrayList();
            tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
            tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
            tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
            List<ModelParam> params = new ArrayList();
            params.add(new ModelParam(mXML.ISDN, new StringBuilder(CommonDefine.NUMBER_HEADER).append(ActivityBalanceInfo.this.preferences.getString(PreferenceKey.PHONE_NUMBER, "")).toString()));
            params.add(new ModelParam("listId", "2001,2004,4200,4046"));
            SOAPUtil soap = new SOAPUtil(WSCode.GET_SUB_ACC, tags, params);
            soap.makeSOAPRequest();
            NodeList listId = soap.mDoc.getElementsByTagName("balanceId");
            NodeList listExpire = soap.mDoc.getElementsByTagName("balanceExpire");
            NodeList listName = soap.mDoc.getElementsByTagName("balanceName");
            NodeList listValue = soap.mDoc.getElementsByTagName("balanceValue");
            for (int i = 0; i < 10; i++) {
                if (!XMLParser.getElementValue(listName.item(i)).trim().equals("")) {
                    ActivityBalanceInfo.this.listBalances.add(new ModelBalance(XMLParser.getElementValue(listExpire.item(i)), XMLParser.getElementValue(listId.item(i)), XMLParser.getElementValue(listName.item(i)), FunctionHelper.formatMoney(XMLParser.getElementValue(listValue.item(i)))));
                }
            }
            return "";
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            this.pDialog.dismiss();
            ActivityBalanceInfo.this.adapterBalance = new AdapterBalanceInfo(ActivityBalanceInfo.this.mContext, ActivityBalanceInfo.this.listBalances);
            ActivityBalanceInfo.this.lvBalance.setAdapter(ActivityBalanceInfo.this.adapterBalance);
        }
    }

    protected void onCreate(@Nullable Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(1);
        setRequestedOrientation(7);
        this.mContext = this;
        this.preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
        this.listBalances = new ArrayList();
    }

    private void activityCreate() {
        setContentView(R.layout.activity_balance_info);
        initView();
        initBanner();
    }

    private void initBanner() {
        this.pagerBanner = (ViewPager) findViewById(R.id.pager_banner);
        this.adapterBanner = new AdapterBanner(getSupportFragmentManager());
        this.pagerBanner.setAdapter(this.adapterBanner);
        changeBanner();
    }

    private void changeBanner() {
        new Handler().postDelayed(new C05341(), 10000);
    }

    public void initView() {
        this.ivBack = (ImageView) findViewById(R.id.ivBack);
        this.ivBack.setOnClickListener(new C05352());
        this.lvBalance = (ListView) findViewById(R.id.lv_balance);
        new AsyncTaskBalanceInfo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[0]);
    }

    private void actionBack() {
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void setLanguage() {
        Locale locale = new Locale(this.preferences.getString(PreferenceKey.LOCATE, "en"));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        activityCreate();
    }

    protected void onStart() {
        super.onStart();
        setLanguage();
    }

    public void onBackPressed() {
        actionBack();
    }
}
