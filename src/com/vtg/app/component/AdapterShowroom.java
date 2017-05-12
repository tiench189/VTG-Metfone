package com.vtg.app.component;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vtg.app.ActivityMap;
import com.vtg.app.ActivityShowroom;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelShowroom;
import java.util.List;

public class AdapterShowroom extends BaseAdapter {
	private LayoutInflater inflater;
	private List<ModelShowroom> listShowroom;
	private Context mContext;

	/* renamed from: com.vtg.app.component.AdapterShowroom.1 */
	class C06011 implements OnClickListener {
		private final/* synthetic */ModelShowroom val$sr;

		C06011(ModelShowroom modelShowroom) {
			this.val$sr = modelShowroom;
		}

		public void onClick(View v) {
			ActivityShowroom.crShowroom = this.val$sr;
			AdapterShowroom.this.mContext.startActivity(new Intent(
					AdapterShowroom.this.mContext, ActivityMap.class));
			((Activity) AdapterShowroom.this.mContext)
					.overridePendingTransition(R.anim.slide_in_left,
							R.anim.slide_out_left);
		}
	}

	private class Holder {
		ImageView ivCover;
		TextView tvAddress;
		TextView tvMap;
		TextView tvName;
		TextView tvPhone;
		ImageView ivThumb;

		private Holder() {
		}
	}

	public AdapterShowroom(Context context, List<ModelShowroom> listShowroom) {
		this.mContext = context;
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
		Holder holder;
		View view = convertView;
		if (view == null) {
			view = this.inflater.inflate(R.layout.row_showroom, null);
			holder = new Holder();
			holder.tvName = (TextView) view.findViewById(R.id.tv_name);
			holder.tvPhone = (TextView) view.findViewById(R.id.tv_phone);
			holder.tvAddress = (TextView) view.findViewById(R.id.tv_address);
			holder.tvMap = (TextView) view.findViewById(R.id.tv_map);
			holder.ivThumb = (ImageView) view.findViewById(R.id.iv_thumb);
			view.setTag(holder);
		}
		holder = (Holder) view.getTag();
		ModelShowroom sr = getItem(position);
		holder.tvName.setText(sr.name);
		holder.tvPhone.setText(mContext.getString(R.string.tel) + ": "
				+ sr.phone);
		holder.tvAddress.setText(mContext.getString(R.string.address) + ": "
				+ sr.address);
		holder.tvMap.setOnClickListener(new C06011(sr));
		try {
			Glide.with(mContext).load(sr.image).into(holder.ivThumb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}
}
