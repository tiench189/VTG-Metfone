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
import com.vtg.app.model.ModelVasHistory;
import java.util.List;

public class AdapterVasHistory extends BaseAdapter {
	private LayoutInflater inflater;
	private List<ModelVasHistory> listVas;

	class Holder {
		RelativeLayout layoutDivider;
		TextView tvAmount;
		TextView tvDate;
		TextView tvNo;
		TextView tvService;
		TextView tvType;

		Holder() {
		}
	}

	public AdapterVasHistory(Context context, List<ModelVasHistory> listVas) {
		this.listVas = listVas;
		this.inflater = ((Activity) context).getLayoutInflater();
	}

	public int getCount() {
		return this.listVas.size();
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
			view = this.inflater.inflate(R.layout.row_vas_history, null);
			holder = new Holder();
			holder.tvNo = (TextView) view.findViewById(R.id.tv_no);
			holder.tvDate = (TextView) view.findViewById(R.id.tv_date);
			holder.tvType = (TextView) view.findViewById(R.id.tv_type);
			holder.tvService = (TextView) view.findViewById(R.id.tv_service);
			holder.tvAmount = (TextView) view.findViewById(R.id.tv_amount);
			holder.layoutDivider = (RelativeLayout) view
					.findViewById(R.id.layout_divider);
			view.setTag(holder);
		}
		holder = (Holder) view.getTag();
		ModelVasHistory vas = (ModelVasHistory) this.listVas.get(position);
		if (position == 0) {
			holder.tvNo.setText("No");
			holder.layoutDivider.setVisibility(0);
		} else {
			holder.tvNo.setText("" + position);
			holder.layoutDivider.setVisibility(8);
		}
		holder.tvDate.setText(vas.date);
		holder.tvType.setText(vas.type);
		holder.tvService.setText(vas.service);
		holder.tvAmount.setText(vas.amount);
		return view;
	}
}
