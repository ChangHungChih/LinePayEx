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

public class PaymentApiRequest {
    private static final String TAG = "PaymentApiRequest";
    private static String PAYMENT_API_URL = "/v2/payments/oneTimeKeys/pay";
    public static final MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json");

    //必須欄位
    private String productName;         //產品名稱 (charset:"UTF-8")
    private double amount;                         //付款金額
    private String currency;                 //付款貨幣 (ISO 4217)
    // 支援的貨幣如下：  USD  JPY   TWD  THB
    private String orderId;                   //商家與該筆付款請求對應的訂單編號  這是商家自行管理的唯一編號。
    private String oneTimeKey;             // oneTimeKey 的有效時間為 5 分鐘，有效時間從 LINEPay
                                            // 用戶開啟"我的條碼"裡的 QR/Bar code 後開始計算。


    public PaymentApiRequest(String productName, double amount, String currency, String orderId, String oneTimeKey) {
        this.productName = productName;
        this.amount = amount;
        this.currency = currency;
        this.orderId = orderId;
        this.oneTimeKey = oneTimeKey;
    }

    public String connectApi() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productName", productName);
            jsonObject.put("amount", amount);
            jsonObject.put("currency", currency);
            jsonObject.put("orderId", orderId);
            jsonObject.put("oneTimeKey", oneTimeKey);
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
                .url("https://sandbox-api-pay.line.me" + PAYMENT_API_URL)
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


    //如果付款已完成，必須檢查info.payinfo[].amount的總金額是否與交易所請求的金額一致。
    public class Response{

        /**
         * returnCode : 0000
         * returnMessage : success
         * info : {"transactoinId":"2016010112345678910","orderId":"test_order_#1","transactionDate":"2016-01-01T01:01:00Z","payInfo":[{"method":"BALANCE","amount":10},{"method":"DISCOUNT","amount":5}],"balance":9900}
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
             * transactoinId : 2016010112345678910
             * orderId : test_order_#1
             * transactionDate : 2016-01-01T01:01:00Z
             * payInfo : [{"method":"BALANCE","amount":10},{"method":"DISCOUNT","amount":5}]
             * balance : 9900
             */

            private String transactoinId;
            private String orderId;
            private String transactionDate;
            private int balance;
            private List<PayInfoBean> payInfo;

            public String getTransactoinId() {
                return transactoinId;
            }

            public void setTransactoinId(String transactoinId) {
                this.transactoinId = transactoinId;
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
