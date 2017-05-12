package com.vtg.app.component;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vtg.app.ActivityMain;
import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelTag;
import com.vtg.app.model.ModelTypeVas;
import com.vtg.app.model.ModelVas;
import com.vtg.app.util.CommonDefine;
import com.vtg.app.util.CommonDefine.Action;
import com.vtg.app.util.CommonDefine.PreferenceKey;
import com.vtg.app.util.CommonDefine.SoapTag;
import com.vtg.app.util.CommonDefine.WSCode;
import com.vtg.app.util.CommonDefine.mXML;
import com.vtg.app.util.SOAPUtil;

public class AdapterTypeVas extends BaseExpandableListAdapter {

	public Context mContext;
	public LayoutInflater inflater;
	public List<ModelTypeVas> listTypes;
	private SharedPreferences preferences;
	private boolean isActivity;

	public AdapterTypeVas(Context context, List<ModelTypeVas> listTypes,
			boolean isActivity) {
		this.mContext = context;
		this.listTypes = listTypes;
		this.isActivity = isActivity;
		this.inflater = ((Activity) context).getLayoutInflater();
		this.preferences = ((Activity) context).getSharedPreferences(
				CommonDefine.MY_PACKAGE, 0);
	}

	@Override
	public int getGroupCount() {
		return listTypes.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return listTypes.get(groupPosition).listVas.size();
	}

	@Override
	public ModelTypeVas getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return listTypes.get(groupPosition);
	}

	@Override
	public ModelVas getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return getGroup(groupPosition).listVas.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	public class GroupHolder {
		TextView tvName;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.row_type, null);
			GroupHolder holder = new GroupHolder();
			holder.tvName = (TextView) view.findViewById(R.id.tv_name);
			view.setTag(holder);
		}
		GroupHolder holder = (GroupHolder) view.getTag();
		ModelTypeVas type = getGroup(groupPosition);
		holder.tvName.setText(type.name);
		return view;
	}

	private class Holder {
		RelativeLayout btnRegister;
		TextView tvGuide;
		TextView tvName;
		TextView tvPrice;

		private Holder() {
		}
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		Holder holder;
		View view = convertView;
		if (view == null) {
			view = this.inflater.inflate(R.layout.row_vas_unused, null);
			holder = new Holder();
			holder.tvName = (TextView) view.findViewById(R.id.tv_name);
			holder.tvPrice = (TextView) view.findViewById(R.id.tv_price);
			holder.tvGuide = (TextView) view.findViewById(R.id.tv_guide);
			holder.btnRegister = (RelativeLayout) view
					.findViewById(R.id.btn_register);
			view.setTag(holder);
		}
		final ModelVas vas = getChild(groupPosition, childPosition);
		holder = (Holder) view.getTag();
		holder.tvName.setText(vas.name);
		holder.tvPrice.setText(vas.fee);
		holder.tvGuide.setText(vas.description);
		holder.btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final DialogConfirm cfDialog = new DialogConfirm(mContext,
						mContext.getString(R.string.cf_regis) + " " + vas.name
								+ "?");
				cfDialog.btnYes.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						cfDialog.dismiss();
						new AsyncTaskRegister().execute(new ModelVas[] { vas });
					}
				});
				cfDialog.show();
			}
		});
		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	private class AsyncTaskRegister extends
			AsyncTask<ModelVas, String, Boolean> {
		String message;
		ProgressDialog pDialog;

		/*
		 * renamed from:
		 * com.vtg.app.component.AdapterVasUnused.AsyncTaskRegister.1
		 */
		class C06151 implements OnClickListener {
			private final/* synthetic */DialogReport val$sc;

			C06151(DialogReport dialogReport) {
				this.val$sc = dialogReport;
			}

			public void onClick(View v) {
				this.val$sc.dismiss();
				if (isActivity) {
					((Activity) mContext).finish();
				}
			}
		}

		private AsyncTaskRegister() {
			this.message = mContext.getString(R.string.fail_fake_mo);
		}

		protected Boolean doInBackground(ModelVas... prs) {
			try {
				ModelVas vas = prs[0];
				List<ModelTag> tags = new ArrayList();
				tags.add(new ModelTag(SoapTag.USERNAME, ActivityMain.USERNAME));
				tags.add(new ModelTag(SoapTag.PASSWORD, ActivityMain.PASSWORD));
				tags.add(new ModelTag(SoapTag.RAWDATA, "?"));
				List<ModelParam> params = new ArrayList();
				params.add(new ModelParam(mXML.ISDN, preferences.getString(
						PreferenceKey.PHONE_NUMBER, "")));
				params.add(new ModelParam("receiverIsdn", vas.receiverIsdn));
				params.add(new ModelParam("command", vas.regis));
				SOAPUtil soap = new SOAPUtil(WSCode.REGIS_VAS_FAKE_MO, tags,
						params);
				soap.makeSOAPRequest();
				if (soap.getError() != 0) {
					this.message = mContext.getString(R.string.fail_fake_mo);
					return Boolean.valueOf(false);
				} else if (Integer.parseInt(soap.getValue(mXML.CODE)) == 1) {
					this.message = mContext.getString(R.string.success_fake_mo);
					return Boolean.valueOf(true);
				} else {
					this.message = soap.getValue(mXML.MESSAGE);
					return Boolean.valueOf(false);
				}
			} catch (Exception e) {
				return Boolean.valueOf(false);
			}
		}

		protected void onPreExecute() {
			super.onPreExecute();
			this.pDialog = new ProgressDialog(mContext);
			this.pDialog.setMessage(mContext
					.getString(R.string.message_loading));
			this.pDialog.setCancelable(false);
			this.pDialog.show();
		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			this.pDialog.dismiss();
			if (result.booleanValue()) {
				Intent intent = new Intent();
				intent.setAction(CommonDefine.MY_PACKAGE);
				intent.putExtra(Action.ACTION, Action.RELOAD_VAS);
				mContext.sendBroadcast(intent);
				DialogReport sc = new DialogReport(mContext, this.message);
				sc.btnOK.setOnClickListener(new C06151(sc));
				sc.show();
				return;
			}
			new DialogReport(mContext, this.message).show();
		}
	}

}
