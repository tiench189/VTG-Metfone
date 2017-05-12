package com.vtg.app.component;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.vtg.app.ActivityMain;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelMoreData;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelTag;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.Action;
import com.vtg.app.util.CommonDefine.PreferenceKey;
import com.vtg.app.util.CommonDefine.SoapTag;
import com.vtg.app.util.CommonDefine.WSCode;
import com.vtg.app.util.CommonDefine.mXML;
import com.vtg.app.util.FunctionHelper;
import com.vtg.app.util.SOAPUtil;
import java.util.ArrayList;
import java.util.List;

public class DialogMoreData extends Dialog implements CommonDefine {
    private AdapterModeData adapterMore;
    private RelativeLayout btnBuy;
    private RelativeLayout btnCancel;
    private ListView lvMore;
    private Context mContext;
    private SharedPreferences preferences;


    private class AsyncTaskBuyData extends AsyncTask<String, String, Boolean> {
        String message;
        ProgressDialog pDialog;

        private AsyncTaskBuyData() {
            this.message = "";
        }

        protected Boolean doInBackground(String... prs) {
            List<ModelTag> tags = new ArrayList();
            tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
            tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
            tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
            List<ModelParam> params = new ArrayList();
            params.add(new ModelParam("msisdn", new StringBuilder(CommonDefine.NUMBER_HEADER).append(DialogMoreData.this.preferences.getString(PreferenceKey.PHONE_NUMBER, "")).toString()));
            params.add(new ModelParam("send_sms", "1"));
            params.add(new ModelParam("requestId", FunctionHelper.formatCurrentTime()));
            params.add(new ModelParam("command", DialogMoreData.this.adapterMore.getItem(DialogMoreData.this.adapterMore.pick).code));
            SOAPUtil soap = new SOAPUtil(WSCode.BUY_DATA, tags, params);
            soap.makeSOAPRequest();
            if (soap.getError() != 0) {
                this.message = mContext.getString(R.string.message_fail);
                return Boolean.valueOf(false);
            } else if (Integer.parseInt(soap.getValue(mXML.ERR_CODE)) == 0) {
                this.message = soap.getValue(mXML.MESSAGE);
                return Boolean.valueOf(true);
            } else {
                this.message = soap.getValue(mXML.MESSAGE);
                return Boolean.valueOf(false);
            }
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog = new ProgressDialog(DialogMoreData.this.mContext);
            this.pDialog.setMessage(DialogMoreData.this.mContext.getString(R.string.message_loading));
            this.pDialog.setCancelable(false);
            this.pDialog.show();
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            this.pDialog.dismiss();
            if (result.booleanValue()) {
                Intent intent = new Intent();
                intent.setAction(CommonDefine.MY_PACKAGE);
                intent.putExtra(Action.ACTION, Action.RELOAD_DATA);
                DialogMoreData.this.mContext.sendBroadcast(intent);
            }
            new DialogReport(DialogMoreData.this.mContext, this.message).show();
            DialogMoreData.this.dismiss();
        }
    }

    public DialogMoreData(Context context, List<ModelMoreData> listMoreData) {
        super(context);
        requestWindowFeature(1);
        setContentView(R.layout.dialog_more_data);
        this.mContext = context;
        this.preferences = ((Activity) context).getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
        this.lvMore = (ListView) findViewById(R.id.lv_more_data);
        this.adapterMore = new AdapterModeData(this.mContext, listMoreData);
        this.lvMore.setAdapter(this.adapterMore);
        this.btnBuy = (RelativeLayout) findViewById(R.id.btn_buy);
        this.btnBuy.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (DialogMoreData.this.adapterMore.pick == -1) {
	                Toast.makeText(DialogMoreData.this.mContext, "Pick a package!", 1).show();
	            } else {
	                new AsyncTaskBuyData().execute(new String[0]);
	            }
			}
		});
        this.btnCancel = (RelativeLayout) findViewById(R.id.btn_cancel);
        this.btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
    }
}
