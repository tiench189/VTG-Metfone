package com.vtg.app.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vtg.app.metfone.R;

public class DialogConfirm extends Dialog {
    public RelativeLayout btnNo;
    public RelativeLayout btnYes;
    public TextView tvMess;
    public TextView tvNo;
    public TextView tvYes;


    public DialogConfirm(Context context, String msg) {
        super(context);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.dialog_confirm);
        setCanceledOnTouchOutside(true);
        this.tvMess = (TextView) findViewById(R.id.tvMess);
        this.tvYes = (TextView) findViewById(R.id.tv_yes);
        this.tvNo = (TextView) findViewById(R.id.tv_no);
        this.btnYes = (RelativeLayout) findViewById(R.id.btn_yes);
        this.btnNo = (RelativeLayout) findViewById(R.id.btn_no);
        this.tvMess.setText(msg);
        this.btnYes.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
        this.btnNo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
    }
}
