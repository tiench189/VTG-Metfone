package com.vtg.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.vtg.app.metfone.R;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.PreferenceKey;

public class ActivitySplash extends Activity implements CommonDefine {
    private Context mContext;
    private SharedPreferences preferences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setRequestedOrientation(7);
        setContentView(R.layout.activity_splash);
        this.mContext = this;
        this.preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
        if (this.preferences.getString(PreferenceKey.PHONE_NUMBER, "") == "") {
            startActivity(new Intent(this.mContext, ActivityFirst.class));
            finish();
            return;
        }
        startActivity(new Intent(this.mContext, ActivityMain.class));
        finish();
    }
}
