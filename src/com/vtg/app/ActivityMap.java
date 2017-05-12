package com.vtg.app;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition.Builder;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelShowroom;

public class ActivityMap extends FragmentActivity implements OnMapReadyCallback {
	private GoogleMap googleMap;
	private ModelShowroom mSr;
	private Context mContext;

	/* renamed from: com.vtg.app.ActivityMap.1 */
	class C05621 implements OnClickListener {
		C05621() {
		}

		public void onClick(View v) {
			actionBack();
		}
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(1);
		setContentView(R.layout.activity_map);
		mSr = ActivityShowroom.crShowroom;
		mContext = this;
		try {
			initilizeMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		((ImageView) findViewById(R.id.ivBack))
				.setOnClickListener(new C05621());
	}

	@Override
	public void onMapReady(GoogleMap gMap) {
		googleMap = gMap;

		if (googleMap == null) {
			Toast.makeText(getApplicationContext(),
					"Sorry! unable to create maps", Toast.LENGTH_LONG).show();
		}
		MarkerOptions marker = new MarkerOptions()
				.position(new LatLng(mSr.latitude, mSr.longtitude))
				.title(mSr.name)
				.snippet(
						getString(R.string.address) + ": " + mSr.address
								+ "\n" + getString(R.string.tel) + ": "
								+ mSr.phone);
		marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
		googleMap.addMarker(marker);
		googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

			@Override
			public View getInfoWindow(Marker arg0) {
				return null;
			}

			@Override
			public View getInfoContents(Marker marker) {

				LinearLayout info = new LinearLayout(mContext);
				info.setOrientation(LinearLayout.VERTICAL);

				TextView title = new TextView(mContext);
				title.setTextColor(getResources().getColor(
						R.color.main_color));
				title.setGravity(Gravity.CENTER);
				title.setTypeface(null, Typeface.BOLD);
				title.setText(marker.getTitle());

				TextView snippet = new TextView(mContext);
				snippet.setTextColor(Color.GRAY);
				snippet.setText(marker.getSnippet());

				info.addView(title);
				info.addView(snippet);

				return info;
			}
		});
		googleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(new Builder()
						.target(new LatLng(mSr.latitude, mSr.longtitude))
						.zoom(13.0f).build()));
		Log.v("", "tiench map: " + mSr.latitude + "/" + mSr.longtitude);
		googleMap.setMyLocationEnabled(false);
		googleMap.getUiSettings().setZoomControlsEnabled(false);
	}

	private void initilizeMap() {
		if (googleMap == null) {
			((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMapAsync(this);
		}
	}

	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	private void actionBack() {
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

	public void onBackPressed() {
		actionBack();
	}
}
