package com.vtg.app.component;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelMoreData;
import java.util.List;

public class AdapterModeData extends BaseAdapter {
    private LayoutInflater inflater;
    private List<ModelMoreData> listMore;
    public int pick;

    /* renamed from: com.vtg.app.component.AdapterModeData.1 */
    class C05991 implements OnClickListener {
        private final /* synthetic */ int val$position;

        C05991(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            if (AdapterModeData.this.pick != this.val$position) {
                AdapterModeData.this.pick = this.val$position;
                AdapterModeData.this.notifyDataSetChanged();
            }
        }
    }

    private class Holder {
        RelativeLayout layoutMore;
        RadioButton rbActive;
        TextView tvMore;

        private Holder() {
        }
    }

    public AdapterModeData(Context context, List<ModelMoreData> listMore) {
        this.pick = -1;
        this.listMore = listMore;
        this.inflater = ((Activity) context).getLayoutInflater();
        this.pick = -1;
    }

    public int getCount() {
        return this.listMore.size();
    }

    public ModelMoreData getItem(int position) {
        return (ModelMoreData) this.listMore.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        View view = convertView;
        if (view == null) {
            view = this.inflater.inflate(R.layout.row_more_data, null);
            holder = new Holder();
            holder.tvMore = (TextView) view.findViewById(R.id.tv_more_data);
            holder.rbActive = (RadioButton) view.findViewById(R.id.rb_active);
            holder.layoutMore = (RelativeLayout) view.findViewById(R.id.layout_more);
            view.setTag(holder);
        }
        ModelMoreData more = getItem(position);
        holder = (Holder) view.getTag();
        holder.tvMore.setText(more.value + "MB ($" + more.price + ")");
        holder.rbActive.setChecked(this.pick == position);
        holder.layoutMore.setOnClickListener(new C05991(position));
        return view;
    }
}
