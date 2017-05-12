package com.vtg.app.component;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vtg.app.ActivityBalanceInfo;
import com.vtg.app.ActivityBasic3G;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelBalance;
import com.vtg.app.util.FunctionHelper;
import java.util.List;

public class AdapterBalanceInfo extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<ModelBalance> listBalances;

	private class Holder {
		RelativeLayout layoutBalance;
		TextView tvExpire;
		TextView tvName;
		TextView tvValue;

		private Holder() {
		}
	}

	public AdapterBalanceInfo(Context context, List<ModelBalance> listBalances) {
		this.listBalances = listBalances;
		this.inflater = ((Activity) context).getLayoutInflater();
		this.context = context;
	}

	public int getCount() {
		return this.listBalances.size();
	}

	public ModelBalance getItem(int position) {
		return (ModelBalance) this.listBalances.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		View view = convertView;
		if (view == null) {
			view = this.inflater.inflate(R.layout.row_balance_info, null);
			holder = new Holder();
			holder.layoutBalance = (RelativeLayout) view
					.findViewById(R.id.layout_balance);
			holder.tvName = (TextView) view.findViewById(R.id.tv_balance_name);
			holder.tvValue = (TextView) view
					.findViewById(R.id.tv_balance_value);
			holder.tvExpire = (TextView) view
					.findViewById(R.id.tv_balance_expire);
			view.setTag(holder);
		}
		ModelBalance balance = getItem(position);
		holder = (Holder) view.getTag();
		holder.tvName.setText(FunctionHelper.convertBalanceName(this.context,
				Integer.parseInt(balance.id)));
		holder.tvValue.setText(balance.value
				+ " "
				+ FunctionHelper.convertBalanceUnit(this.context,
						Integer.parseInt(balance.id)));
		holder.tvExpire.setText(FunctionHelper.formatDateTime(balance.expire));
		return view;
	}
}
