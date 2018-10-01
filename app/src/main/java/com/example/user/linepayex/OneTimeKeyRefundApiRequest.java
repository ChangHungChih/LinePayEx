package com.example.user.linepayex;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

public class OneTimeKeyRefundApiRequest {
    private static final String TAG = "OneTimeKeyRefundApi";
    private static String REFUND_API_URL = "/v2/payments/orders/{orderId}/refund";
    public static final MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json");

    private String orderId;

    public OneTimeKeyRefundApiRequest(String orderId) {
        this.orderId = orderId;
    }

    public String connectApi() {
        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("orderId", orderId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        String jsonString = jsonObject.toString();
        RequestBody formBody = RequestBody.create(FORM_CONTENT_TYPE, jsonString);

//        RequestBody formBody = new FormEncodingBuilder()
//                .add("productName", productName)
//                .add("amount", String.valueOf(amount))
//                .add("currency", currency)
//                .add("orderId", orderId)
//                .add("confirmUrl", confirmUrl)
//                .build();
        String encodedOrderId = null;
        try {
            encodedOrderId = URLEncoder.encode(orderId, "utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(15, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(15, TimeUnit.SECONDS);    // socket timeout
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("X-LINE-ChannelId", "1596472709")
                .addHeader("X-LINE-ChannelSecret", "2a34c4d4cd0f97e3e9a4969d83dfe02d")
                .url("https://sandbox-api-pay.line.me" + REFUND_API_URL.replace("{orderId}", encodedOrderId))
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

    public class Response{

        /**
         * returnCode : 0000
         * returnMessage : success
         * info : {"refundTransactoinId":"2016010112345678910","refundTransactionDate":"2016-01-01T01:01:00Z"}
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

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public class InfoBean {
            /**
             * refundTransactoinId : 2016010112345678910
             * refundTransactionDate : 2016-01-01T01:01:00Z
             */

            private String refundTransactoinId;
            private String refundTransactionDate;

            public String getRefundTransactoinId() {
                return refundTransactoinId;
            }

            public void setRefundTransactoinId(String refundTransactoinId) {
                this.refundTransactoinId = refundTransactoinId;
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
