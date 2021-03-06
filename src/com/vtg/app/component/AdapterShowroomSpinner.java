package com.vtg.app.component;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelShowroom;
import java.util.List;

public class AdapterShowroomSpinner extends BaseAdapter {
    private LayoutInflater inflater;
    List<ModelShowroom> listShowroom;

    private class Holder {
        TextView tv;

        private Holder() {
        }
    }

    public AdapterShowroomSpinner(Context context, List<ModelShowroom> listShowroom) {
        this.listShowroom = listShowroom;
        this.inflater = ((Activity) context).getLayoutInflater();
    }

    public int getCount() {
        return this.listShowroom.size();
    }

    public ModelShowroom getItem(int position) {
        return (ModelShowroom) this.listShowroom.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = this.inflater.inflate(R.layout.row_basic_text, null);
            Holder holder = new Holder();
            holder.tv = (TextView) view.findViewById(R.id.tv);
            view.setTag(holder);
        }
        ((Holder) view.getTag()).tv.setText(getItem(position).name);
        return view;
    }
}
