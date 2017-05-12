package com.vtg.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.util.Log;

import com.bumptech.glide.load.Key;

public class JSONParser {
    static InputStream is;
    static JSONObject jObj;
    static String json;
    DefaultHttpClient httpClient;
    HttpGet httpGet;
    HttpPost httpPost;
    HttpResponse httpResponse;
    private int timeout;

    static {
        is = null;
        jObj = null;
        json = "";
    }

    public JSONParser() {
        this.timeout = 12;
    }

    public String getJSONFromUrl(String url, String method, List<NameValuePair> nameValuePairs) {
        try {
            this.httpClient = new DefaultHttpClient();
            HttpParams httpParams = this.httpClient.getParams();
            httpParams.setParameter("http.connection.timeout", 30000);
            httpParams.setParameter("http.socket.timeout", 30000);
            if (method.equals(CommonDefine.METHOD_POST)) {
                this.httpPost = new HttpPost(url);
                this.httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, Key.STRING_CHARSET_NAME));
                this.httpResponse = this.httpClient.execute(this.httpPost);
            } else {
                this.httpGet = new HttpGet(url);
                this.httpResponse = this.httpClient.execute(this.httpGet);
            }
            is = this.httpResponse.getEntity().getContent();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line).append("\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e4) {
            Log.e("Buffer Error", "Error converting result " + e4.toString());
        }
        this.httpClient.getConnectionManager().shutdown();
        this.httpClient.clearRequestInterceptors();
        this.httpClient.clearResponseInterceptors();
        return json;
    }
}
