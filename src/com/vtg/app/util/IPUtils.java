package com.vtg.app.util;

import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.bumptech.glide.load.Key;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Iterator;

public class IPUtils {
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sbuf = new StringBuilder();
        for (byte b : bytes) {
            int intVal = b & MotionEventCompat.ACTION_MASK;
            if (intVal < 16) {
                sbuf.append("0");
            }
            sbuf.append(Integer.toHexString(intVal).toUpperCase());
        }
        return sbuf.toString();
    }

    public static byte[] getUTF8Bytes(String str) {
        try {
            return str.getBytes(Key.STRING_CHARSET_NAME);
        } catch (Exception e) {
            return null;
        }
    }

    public static String loadFileAsString(String filename) throws IOException {
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
        try {
            String str;
            ByteArrayOutputStream baos = new ByteArrayOutputStream(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
            byte[] bytes = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
            boolean isUTF8 = false;
            int count = 0;
            while (true) {
                int read = is.read(bytes);
                if (read == -1) {
                    break;
                }
                if (count == 0) {
                    if (bytes[0] == -17 && bytes[1] == -69 && bytes[2] == -65) {
                        isUTF8 = true;
                        baos.write(bytes, 3, read - 3);
                        count += read;
                    }
                }
                baos.write(bytes, 0, read);
                count += read;
            }
            if (isUTF8) {
                str = new String(baos.toByteArray(), Key.STRING_CHARSET_NAME);
            } else {
                str = new String(baos.toByteArray());
            }
            return str;
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
    }


    public static String getIPAddress(boolean useIPv4) {
        try {
            for (NetworkInterface intf : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress addr : Collections.list(intf.getInetAddresses())) {
                    if (!addr.isLoopbackAddress()) {
                        boolean isIPv4;
                        String sAddr = addr.getHostAddress();
                        if (sAddr.indexOf(58) < 0) {
                            isIPv4 = true;
                        } else {
                            isIPv4 = false;
                        }
                        if (useIPv4) {
                            if (isIPv4) {
                                return sAddr;
                            }
                        } else if (!isIPv4) {
                            String toUpperCase;
                            int delim = sAddr.indexOf(37);
                            if (delim < 0) {
                                toUpperCase = sAddr.toUpperCase();
                            } else {
                                toUpperCase = sAddr.substring(0, delim).toUpperCase();
                            }
                            return toUpperCase;
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }
}
