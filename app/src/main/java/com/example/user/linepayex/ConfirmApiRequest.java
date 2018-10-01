package com.example.user.linepayex;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ConfirmApiRequest {
    private static final String TAG = "ConfirmApiRequest";
    private static String CONFIRM_API_URL = "/v2/payments/{transactionId}/confirm";
    public static final MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json");

    private int amount;                      //付款金額
    private String currency;                 //付款貨幣 (ISO 4217)
    private String transactionId;

    public ConfirmApiRequest(int amount, String currency, String transactionId) {
        this.amount = amount;
        this.currency = currency;
        this.transactionId = transactionId;
    }

    public String connectApi() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("amount", amount);
            jsonObject.put("currency", currency);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonString = jsonObject.toString();
        RequestBody formBody = RequestBody.create(FORM_CONTENT_TYPE, jsonString);

//        RequestBody formBody = new FormEncodingBuilder()
//                .add("productName", productName)
//                .add("amount", String.valueOf(amount))
//                .add("currency", currency)
//                .add("orderId", orderId)
//                .add("confirmUrl", confirmUrl)
//                .build();

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(15, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(15, TimeUnit.SECONDS);    // socket timeout
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("X-LINE-ChannelId", "1596472709")
                .addHeader("X-LINE-ChannelSecret", "2a34c4d4cd0f97e3e9a4969d83dfe02d")
                .url("https://api-pay.line.me" + CONFIRM_API_URL.replace("{transactionId}", transactionId))
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
            private String orderId;
            private String transactionId;
            private String regKey;
            private List<PayInfoBean> payInfo;

            public String getOrderId() {
                return orderId;
            }

            public void setOrderId(String orderId) {
                this.orderId = orderId;
            }

            public String getTransactionId() {
                return transactionId;
            }

            public void setTransactionId(String transactionId) {
                this.transactionId = transactionId;
            }

            public String getRegKey() {
                return regKey;
            }

            public void setRegKey(String regKey) {
                this.regKey = regKey;
            }

            public List<PayInfoBean> getPayInfo() {
                return payInfo;
            }

            public void setPayInfo(List<PayInfoBean> payInfo) {
                this.payInfo = payInfo;
            }

            public class PayInfoBean {
                private String method;
                private int amount;
                private String creditCardNickname;
                private String creditCardBrand;

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

                public String getCreditCardNickname() {
                    return creditCardNickname;
                }

                public void setCreditCardNickname(String creditCardNickname) {
                    this.creditCardNickname = creditCardNickname;
                }

                public String getCreditCardBrand() {
                    return creditCardBrand;
                }

                public void setCreditCardBrand(String creditCardBrand) {
                    this.creditCardBrand = creditCardBrand;
                }
            }
        }
    }
}
