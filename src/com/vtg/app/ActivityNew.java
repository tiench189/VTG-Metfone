package com.vtg.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import com.vtg.app.component.AdapterNew;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelNew;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.MyService;
import com.vtg.app.util.JSONParser;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;

public class ActivityNew extends Activity implements CommonDefine {
    private List<ModelNew> listNews;
    private ListView lvNew;
    private Context mContext;
    private ProgressDialog pDialog;
    private SharedPreferences preferences;

    /* renamed from: com.vtg.app.ActivityNew.1 */
    class C05631 implements OnClickListener {
        C05631() {
        }

        public void onClick(View v) {
            ActivityNew.this.actionBack();
        }
    }

    private class AsyntaskNews extends AsyncTask<String, String, Boolean> {
        private AsyntaskNews() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            ActivityNew.this.pDialog.show();
            ActivityNew.this.listNews = new ArrayList();
        }

        protected Boolean doInBackground(String... arg0) {
            try {
            	String url = MyService.GET_NEW;
            	if (preferences.getString(PreferenceKey.LOCATE, "en").equals("vi")){
            		url += "?kh=true";
            	}
                JSONArray jsonArr = new JSONArray(new JSONParser().getJSONFromUrl(url, CommonDefine.METHOD_GET, null));
                for (int i = 0; i < jsonArr.length(); i++) {
                    ModelNew nw = new ModelNew();
                    nw.title = jsonArr.getJSONObject(i).getString("Title");
                    nw.description = jsonArr.getJSONObject(i).getString("Description");
                    nw.image = jsonArr.getJSONObject(i).getString("Image");
                    nw.link = jsonArr.getJSONObject(i).getString("Link");
                    nw.content = jsonArr.getJSONObject(i).getString("Details");
                    ActivityNew.this.listNews.add(nw);
                }
                return Boolean.valueOf(true);
            } catch (JSONException e) {
                e.printStackTrace();
                return Boolean.valueOf(false);
            }
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            ActivityNew.this.pDialog.dismiss();
            if (result.booleanValue()) {
                ActivityNew.this.lvNew.setAdapter(new AdapterNew(ActivityNew.this.mContext, ActivityNew.this.listNews));
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setRequestedOrientation(7);
        setContentView(R.layout.activity_new);
        this.mContext = this;
        preferences = getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
        this.pDialog = new ProgressDialog(this.mContext);
        this.pDialog.setCancelable(false);
        this.pDialog.setMessage(this.mContext.getString(R.string.message_loading));
        initView();
    }

    private void initView() {
        ((ImageView) findViewById(R.id.ivBack)).setOnClickListener(new C05631());
        this.lvNew = (ListView) findViewById(R.id.lv_new);
        new AsyntaskNews().execute(new String[0]);
    }

    private void actionBack() {
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void onBackPressed() {
        actionBack();
    }
}
