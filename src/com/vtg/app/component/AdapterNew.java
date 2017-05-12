package com.vtg.app.component;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vtg.app.ActivityWebview;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelNew;
import com.vtg.app.util.CommonDefine;

public class AdapterNew extends BaseAdapter implements CommonDefine {
	private LayoutInflater inflater;
	private List<ModelNew> listNews;
	private Context mContext;

	/* renamed from: com.vtg.app.component.AdapterNew.1 */
	class C06001 implements OnClickListener {
		private final/* synthetic */ModelNew val$nw;

		C06001(ModelNew modelNew) {
			this.val$nw = modelNew;
		}

		public void onClick(View v) {
			Intent iDetail = new Intent(AdapterNew.this.mContext,
					ActivityWebview.class);
			iDetail.putExtra("content", this.val$nw.content);
			iDetail.putExtra("title", this.val$nw.title);
			AdapterNew.this.mContext.startActivity(iDetail);
			((Activity) AdapterNew.this.mContext).overridePendingTransition(
					R.anim.slide_in_left, R.anim.slide_out_left);
		}
	}

	private class Holder {
		ImageView ivThumb;
		RelativeLayout layoutContainer;
		TextView tvDescription;
		TextView tvTitle;

		private Holder() {
		}
	}

	public AdapterNew(Context context, List<ModelNew> listNews) {
		this.mContext = context;
		this.listNews = listNews;
		this.inflater = ((Activity) context).getLayoutInflater();
	}

	public int getCount() {
		return this.listNews.size();
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
			view = this.inflater.inflate(R.layout.row_new, null);
			holder = new Holder();
			holder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
			holder.tvDescription = (TextView) view
					.findViewById(R.id.tv_description);
			holder.ivThumb = (ImageView) view.findViewById(R.id.iv_thumb);
			holder.layoutContainer = (RelativeLayout) view
					.findViewById(R.id.layout_container);
			view.setTag(holder);
		}
		holder = (Holder) view.getTag();
		ModelNew nw = (ModelNew) this.listNews.get(position);
		holder.tvTitle.setText(nw.title);
		holder.tvDescription.setText(Html.fromHtml(nw.description));
		try {
			Glide.with(this.mContext)
					.load(new StringBuilder(MyService.WEB_DOMAIN).append(
							nw.image).toString()).into(holder.ivThumb);
		} catch (Exception e) {
		}
		holder.layoutContainer.setOnClickListener(new C06001(nw));
		return view;
	}
}
