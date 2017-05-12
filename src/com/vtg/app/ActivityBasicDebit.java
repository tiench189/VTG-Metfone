package com.vtg.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.vtg.app.component.AdapterBanner;
import com.vtg.app.component.DialogConfirm;
import com.vtg.app.component.DialogTranfer;
import com.vtg.app.component.DialogUpPay;
import com.vtg.app.metfone.R;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.Action;
import com.vtg.app.util.CommonDefine.PreferenceKey;
import java.util.Locale;

public class ActivityBasicDebit extends FragmentActivity implements CommonDefine, OnClickListener {
    private static final String TAG = "ActivityBasicDebit";
    private AdapterBanner adapterBanner;
    private RelativeLayout btnPay199;
    private RelativeLayout btnTranfer;
    Configuration config;
    private String locate;
    private Context mContext;
    private ViewPager pagerBanner;
    private SharedPreferences preferences;
    private BasicAccountReceiver receiver;
    private TextView tvCharge;
    private TextView tvCredit;
    private TextView tvDebit;
    private TextView tvPayment;

    /* renamed from: com.vtg.app.ActivityBasicDebit.1 */
    class C05411 implements Runnable {
        C05411() {
        }

        public void run() {
            int current = ActivityBasicDebit.this.pagerBanner.getCurrentItem();
            if (current < 3) {
                current++;
            } else {
                current = 0;
            }
            ActivityBasicDebit.this.pagerBanner.setCurrentItem(current);
            ActivityBasicDebit.this.changeBanner();
        }
    }

    /* renamed from: com.vtg.app.ActivityBasicDebit.2 */
    class C05422 implements OnClickListener {
        private final /* synthetic */ DialogConfirm val$cfUpPay;

        C05422(DialogConfirm dialogConfirm) {
            this.val$cfUpPay = dialogConfirm;
        }

        public void onClick(View v) {
            this.val$cfUpPay.dismiss();
            ActivityBasicDebit.this.showDialogUpPay(true);
        }
    }

    /* renamed from: com.vtg.app.ActivityBasicDebit.3 */
    class C05433 implements OnClickListener {
        private final /* synthetic */ DialogConfirm val$cfUpPay;

        C05433(DialogConfirm dialogConfirm) {
            this.val$cfUpPay = dialogConfirm;
        }

        public void onClick(View v) {
            this.val$cfUpPay.dismiss();
            ActivityBasicDebit.this.showDialogUpPay(false);
        }
    }

    private class BasicAccountReceiver extends BroadcastReceiver {
        private BasicAccountReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras().getString(Action.ACTION).equals(Action.DONE_RELOAD_ACCOUNT)) {
                ActivityBasicDebit.this.setBasicAccount();
            }
        }
    }

    public ActivityBasicDebit() {
        this.locate = "";
    }

    protected void onCreate(@Nullable Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(1);
        setRequestedOrientation(7);
        this.mContext = this;
        this.preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
        this.receiver = new BasicAccountReceiver();
        registerReceiver(this.receiver, new IntentFilter(CommonDefine.MY_PACKAGE));
        Log.v(TAG, "tiench onCreated");
    }

    private void initBanner() {
        this.pagerBanner = (ViewPager) findViewById(R.id.pager_banner);
        this.adapterBanner = new AdapterBanner(getSupportFragmentManager());
        this.pagerBanner.setAdapter(this.adapterBanner);
        changeBanner();
    }

    private void changeBanner() {
        new Handler().postDelayed(new C05411(), 10000);
    }

    private void activityCreate() {
        setContentView(R.layout.activity_basic_debit);
        initView();
        initBanner();
    }

    private void initView() {
        ((ImageView) findViewById(R.id.ivBack)).setOnClickListener(this);
        this.tvCharge = (TextView) findViewById(R.id.tv_charge);
        this.tvDebit = (TextView) findViewById(R.id.tv_debit);
        this.tvPayment = (TextView) findViewById(R.id.tv_payment);
        this.tvCredit = (TextView) findViewById(R.id.tv_credit);
        this.btnTranfer = (RelativeLayout) findViewById(R.id.btn_tranfer);
        this.btnTranfer.setOnClickListener(this);
        this.btnPay199 = (RelativeLayout) findViewById(R.id.btn_pay_199);
        this.btnPay199.setOnClickListener(this);
        setBasicAccount();
    }

    private void setBasicAccount() {
        this.tvCharge.setText(new StringBuilder(String.valueOf(ActivityMain.debitAcc.charge)).append(" USD").toString());
        this.tvDebit.setText(new StringBuilder(String.valueOf(ActivityMain.debitAcc.debitStart)).append(" USD").toString());
        this.tvPayment.setText(new StringBuilder(String.valueOf(ActivityMain.debitAcc.payment)).append(" USD").toString());
        this.tvCredit.setText(new StringBuilder(String.valueOf(ActivityMain.debitAcc.credit)).append(" USD").toString());
    }

    private void actionBack() {
        try {
            unregisterReceiver(this.receiver);
        } catch (Exception e) {
        }
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void onBackPressed() {
        actionBack();
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
        if (!this.preferences.getString(PreferenceKey.LOCATE, "en").equals(this.locate)) {
            this.locate = this.preferences.getString(PreferenceKey.LOCATE, "en");
            setLanguage();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                actionBack();
                break;
            case R.id.btn_tranfer:
                showDialogTranfer();
                break;
            case R.id.btn_pay_199:
                showConfirmUpPay();
                break;
            default:
        }
    }

    private void showDialogTranfer() {
        new DialogTranfer(this.mContext).show();
    }

    private void showConfirmUpPay() {
        DialogConfirm cfUpPay = new DialogConfirm(this.mContext, getString(R.string.confirm_up_pay));
        cfUpPay.tvYes.setText(getString(R.string.you));
        cfUpPay.tvNo.setText(getString(R.string.anyone));
        cfUpPay.btnYes.setOnClickListener(new C05422(cfUpPay));
        cfUpPay.btnNo.setOnClickListener(new C05433(cfUpPay));
        cfUpPay.show();
    }

    private void showDialogUpPay(boolean isMe) {
        DialogUpPay dlUpPay = new DialogUpPay(this.mContext, true);
        if (isMe) {
            dlUpPay.edtNumber.setText(this.preferences.getString(PreferenceKey.PHONE_NUMBER, ""));
            dlUpPay.edtNumber.setVisibility(8);
        }
        dlUpPay.show();
    }
}
