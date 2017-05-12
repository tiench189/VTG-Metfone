package com.vtg.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.vtg.app.component.AdapterNew;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelNew;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.MyService;
import com.vtg.app.util.CommonDefine.PreferenceKey;
import com.vtg.app.util.JSONParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

public class ActivityWithoutLogin extends FragmentActivity {
	public static SlidingMenu menu;
	private ImageView ivMenu;
	private List<ModelNew> listNews;
	private ListView lvNew;
	private Context mContext;
	private ProgressDialog pDialog;

	/* renamed from: com.vtg.app.ActivityWithoutLogin.1 */
	class C05971 implements OnClickListener {
		C05971() {
		}

		public void onClick(View v) {
			if (ActivityWithoutLogin.menu.isMenuShowing()) {
				ActivityWithoutLogin.menu.showContent();
			} else {
				ActivityWithoutLogin.menu.showMenu();
			}
		}
	}

	private class AsyntaskNews extends AsyncTask<String, String, Boolean> {
		private AsyntaskNews() {
		}

		protected void onPreExecute() {
			super.onPreExecute();
			ActivityWithoutLogin.this.pDialog.show();
			ActivityWithoutLogin.this.listNews = new ArrayList();
		}

		protected Boolean doInBackground(String... arg0) {
			try {
				JSONArray jsonArr = new JSONArray(
						new JSONParser().getJSONFromUrl(MyService.GET_NEW,
								CommonDefine.METHOD_GET, null));
				for (int i = 0; i < jsonArr.length(); i++) {
					ModelNew nw = new ModelNew();
					nw.title = jsonArr.getJSONObject(i).getString("Title");
					nw.description = jsonArr.getJSONObject(i).getString(
							"Description");
					nw.image = jsonArr.getJSONObject(i).getString("Image");
					nw.link = jsonArr.getJSONObject(i).getString("Link");
					nw.content = jsonArr.getJSONObject(i).getString("Details");
					ActivityWithoutLogin.this.listNews.add(nw);
				}
				return Boolean.valueOf(true);
			} catch (JSONException e) {
				e.printStackTrace();
				return Boolean.valueOf(false);
			}
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			ActivityWithoutLogin.this.pDialog.dismiss();
			if (result.booleanValue()) {
				ActivityWithoutLogin.this.lvNew.setAdapter(new AdapterNew(
						ActivityWithoutLogin.this.mContext,
						ActivityWithoutLogin.this.listNews));
			}
		}
	}

	public void setLanguage() {
		Locale locale = new Locale(preferences.getString(PreferenceKey.LOCATE,
				"en"));
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
	}

	private SharedPreferences preferences;

	protected void onCreate(@Nullable Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(1);
		setRequestedOrientation(7);
		preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
		setLanguage();
		setContentView(R.layout.activity_without_login);
		this.mContext = this;
		this.pDialog = new ProgressDialog(this.mContext);
		this.pDialog.setCancelable(false);
		this.pDialog.setMessage(this.mContext
				.getString(R.string.message_loading));
		initView();
		initMenu();
	}

	private void initView() {
		this.lvNew = (ListView) findViewById(R.id.lv_new);
		new AsyntaskNews().execute(new String[0]);
	}

	private void initMenu() {
		this.ivMenu = (ImageView) findViewById(R.id.ivMenu);
		if (getIntent().getExtras() != null) {
			this.ivMenu.setVisibility(8);
		}
		this.ivMenu.setOnClickListener(new C05971());
		menu = new SlidingMenu(this);
		menu.setTouchModeAbove(0);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable((int) R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, 1);
		menu.setMenu((int) R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new FragmentMenuWithoutLogin())
				.commit();
	}
}
