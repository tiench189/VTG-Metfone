package com.vtg.app.util;

import android.util.Log;
import com.bumptech.glide.load.Key;
import com.vtg.app.model.ModelParam;
import com.vtg.app.model.ModelTag;
import java.net.URI;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;

public class SOAPRegisProfile {
    public static final String PREFIX_REQUEST = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.bccsgw.viettel.com/\"><soapenv:Header/><soapenv:Body><web:gwOperation><Input>";
    public static final String SUFFIX_REQUEST = "</Input></web:gwOperation></soapenv:Body></soapenv:Envelope>";
    private static final String TAG = "SOAPUtil";
    public static final String WSDL_URL = "http://36.37.242.77:8070/BCCSGateway?wsdl";
    public static final String WS_NAMESPACE = "http://webservice.bccsgw.viettel.com/";
    public Document mDoc;
    public List<ModelParam> params;
    public String request;
    public String result;
    public List<ModelTag> tags;
    public String wscode;

    public static String createTag(ModelTag tag) {
        return "<" + tag.tag + ">" + tag.value + "</" + tag.tag + ">";
    }

    public static String createParam(ModelParam param) {
        return "<param name=\"" + param.name + "\" value=\"" + param.value + "\"/>";
    }

    public SOAPRegisProfile(String request) {
        this.request = request;
    }

    public String makeSOAPRequest() {
        this.result = "";
        try {
            String soapAction = WS_NAMESPACE;
            StringEntity se = new StringEntity(this.request, Key.STRING_CHARSET_NAME);
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
            HttpConnectionParams.setSoTimeout(httpParameters, 15000);
            DefaultHttpClient client = new DefaultHttpClient(httpParameters);
            HttpPost httpPost = new HttpPost(new URI(WSDL_URL));
            httpPost.addHeader("Content-Type", "text/xml; charset=utf-8");
            httpPost.addHeader("SOAPAction", soapAction);
            httpPost.setEntity(se);
            HttpResponse response = client.execute(httpPost);
            int responseStatusCode = response.getStatusLine().getStatusCode();
            Log.d(TAG, "HTTP Status code:" + responseStatusCode);
            if (responseStatusCode >= 200 && responseStatusCode < 300) {
                this.result = EntityUtils.toString(response.getEntity());
                Log.i(TAG, "tiench response root: " + this.result);
                this.mDoc = getDocumentFromXML();
            }
        } catch (Exception e) {
            Log.e("Response Exception", "tiench error: " + e.getMessage(), e);
        }
        return this.result;
    }

    public Document getDocumentFromXML() {
        String apiResponse = XMLParser.getValueFromDoc(XMLParser.getDomElement(this.result), "original");
        Log.d(TAG, "Tiench " + this.wscode + " Response:: " + apiResponse);
        return XMLParser.getDomElement(apiResponse);
    }

    public int getError() {
        int err = -1;
        try {
            err = Integer.parseInt(XMLParser.getValueFromDoc(XMLParser.getDomElement(this.result), "error"));
        } catch (Exception e) {
        }
        return err;
    }

    public String getValue(String str) {
        return XMLParser.getValueFromDoc(this.mDoc, str);
    }
}
