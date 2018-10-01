package com.example.user.linepayex;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StatusCheckApiRequest {
    private static final String TAG = "StatusCheckApiRequest";
    private static String STATUS_CHECK_API_URL = "/v2/payments/orders/{orderId}/check";

    private String orderId;

    public StatusCheckApiRequest(String orderId) {
        this.orderId = orderId;
    }

    public String connectApi() {
        String url = "";

        try {
            url = URLEncoder.encode(orderId, "utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(5, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(20, TimeUnit.SECONDS);    // socket timeout
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("X-LINE-ChannelId", "1596472709")
                .addHeader("X-LINE-ChannelSecret", "2a34c4d4cd0f97e3e9a4969d83dfe02d")
                .url("https://sandbox-api-pay.line.me" + STATUS_CHECK_API_URL.replace("{orderId}", url))
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

    public class Response{

        /**
         * returnCode : 0000
         * returnMessage : success
         * info : {"status":"COMPLETE","transactoinId":"2016010112345678910","orderId":"test_order_#1","transactionDate":"2016-01-01T01:01:00Z","payInfo":[{"method":"BALANCE","amount":10},{"method":"DISCOUNT","amount":5}],"balance":9900}
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
             * status : COMPLETE
             * transactoinId : 2016010112345678910
             * orderId : test_order_#1
             * transactionDate : 2016-01-01T01:01:00Z
             * payInfo : [{"method":"BALANCE","amount":10},{"method":"DISCOUNT","amount":5}]
             * balance : 9900
             */

            private String status;
            private String transactoinId;
            private String failReturnCode;
            private String failReturnMessage;
            private String orderId;
            private String transactionDate;
            private int balance;
            private List<PayInfoBean> payInfo;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getTransactoinId() {
                return transactoinId;
            }

            public void setTransactoinId(String transactoinId) {
                this.transactoinId = transactoinId;
            }

            public String getFailReturnCode() {
                return failReturnCode;
            }

            public void setFailReturnCode(String failReturnCode) {
                this.failReturnCode = failReturnCode;
            }

            public String getFailReturnMessage() {
                return failReturnMessage;
            }

            public void setFailReturnMessage(String failReturnMessage) {
                this.failReturnMessage = failReturnMessage;
            }

            public String getOrderId() {
                return orderId;
            }

            public void setOrderId(String orderId) {
                this.orderId = orderId;
            }

            public String getTransactionDate() {
                return transactionDate;
            }

            public void setTransactionDate(String transactionDate) {
                this.transactionDate = transactionDate;
            }

            public int getBalance() {
                return balance;
            }

            public void setBalance(int balance) {
                this.balance = balance;
            }

            public List<PayInfoBean> getPayInfo() {
                return payInfo;
            }

            public void setPayInfo(List<PayInfoBean> payInfo) {
                this.payInfo = payInfo;
            }

            public class PayInfoBean {
                /**
                 * method : BALANCE
                 * amount : 10
                 */

                private String method;
                private int amount;

                public String getMethod() {
                    return method;
                }

                public void setMethod(String method) {
                    this.method = method;
                }

                public int getAmount() {
                    return amount;
                }

                public void setAmount(int amount) {
                    this.amount = amount;
                }
            }
        }
    }
}
