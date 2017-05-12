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
import com.vtg.app.model.ModelInternet;
import com.vtg.app.model.ModelVasHistory;
import java.util.List;

public class AdapterInternet extends BaseAdapter {
	private LayoutInflater inflater;
	private List<ModelInternet> listInternet;

	class Holder {
		RelativeLayout layoutDivider;
		TextView tvData;
		TextView tvDate;
		TextView tvNo;
		TextView tvType;
		TextView tvAmount;

		Holder() {
		}
	}

	public AdapterInternet(Context context, List<ModelInternet> listInternet) {
		this.listInternet = listInternet;
		this.inflater = ((Activity) context).getLayoutInflater();
	}

	public int getCount() {
		return this.listInternet.size();
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
			view = this.inflater.inflate(R.layout.row_internet, null);
			holder = new Holder();
			holder.tvNo = (TextView) view.findViewById(R.id.tv_no);
			holder.tvDate = (TextView) view.findViewById(R.id.tv_date);
			holder.tvType = (TextView) view.findViewById(R.id.tv_type);
			holder.tvData = (TextView) view.findViewById(R.id.tv_data);
			holder.tvAmount = (TextView) view.findViewById(R.id.tv_amount);
			holder.layoutDivider = (RelativeLayout) view
					.findViewById(R.id.layout_divider);
			view.setTag(holder);
		}
		holder = (Holder) view.getTag();
		ModelInternet vas = (ModelInternet) this.listInternet.get(position);
		if (position == 0) {
			holder.tvNo.setText("No");
			holder.layoutDivider.setVisibility(0);
		} else {
			holder.tvNo.setText("" + position);
			holder.layoutDivider.setVisibility(8);
		}
		holder.tvDate.setText(vas.date);
		holder.tvType.setText(vas.type);
		holder.tvData.setText(vas.dataUsing);
		holder.tvAmount.setText(vas.amount);
		return view;
	}
}
