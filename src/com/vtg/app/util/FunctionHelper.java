package com.vtg.app.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.vtg.app.metfone.R;
import com.vtg.app.model.ModelArea;
import com.vtg.app.model.ModelBalance;
import com.vtg.app.model.ModelPaper;

@SuppressLint({ "SimpleDateFormat" })
public class FunctionHelper implements CommonDefine {
	private static final String TAG = "FunctionHelper";

	public static String convertBalanceName(Context context, int balanceId) {
		String name = "";
		switch (balanceId) {
		case 2000:
			return context.getResources().getString(R.string.acc2000);
		case 2001:
			return context.getResources().getString(R.string.acc2001);
		case 2002:
			return context.getResources().getString(R.string.acc2002);
		case 2003:
			return context.getResources().getString(R.string.acc2003);
		case 2004:
			return context.getResources().getString(R.string.acc2004);
		case 2100:
			return context.getResources().getString(R.string.acc2100);
		case 2113:
			return context.getResources().getString(R.string.acc2113);
		case 2400:
			return context.getResources().getString(R.string.acc2400);
		case 2500:
			return context.getResources().getString(R.string.acc2500);
		case 2501:
			return context.getResources().getString(R.string.acc2501);
		case 2504:
			return context.getResources().getString(R.string.acc2504);
		case 2505:
			return context.getResources().getString(R.string.acc2505);
		case 4000:
			return context.getResources().getString(R.string.acc4000);
		case 4001:
			return context.getResources().getString(R.string.acc4001);
		case 4046:
			return context.getResources().getString(R.string.acc4046);
		case 4200:
			return context.getResources().getString(R.string.acc4200);
		case 4400:
			return context.getResources().getString(R.string.acc4400);
		case 4500:
			return context.getResources().getString(R.string.acc4500);
		case 4560:
			return context.getResources().getString(R.string.acc4560);
		case 4561:
			return context.getResources().getString(R.string.acc4561);
		default:
			return name;
		}
	}

	public static String convertBalanceUnit(Context context, int balanceId) {
		String name = "";
		switch (balanceId) {
		case 2000:
			return "USD";
		case 2001:
			return "USD";
		case 2002:
			return "USD";
		case 2003:
			return "USD";
		case 2004:
			return "USD";
		case 2100:
			return "USD";
		case 2113:
			return "USD";
		case 2400:
			return "USD";
		case 2500:
			return "USD";
		case 2501:
			return "USD";
		case 2504:
			return "USD";
		case 2505:
			return "USD";
		case 4000:
			return "Mins";
		case 4001:
			return "Mins";
		case 4046:
			return "Mins";
		case 4200:
			return "SMS";
		case 4400:
			return "Mins";
		case 4500:
			return "MB";
		case 4560:
			return "MB";
		case 4561:
			return "MB";
		default:
			return name;
		}
	}

	public static String getErrorMessage(Context context, int errCode) {
		String mess = context.getString(R.string.err_m1);
		switch (errCode) {
		case 0:
			return context.getString(R.string.err0);
		case 1000:
			return context.getString(R.string.err1000);
		case 1001:
			return context.getString(R.string.err1001);
		case 1002:
			return context.getString(R.string.err1002);
		case 2000:
			return context.getString(R.string.err2000);
		case 2001:
			return context.getString(R.string.err2001);
		case 2002:
			return context.getString(R.string.err2002);
		case 2003:
			return context.getString(R.string.err2003);
		case 2004:
			return context.getString(R.string.err2004);
		case 2005:
			return context.getString(R.string.err2005);
		case 3000:
			return context.getString(R.string.err3000);
		case 4000:
			return context.getString(R.string.err4000);
		case 4001:
			return context.getString(R.string.err4001);
		case 5000:
			return context.getString(R.string.err5000);
		case 6000:
			return context.getString(R.string.err6000);
		case 6001:
			return context.getString(R.string.err6001);
		case 6002:
			return context.getString(R.string.err6002);
		case 6003:
			return context.getString(R.string.err6003);
		case 6004:
			return context.getString(R.string.err6004);
		case 6005:
			return context.getString(R.string.err6005);
		case 6006:
			return context.getString(R.string.err6006);
		case 7000:
			return context.getString(R.string.err7000);
		case 7001:
			return context.getString(R.string.err7001);
		case 8000:
			return context.getString(R.string.err8000);
		case 8001:
			return context.getString(R.string.err8001);
		case 8002:
			return context.getString(R.string.err8002);
		case 9000:
			return context.getString(R.string.err9000);
		case 9001:
			return context.getString(R.string.err9001);
		case 9002:
			return context.getString(R.string.err9002);
		default:
			return mess;
		}
	}

