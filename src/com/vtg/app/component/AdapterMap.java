package com.vtg.app.component;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelMap;
import java.util.List;

public class AdapterMap extends BaseAdapter {
    private LayoutInflater inflater;
    private List<ModelMap> listMap;

    private class Holder {
        TextView tvKey;
        TextView tvValue;

        private Holder() {
        }
    }

    public AdapterMap(Context context, List<ModelMap> listMap) {
        this.listMap = listMap;
        this.inflater = ((Activity) context).getLayoutInflater();
    }

    public int getCount() {
        return this.listMap.size();
    }

    public ModelMap getItem(int position) {
        return (ModelMap) this.listMap.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        View view = convertView;
        if (view == null) {
            view = this.inflater.inflate(R.layout.row_profile, null);
            holder = new Holder();
            holder.tvKey = (TextView) view.findViewById(R.id.tv_key);
            holder.tvValue = (TextView) view.findViewById(R.id.tv_value);
            view.setTag(holder);
        }
        holder = (Holder) view.getTag();
        ModelMap map = getItem(position);
        holder.tvKey.setText(map.key);
        holder.tvValue.setText(map.value);
        return view;
    }
}
