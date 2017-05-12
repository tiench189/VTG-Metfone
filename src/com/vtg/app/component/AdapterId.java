package com.vtg.app.component;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelId;
import java.util.List;

public class AdapterId extends BaseAdapter {
    private LayoutInflater inflater;
    private List<ModelId> listIds;

    private class Holder {
        TextView tvName;

        private Holder() {
        }
    }

    public AdapterId(Context context, List<ModelId> listIds) {
        this.listIds = listIds;
        this.inflater = ((Activity) context).getLayoutInflater();
    }

    public int getCount() {
        return this.listIds.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = this.inflater.inflate(R.layout.row_id, null);
            Holder holder = new Holder();
            holder.tvName = (TextView) view.findViewById(R.id.tv_name);
            view.setTag(holder);
        }
        ((Holder) view.getTag()).tvName.setText(((ModelId) this.listIds.get(position)).name);
        return view;
    }
}