	public static String formatCurrentTime() {
		String result = "";
		Calendar c = Calendar.getInstance();
		String dd = c.get(Calendar.DATE) < 10 ? "0" + c.get(Calendar.DATE) : ""
				+ c.get(Calendar.DATE);
		String MM = (c.get(Calendar.MONTH) + 1) < 10 ? "0"
				+ (c.get(Calendar.MONTH) + 1) : ""
				+ (c.get(Calendar.MONTH) + 1);
		String yyyy = "" + c.get(Calendar.YEAR);
		String HH = c.get(Calendar.HOUR) < 10 ? "0" + c.get(Calendar.HOUR) : ""
				+ c.get(Calendar.HOUR);
		String mm = c.get(Calendar.MINUTE) < 10 ? "0" + c.get(Calendar.MINUTE)
				: "" + c.get(Calendar.MINUTE);
		String ss = c.get(Calendar.SECOND) < 10 ? "0" + c.get(Calendar.SECOND)
				: "" + c.get(Calendar.SECOND);
		result = dd + MM + yyyy + HH + mm + ss;
		return result;
	}

	public static String formatDateTime(String oldTime) {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			return new SimpleDateFormat("dd/MM/yyyy")
					.format(sdf.parse(oldTime));
		} catch (Exception ex) {
			ex.printStackTrace();
			return oldTime;
		}
	}

	public static int setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return 0;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		LayoutParams params = listView.getLayoutParams();
		params.height = (listView.getDividerHeight() * (listAdapter.getCount() - 1))
				+ totalHeight;
		listView.setLayoutParams(params);
		listView.requestLayout();
		return totalHeight;
	}

	public static String formatMoney(String oldValue) {
		String newValue = "";
		String[] split = oldValue.split("\\.");
		if (split.length <= 1) {
			return oldValue;
		}
		newValue = new StringBuilder(String.valueOf(new StringBuilder(String
				.valueOf(newValue)).append(split[0]).toString())).append(".")
				.toString();
		if (split[1].length() > 2) {
			newValue = new StringBuilder(String.valueOf(newValue)).append(
					split[1].substring(0, 2)).toString();
		} else {
			newValue = new StringBuilder(String.valueOf(newValue)).append(
					split[1]).toString();
		}
		return newValue;
	}

	public static String makeSignatureAPT(Map<String, String> hm) {
		String TOKEN = "6b2e85357c7581a9d7ef04304ca78080";
		Map<String, String> hmSorted = new TreeMap(hm);
		String values = "";
		for (String key : hmSorted.keySet()) {
			values = new StringBuilder(String.valueOf(values)).append(
					(String) hmSorted.get(key)).toString();
		}
		values = new StringBuilder(String.valueOf(values)).append(TOKEN)
				.toString();
		Log.v("", "tiench sort: " + values);
		return getMD5OfString(values);
	}

	public static String getMD5OfString(String s) {
		String MD5 = "MD5";
		String md5str = "";
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte[] messageDigest = digest.digest();
			StringBuilder hexString = new StringBuilder();
			for (byte aMessageDigest : messageDigest) {
				String h = Integer.toHexString(aMessageDigest
						& MotionEventCompat.ACTION_MASK);
				while (h.length() < 2) {
					h = "0" + h;
				}
				hexString.append(h);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return md5str;
		}
	}

	public static String getPhoneNumber(Context context) {
		try {
			TelephonyManager tMgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String mPhoneNumber = tMgr.getLine1Number();
			Log.v(TAG,
					"tiench mobile: " + mPhoneNumber + "/"
							+ tMgr.getNetworkCountryIso() + "/"
							+ tMgr.getSimCountryIso());
			return mPhoneNumber;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String formatPhoneNumber(String old, Context context) {
		String rs = old;
		try {
			if (old.startsWith("0")) {
				return old.substring(1);
			}
			if (old.startsWith("+")) {
				if (((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE))
						.getNetworkCountryIso().equals("vn")) {
					return old.substring(3);
				}
				return old.substring(4);
			} else if (old.startsWith(CommonDefine.NUMBER_HEADER)) {
				return old.substring(3);
			} else {
				return rs;
			}
		} catch (Exception e) {
			return rs;
		}
	}

	public static String subDataCode(String mess) {
		String result = "";
		try {
			String first = mess.split("\\(")[0];
			result = first.split(" ")[first.split(" ").length - 1];
			Log.v(TAG, "tiench data: " + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
	}

	public static ModelPaper findPaper(String code, List<ModelPaper> papers) {
		for (int i = 0; i < papers.size(); i++) {
			if (((ModelPaper) papers.get(i)).type.equals(code)) {
				return (ModelPaper) papers.get(i);
			}
		}
		return new ModelPaper("", "", "", "");
	}

	public static ArrayList<String> convertArea(List<ModelArea> listArea) {
		ArrayList<String> result = new ArrayList();
		for (int i = 0; i < listArea.size(); i++) {
			result.add(((ModelArea) listArea.get(i)).name);
		}
		return result;
	}

	public static int[] getTimeFromEdt(EditText edt) {
		Date date;
		try {
			date = new SimpleDateFormat("dd/MM/yyyy").parse(edt.getText()
					.toString().trim());
			return new int[] {
					Integer.parseInt((String) DateFormat.format("yyyy", date)),
					Integer.parseInt((String) DateFormat.format("MM", date)) - 1,
					Integer.parseInt((String) DateFormat.format("dd", date)) };
		} catch (Exception e) {
			e.printStackTrace();
			date = new Date();
			return new int[] {
					Integer.parseInt((String) DateFormat.format("yyyy", date)),
					Integer.parseInt((String) DateFormat.format("MM", date)) - 1,
					Integer.parseInt((String) DateFormat.format("dd", date)) };
		}
	}

	public static String convertDateFormat(String date, String oldFormat,
			String newFormat) {
		String result = date;
		try {
			result = new SimpleDateFormat(newFormat)
					.format(new SimpleDateFormat(oldFormat).parse(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String formatFloatMoney(Float mon) {
		try {
			if (mon == 0.0) {
				return "0";
			} else {
				String[] splitDot = (mon + "").split("\\.");
				String pre = splitDot[1];
				if (pre.length() > 3) {
					return splitDot[0] + "." + pre.substring(0, 3);
				} else if (Integer.parseInt(pre) == 0) {
					return splitDot[0];
				} else {
					return mon + "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String formatFloatData(Float mon) {
		try {
			if (mon == 0.0) {
				return "0";
			} else {
				String[] splitDot = (mon + "").split("\\.");
				String pre = splitDot[1];
				if (pre.length() > 2) {
					return splitDot[0] + "." + pre.substring(0, 2);
				} else if (Integer.parseInt(pre) == 0) {
					return splitDot[0];
				} else {
					return mon + "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void checkBalanceExist(List<ModelBalance> listBalances,
			ModelBalance balance) {
		for (int i = 0; i < listBalances.size(); i++) {
			if (listBalances.get(i).id.equals(balance.id)
					&& listBalances.get(i).expire.equals(balance.expire)) {
				listBalances.get(i).val += balance.val;
				return;
			}
		}
		listBalances.add(balance);

	}

	public static String parseExpirityData(String data) {
		String expirity = "";
		try {
			String split1 = data.split(";")[0];
			String[] split2 = split1.split("=");
			String date = split2[split2.length - 1];
			if (date.length() < 5) {
				date = "0" + date;
			}
			Date old = new SimpleDateFormat("ddMMM").parse(date);
			addDays(old, -1);
			expirity = new SimpleDateFormat("dd/MM").format(old);
			Log.d("", "tiench data: " + expirity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return expirity;
	}

	public static void addDays(Date d, int days) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.DATE, days);
		d.setTime(c.getTime().getTime());
	}
}
