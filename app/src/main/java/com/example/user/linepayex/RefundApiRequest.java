package com.example.user.linepayex;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RefundApiRequest {
    private static final String TAG = "ConfirmApiRequest";
    private static String REFUND_API_URL = "/v2/payments/{transactionId}/refund ";
    public static final MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json");

    private int refundAmount;                      //退款金額
    private String transactionId;

    public RefundApiRequest(int refundAmount, String transactionId) {
        this.refundAmount = refundAmount;
        this.transactionId = transactionId;
    }

    public String connectApi() {
        JSONObject jsonObject = new JSONObject();
        if (refundAmount > 0) {
            try {
                //退款金額若無填則全額退款
                jsonObject.put("refundAmount", refundAmount);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String jsonString = jsonObject.toString();
        RequestBody formBody = RequestBody.create(FORM_CONTENT_TYPE, jsonString);

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(5, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(20, TimeUnit.SECONDS);    // socket timeout
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("X-LINE-ChannelId", "1596472709")
                .addHeader("X-LINE-ChannelSecret", "2a34c4d4cd0f97e3e9a4969d83dfe02d")
                .url("https://api-pay.line.me" + REFUND_API_URL.replace("{transactionId}", transactionId))
                .post(formBody)
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
         * "info":{"refundTransactionId":2018082347999025311,"refundTransactionDate":"2018-08-23T03:43:02Z"}
         */

        private String returnCode;
        private String returnMessage;
        private InfoBean info;

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


        public class InfoBean {
            /**
             * "refundTransactionId":2018082347999025311
             * "refundTransactionDate":"2018-08-23T03:43:02Z"
             */
            private long refundTransactionId;
            private String refundTransactionDate;

            public long getRefundTransactionId() {
                return refundTransactionId;
            }

            public void setRefundTransactionId(long refundTransactionId) {
                this.refundTransactionId = refundTransactionId;
            }

            public String getRefundTransactionDate() {
                return refundTransactionDate;
            }

            public void setRefundTransactionDate(String refundTransactionDate) {
                this.refundTransactionDate = refundTransactionDate;
            }
        }
    }
}
