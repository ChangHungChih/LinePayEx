package com.example.user.linepayex;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RegKeyCheckApiRequest {
    private static final String TAG = "RegKeyCheckApiRequest";
    private static String REGKEY_CHECK_URL = "/v2/payments/preapprovedPay/{regKey}/check ";

    private String regKey;

    public RegKeyCheckApiRequest(String regKey) {
        this.regKey = regKey;
    }

    public String connectApi() {
        if (regKey == null || regKey.equals("")) {
            return null;
        }else {
            REGKEY_CHECK_URL=REGKEY_CHECK_URL.replace("{regKey}",regKey);
        }

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(5, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(20, TimeUnit.SECONDS);    // socket timeout
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("X-LINE-ChannelId", "1596472709")
                .addHeader("X-LINE-ChannelSecret", "2a34c4d4cd0f97e3e9a4969d83dfe02d")
                .url("https://api-pay.line.me" + REGKEY_CHECK_URL)
                .get()
                .build();
        Log.i(TAG, request.urlString());
        try {
            com.squareup.okhttp.Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            } else {
                String body = response.body().string();
                JSONObject json = new JSONObject(body);
                Log.d(TAG, json.toString());
                return body;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public class Response {
        /**
         * "returnCode":"0000"
         * "returnMessage":"Success."
         * "info":[{"transactionId":2018082247939101010,"transactionDate":"2018-08-22T07:07:16Z","transactionType":"PAYMENT","productName":"吃吃","currency":"TWD","payInfo":[{"method":"CREDIT_CARD","amount":1}],"orderId":"6"}]}
         */

        private String returnCode;
        private String returnMessage;

        public String getReturnCode() {
            return returnCode;
        }

        public void setReturnCode(String returnCode) {
            this.returnCode = returnCode;
        }

        public String getReturnMessage() {
            return returnMessage;
        }

        public void setReturnMessage(String returnMessage) {
            this.returnMessage = returnMessage;
        }
    }
}
