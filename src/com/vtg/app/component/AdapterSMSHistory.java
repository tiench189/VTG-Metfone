package com.vtg.app.component;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelSMSHistory;
import java.util.List;

public class AdapterSMSHistory extends BaseAdapter {
    private LayoutInflater inflater;
    private List<ModelSMSHistory> listSMS;

    class Holder {
        RelativeLayout layoutDivider;
        TextView tvCost;
        TextView tvDate;
        TextView tvNo;
        TextView tvNumber;

        Holder() {
        }
    }

    public AdapterSMSHistory(Context context, List<ModelSMSHistory> listSMS) {
        this.listSMS = listSMS;
        this.inflater = ((Activity) context).getLayoutInflater();
    }

    public int getCount() {
        return this.listSMS.size();
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
            view = this.inflater.inflate(R.layout.row_sms_history, null);
            holder = new Holder();
            holder.tvNo = (TextView) view.findViewById(R.id.tv_no);
            holder.tvDate = (TextView) view.findViewById(R.id.tv_date);
            holder.tvNumber = (TextView) view.findViewById(R.id.tv_number);
            holder.tvCost = (TextView) view.findViewById(R.id.tv_cost);
            holder.layoutDivider = (RelativeLayout) view.findViewById(R.id.layout_divider);
            view.setTag(holder);
        }
        holder = (Holder) view.getTag();
        ModelSMSHistory sms = (ModelSMSHistory) this.listSMS.get(position);
        if (position == 0) {
            holder.tvNo.setText("No");
            holder.layoutDivider.setVisibility(0);
        } else {
            holder.tvNo.setText("" + position);
            holder.layoutDivider.setVisibility(8);
        }
        holder.tvDate.setText(sms.date);
        holder.tvCost.setText(sms.cost);
        holder.tvNumber.setText(sms.number);
        return view;
    }
}
