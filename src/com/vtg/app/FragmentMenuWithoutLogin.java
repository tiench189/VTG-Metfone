package com.vtg.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.vtg.app.metfone.R;
import com.vtg.app.util.CommonDefine;

public class FragmentMenuWithoutLogin extends Fragment implements OnClickListener, CommonDefine {
    private RelativeLayout btnLogin;
    private RelativeLayout layoutAbout;
    private RelativeLayout layoutNotification;
    private RelativeLayout layoutShowroom;
    private RelativeLayout layoutTariff;
    private SharedPreferences preferences;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.preferences = getActivity().getSharedPreferences(CommonDefine.MY_PACKAGE, 0);
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_without_login, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        this.layoutNotification = (RelativeLayout) view.findViewById(R.id.layout_notification);
        this.layoutNotification.setOnClickListener(this);
        this.layoutAbout = (RelativeLayout) view.findViewById(R.id.layout_about);
        this.layoutAbout.setOnClickListener(this);
        this.layoutTariff = (RelativeLayout) view.findViewById(R.id.layout_tariff);
        this.layoutTariff.setOnClickListener(this);
        this.layoutShowroom = (RelativeLayout) view.findViewById(R.id.layout_showroom);
        this.layoutShowroom.setOnClickListener(this);
        this.btnLogin = (RelativeLayout) view.findViewById(R.id.btn_login);
        this.btnLogin.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                getActivity().startActivity(new Intent(getActivity(), ActivityFirst.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                getActivity().finish();
                break;
            case R.id.layout_notification:
                ActivityWithoutLogin.menu.showContent();
                break;
            case R.id.layout_about:
                startActivity(new Intent(getActivity(), ActivityAbout.class));
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
            case R.id.layout_tariff:
                startActivity(new Intent(getActivity(), ActivityTariff.class));
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
            case R.id.layout_showroom:
                startActivity(new Intent(getActivity(), ActivityShowroom.class));
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
            default:
            	break;
        }
    }
}
