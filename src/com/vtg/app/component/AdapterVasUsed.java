package com.vtg.app.component;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.vtg.app.ActivityMain;
import com.vtg.app.ActivityVasUnused;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelTag;
import com.vtg.app.model.ModelVas;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.Action;
import com.vtg.app.util.CommonDefine.PreferenceKey;
import com.vtg.app.util.CommonDefine.SoapTag;
import com.vtg.app.util.CommonDefine.WSCode;
import com.vtg.app.util.CommonDefine.mXML;
import com.vtg.app.util.SOAPUtil;
import java.util.ArrayList;
import java.util.List;

public class AdapterVasUsed extends BaseAdapter implements CommonDefine {
    private LayoutInflater inflater;
    private List<ModelVas> listVas;
    private Context mContext;
    private SharedPreferences preferences;

    /* renamed from: com.vtg.app.component.AdapterVasUsed.1 */
    class C06171 implements OnClickListener {
        private final /* synthetic */ ModelVas val$vas;

        /* renamed from: com.vtg.app.component.AdapterVasUsed.1.1 */
        class C06161 implements OnClickListener {
            private final /* synthetic */ DialogConfirm val$cfDialog;
            private final /* synthetic */ ModelVas val$vas;

            C06161(DialogConfirm dialogConfirm, ModelVas modelVas) {
                this.val$cfDialog = dialogConfirm;
                this.val$vas = modelVas;
            }

            public void onClick(View v) {
                this.val$cfDialog.dismiss();
                new AsyncTaskCancel().execute(new ModelVas[]{this.val$vas});
            }
        }

        C06171(ModelVas modelVas) {
            this.val$vas = modelVas;
        }

        public void onClick(View v) {
            DialogConfirm cfDialog = new DialogConfirm(AdapterVasUsed.this.mContext, mContext.getString(R.string.cf_unregis) + " " + this.val$vas.name + "?");
            cfDialog.btnYes.setOnClickListener(new C06161(cfDialog, this.val$vas));
            cfDialog.show();
        }
    }

    /* renamed from: com.vtg.app.component.AdapterVasUsed.2 */
    class C06182 implements OnClickListener {
        C06182() {
        }

        public void onClick(View v) {
            AdapterVasUsed.this.mContext.startActivity(new Intent(AdapterVasUsed.this.mContext, ActivityVasUnused.class));
            ((Activity) AdapterVasUsed.this.mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        }
    }

    private class AsyncTaskCancel extends AsyncTask<ModelVas, String, Boolean> {
        String message;
        ProgressDialog pDialog;

        private AsyncTaskCancel() {
            this.message = mContext.getString(R.string.err_connect);
        }

        protected Boolean doInBackground(ModelVas... prs) {
            try {
                ModelVas vas = prs[0];
                List<ModelTag> tags = new ArrayList();
                tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
                tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
                tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
                List<ModelParam> params = new ArrayList();
                params.add(new ModelParam(mXML.ISDN, AdapterVasUsed.this.preferences.getString(PreferenceKey.PHONE_NUMBER, "")));
                params.add(new ModelParam("receiverIsdn", vas.receiverIsdn));
                params.add(new ModelParam("command", vas.remove));
                SOAPUtil soap = new SOAPUtil(WSCode.REGIS_VAS_FAKE_MO, tags, params);
                soap.makeSOAPRequest();
                if (soap.getError() != 0) {
                    this.message = mContext.getString(R.string.message_fail);
                    return Boolean.valueOf(false);
                } else if (Integer.parseInt(soap.getValue(mXML.CODE)) == 1) {
                    this.message = mContext.getString(R.string.message_success);
                    return Boolean.valueOf(true);
                } else {
                    this.message = soap.getValue(mXML.MESSAGE);
                    return Boolean.valueOf(false);
                }
            } catch (Exception e) {
                return Boolean.valueOf(false);
            }
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog = new ProgressDialog(AdapterVasUsed.this.mContext);
            this.pDialog.setMessage(AdapterVasUsed.this.mContext.getString(R.string.message_loading));
            this.pDialog.setCancelable(false);
            this.pDialog.show();
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            this.pDialog.dismiss();
            if (result.booleanValue()) {
                Intent intent = new Intent();
                intent.setAction(CommonDefine.MY_PACKAGE);
                intent.putExtra(Action.ACTION, Action.RELOAD_VAS);
                AdapterVasUsed.this.mContext.sendBroadcast(intent);
            }
            new DialogReport(AdapterVasUsed.this.mContext, this.message).show();
        }
    }

    private class Holder {
        RelativeLayout btnCancel;
        RelativeLayout layoutAction;
        LinearLayout layoutContent;
        TextView tvName;
        TextView tvPrice;
        TextView tvTime;

        private Holder() {
        }
    }

    public AdapterVasUsed(Context context, List<ModelVas> listVas) {
        this.listVas = listVas;
        this.mContext = context;
        this.inflater = ((Activity) context).getLayoutInflater();
        this.preferences = ((Activity) context).getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
    }

    public int getCount() {
        return this.listVas.size() + 1;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        View view = convertView;
        if (view == null) {
            view = this.inflater.inflate(R.layout.row_vas_used, null);
            holder = new Holder();
            holder.tvName = (TextView) view.findViewById(R.id.tv_name);
            holder.tvPrice = (TextView) view.findViewById(R.id.tv_price);
            holder.tvTime = (TextView) view.findViewById(R.id.tv_time);
            holder.btnCancel = (RelativeLayout) view.findViewById(R.id.btn_cancel);
            holder.layoutContent = (LinearLayout) view.findViewById(R.id.layout_content);
            holder.layoutAction = (RelativeLayout) view.findViewById(R.id.layout_action);
            view.setTag(holder);
        }
        holder = (Holder) view.getTag();
        if (position < this.listVas.size()) {
            holder.layoutAction.setVisibility(8);
            holder.layoutContent.setVisibility(0);
            ModelVas vas = (ModelVas) this.listVas.get(position);
            holder.tvName.setText(vas.name);
            holder.tvPrice.setText("Fee: " + vas.fee);
            holder.tvTime.setText("Register date: " + vas.register_date);
            holder.btnCancel.setOnClickListener(new C06171(vas));
        } else {
            holder.layoutAction.setVisibility(0);
            holder.layoutContent.setVisibility(8);
            holder.layoutAction.setOnClickListener(new C06182());
        }
        return view;
    }
}
