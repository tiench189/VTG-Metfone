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
import com.vtg.app.model.ModelRefill;
import java.util.List;

public class AdapterRefill extends BaseAdapter {
	private LayoutInflater inflater;
	private List<ModelRefill> listRefill;

	class Holder {
		RelativeLayout layoutDivider;
		TextView tvAmount;
		TextView tvCode;
		TextView tvDate;
		TextView tvNo;
		TextView tvType;

		Holder() {
		}
	}

	public AdapterRefill(Context context, List<ModelRefill> listRefill) {
		this.listRefill = listRefill;
		inflater = ((Activity) context).getLayoutInflater();
	}

	public int getCount() {
		return listRefill.size();
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
			view = inflater.inflate(R.layout.row_refill, null);
			Holder holder = new Holder();
			holder.tvNo = (TextView) view.findViewById(R.id.tv_no_refill);
			holder.tvDate = (TextView) view.findViewById(R.id.tv_date);
			holder.tvType = (TextView) view.findViewById(R.id.tv_type);
			holder.tvCode = (TextView) view.findViewById(R.id.tv_code);
			holder.tvAmount = (TextView) view.findViewById(R.id.tv_amount);
			holder.layoutDivider = (RelativeLayout) view
					.findViewById(R.id.layout_divider);
			view.setTag(holder);
		}
		Holder holder = (Holder) view.getTag();
		ModelRefill refill = (ModelRefill) listRefill.get(position);
		if (position == 0) {
			holder.tvNo.setText("No");
			holder.layoutDivider.setVisibility(0);
		} else {
			holder.tvNo.setText("" + position);
			holder.layoutDivider.setVisibility(8);
		}
		holder.tvDate.setText(refill.date);
		holder.tvType.setText(refill.type);
		holder.tvCode.setText(refill.code);
		holder.tvAmount.setText(refill.amount);
		return view;
	}
}
