package com.vtg.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.vtg.app.metfone.R;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.PreferenceKey;
import java.util.Locale;

public class ActivitySetting extends Activity implements CommonDefine {
	private ImageView ivBack;
	private ImageView ivEN;
	private ImageView ivVI;
	private RelativeLayout layoutEN;
	private RelativeLayout layoutVI;
	private Context mContext;
	private SharedPreferences preferences;

	/* renamed from: com.vtg.app.ActivitySetting.1 */
	class C05891 implements OnClickListener {
		C05891() {
		}

		public void onClick(View v) {
			actionBack();
		}
	}

	/* renamed from: com.vtg.app.ActivitySetting.2 */
	class C05902 implements OnClickListener {
		C05902() {
		}

		public void onClick(View v) {
			if (!preferences.getString(PreferenceKey.LOCATE, "en").equals("en")) {
				preferences.edit().putString(PreferenceKey.LOCATE, "en")
						.commit();
				ivEN.setVisibility(0);
				ivVI.setVisibility(4);
				setLanguage();
			}
		}
	}

	/* renamed from: com.vtg.app.ActivitySetting.3 */
	class C05913 implements OnClickListener {
		C05913() {
		}

		public void onClick(View v) {
			if (!preferences.getString(PreferenceKey.LOCATE, "en").equals("vi")) {
				preferences.edit().putString(PreferenceKey.LOCATE, "vi")
						.commit();
				ivEN.setVisibility(4);
				ivVI.setVisibility(0);
				setLanguage();
			}
		}
	}

	protected void onCreate(@Nullable Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(1);
		setRequestedOrientation(7);
		mContext = this;
		preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
	}

	private void activityCreate() {
		setContentView(R.layout.activity_setting);
		initView();
	}

	public void initView() {
		ivBack = (ImageView) findViewById(R.id.ivBack);
		ivBack.setOnClickListener(new C05891());
		layoutEN = (RelativeLayout) findViewById(R.id.layout_en);
		layoutVI = (RelativeLayout) findViewById(R.id.layout_vi);
		ivEN = (ImageView) findViewById(R.id.iv_en);
		ivVI = (ImageView) findViewById(R.id.iv_vi);
		if (preferences.getString(PreferenceKey.LOCATE, "en").equals("en")) {
			ivEN.setVisibility(0);
			ivVI.setVisibility(4);
		} else {
			ivEN.setVisibility(4);
			ivVI.setVisibility(0);
		}
		layoutEN.setOnClickListener(new C05902());
		layoutVI.setOnClickListener(new C05913());
	}

	private void actionBack() {
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

	public void setLanguage() {
		Locale locale = new Locale(preferences.getString(
				PreferenceKey.LOCATE, "en"));
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
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
