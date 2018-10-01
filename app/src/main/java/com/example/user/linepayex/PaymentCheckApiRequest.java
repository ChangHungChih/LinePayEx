package com.example.user.linepayex;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PaymentCheckApiRequest {
    private static final String TAG = "ConfirmApiRequest";
    private static String PAYMENT_API_URL = "/v2/payments";

    private String transactionId;
    private String orderId;
    private String transactionType;

    public PaymentCheckApiRequest(String transactionId, String orderId) {
        this.transactionId = transactionId;
        this.orderId = orderId;
    }

    public String connectApi() {
        String url = "";
        if (transactionId != null && transactionId.length() > 0) {
            url = url + "?transactionId=" + transactionId;
        }
        if (orderId != null && orderId.length() > 0) {
            if (url.length() > 0) {
                url = url + "&";
            } else {
                url = url + "?";
            }
            url = url + "orderId=" + orderId;
        }

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(5, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(20, TimeUnit.SECONDS);    // socket timeout
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("X-LINE-ChannelId", "1596472709")
                .addHeader("X-LINE-ChannelSecret", "2a34c4d4cd0f97e3e9a4969d83dfe02d")
                .url("https://api-pay.line.me" + PAYMENT_API_URL + url)
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
                JSONArray array = new JSONArray(json.get("info").toString());
                JSONObject info = array.getJSONObject(0);
                transactionType = info.getString("transactionType");
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
        private List<InfoBean> info;

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

        public List<InfoBean> getInfo() {
            return info;
        }

        public void setInfo(List<InfoBean> info) {
            this.info = info;
        }

        public class InfoBean {
            /**
             * "transactionId":2018082247939101010
             * "transactionDate":"2018-08-22T07:07:16Z"
             * "transactionType":"PAYMENT"
             * "productName":"吃吃"
             * "currency":"TWD"
             * "payInfo":[{"method":"CREDIT_CARD","amount":1}]
             * "orderId":"6"
             */

            private long transactionId;
            private String transactionDate;
            private String transactionType;
            private String productName;
            private String currency;
            private List<PayInfoBean> payInfo;
            private String orderId;

            public long getTransactionId() {
                return transactionId;
            }

            public void setTransactionId(long transactionId) {
                this.transactionId = transactionId;
            }

            public String getTransactionDate() {
                return transactionDate;
            }

            public void setTransactionDate(String transactionDate) {
                this.transactionDate = transactionDate;
            }

            public String getTransactionType() {
                return transactionType;
            }

            public void setTransactionType(String transactionType) {
                this.transactionType = transactionType;
            }

            public String getProductName() {
                return productName;
            }

            public void setProductName(String productName) {
                this.productName = productName;
            }

            public String getCurrency() {
                return currency;
            }

            public void setCurrency(String currency) {
                this.currency = currency;
            }

            public List<PayInfoBean> getPayInfo() {
                return payInfo;
            }

            public void setPayInfo(List<PayInfoBean> payInfo) {
                this.payInfo = payInfo;
            }

            public String getOrderId() {
                return orderId;
            }

            public void setOrderId(String orderId) {
                this.orderId = orderId;
            }

            public class PayInfoBean {
                /**
                 * "method":"CREDIT_CARD"
                 * "amount":1
                 */
                private String method;
                private String amount;

                public String getMethod() {
                    return method;
                }

                public void setMethod(String method) {
                    this.method = method;
                }

                public String getAmount() {
                    return amount;
                }

                public void setAmount(String amount) {
                    this.amount = amount;
                }
            }
        }
    }
}
