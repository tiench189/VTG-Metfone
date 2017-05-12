package com.vtg.app.component;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.vtg.app.metfone.R;

public class AdapterIndicator extends BaseAdapter {
    private int count;
    private LayoutInflater inflater;
    private int pos;

    private class Holder {
        ImageView ivIndicator;

        private Holder() {
        }
    }

    public AdapterIndicator(Context context, int count) {
        this.pos = 0;
        this.count = count;
        this.inflater = ((Activity) context).getLayoutInflater();
    }

    public int getCount() {
        return this.count;
    }

    public Object getItem(int position) {
        return null;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        View view = convertView;
        if (view == null) {
            view = this.inflater.inflate(R.layout.row_indicator, null);
            holder = new Holder();
            holder.ivIndicator = (ImageView) view.findViewById(R.id.iv_indicator);
            view.setTag(holder);
        }
        holder = (Holder) view.getTag();
        if (position == this.pos) {
            holder.ivIndicator.setImageResource(R.drawable.highlight_circle);
        } else {
            holder.ivIndicator.setImageResource(R.drawable.normal_circle);
        }
        return view;
    }
}
