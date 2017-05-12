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
import com.vtg.app.model.ModelCharge;
import java.util.List;

public class AdapterCharge extends BaseAdapter {
	private LayoutInflater inflater;
	private List<ModelCharge> listCharge;

	class Holder {
		RelativeLayout layoutDivider;
		TextView tvAmount;
		TextView tvDate;
		TextView tvDuration;
		TextView tvNo;
		TextView tvType;
		TextView tvNumber;

		Holder() {
		}
	}

	public AdapterCharge(Context context, List<ModelCharge> listCharge) {
		this.listCharge = listCharge;
		this.inflater = ((Activity) context).getLayoutInflater();
	}

	public int getCount() {
		return this.listCharge.size();
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
			view = this.inflater.inflate(R.layout.row_charge, null);
			holder = new Holder();
			holder.tvNo = (TextView) view.findViewById(R.id.tv_no);
			holder.tvDate = (TextView) view.findViewById(R.id.tv_date);
			holder.tvType = (TextView) view.findViewById(R.id.tv_type);
			holder.tvDuration = (TextView) view.findViewById(R.id.tv_duration);
			holder.tvAmount = (TextView) view.findViewById(R.id.tv_amount);
			holder.tvNumber = (TextView) view.findViewById(R.id.tv_number);
			holder.layoutDivider = (RelativeLayout) view
					.findViewById(R.id.layout_divider);
			view.setTag(holder);
		}
		holder = (Holder) view.getTag();
		ModelCharge charge = (ModelCharge) this.listCharge.get(position);
		if (position == 0) {
			holder.tvNo.setText("No");
			holder.layoutDivider.setVisibility(0);
		} else {
			holder.tvNo.setText("" + position);
			holder.layoutDivider.setVisibility(8);
		}
		holder.tvDate.setText(charge.date);
		holder.tvType.setText(charge.type);
		holder.tvDuration.setText(charge.duration);
		holder.tvAmount.setText(charge.amount);
		holder.tvNumber.setText(charge.number);
		return view;
	}
}
