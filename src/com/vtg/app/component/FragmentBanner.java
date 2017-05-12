package com.vtg.app.component;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vtg.app.ActivityMain;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelBanner;

public class FragmentBanner extends Fragment {

	private ImageView ivBanner;
	private int page;
	private ModelBanner banner;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		page = getArguments().getInt("page");
		banner = ActivityMain.listBanner.get(page);
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_banner, null);
		ivBanner = (ImageView) view.findViewById(R.id.iv_banner);
		try {
			Glide.with(getActivity()).load(banner.image).into(ivBanner);
		} catch (Exception e) {

		}
		ivBanner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					final Intent intent = new Intent(Intent.ACTION_VIEW)
							.setData(Uri.parse(banner.action));
					getActivity().startActivity(intent);
				} catch (Exception e) {

				}
			}
		});
		return view;
	}

}
