package com.vtg.app;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.vtg.app.metfone.R;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.PreferenceKey;

public class ActivityWebview extends Activity implements CommonDefine {
	private Context mContext;
	private WebView wv;
	private TextView tvTitle;

	/* renamed from: com.vtg.app.ActivityWebview.1 */
	class C05961 implements OnClickListener {
		C05961() {
		}

		public void onClick(View v) {
			ActivityWebview.this.actionBack();
		}
	}

	private SharedPreferences preferences;

	public void setLanguage() {
		Locale locale = new Locale(preferences.getString(PreferenceKey.LOCATE,
				"en"));
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(1);
		preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
		mContext = this;
		setLanguage();
		setContentView(R.layout.activity_webview);
		this.mContext = this;
		initView();
	}

	private void initView() {
		((ImageView) findViewById(R.id.ivBack))
				.setOnClickListener(new C05961());
		this.wv = (WebView) findViewById(R.id.wv);
		// this.wv.loadUrl(getIntent().getExtras().getString(PlusShare.KEY_CALL_TO_ACTION_URL));
		wv.loadDataWithBaseURL("",
				getIntent().getExtras().getString("content"), "text/html",
				"UTF-8", "");
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(getIntent().getExtras().getString("title"));
	}

	private void actionBack() {
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

	public void onBackPressed() {
		actionBack();
	}
}
