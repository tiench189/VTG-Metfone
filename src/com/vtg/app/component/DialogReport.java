package com.vtg.app.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.vtg.app.metfone.R;

public class DialogReport extends Dialog {
	public RelativeLayout btnOK;
	public TextView tvMess;

	public DialogReport(Context context, String msg) {
		super(context);
		requestWindowFeature(1);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));
		setContentView(R.layout.dialog_report);
		setCanceledOnTouchOutside(false);
		this.tvMess = (TextView) findViewById(R.id.tvMess);
		this.btnOK = (RelativeLayout) findViewById(R.id.btn_ok);
		this.tvMess.setText(msg);
		this.btnOK.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();

			}
		});
	}
}
