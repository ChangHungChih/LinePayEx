package com.example.user.linepayex;

import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ReserveApiRequest {
    public final static String RESERVE_API_URL = "/v2/payments/request";
    private final static String TAG = "ReserveApiRequest";
    public static final MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json");

    //必須欄位
    private String productName;         //產品名稱 (charset:"UTF-8")
    private int amount;                         //付款金額
    private String currency;                 //付款貨幣 (ISO 4217)
    // 支援的貨幣如下：  USD  JPY   TWD  THB
    private String orderId;                   //商家與該筆付款請求對應的訂單編號  這是商家自行管理的唯一編號。
    private String confirmUrl;             //買家在 LINE Pay 選擇付款方式並輸入密碼後，被重新導 向到商家的 URL。
    //  在重新導向的 URL 上，商家可以呼叫付款 confirm API 並完成付款。
    //  LINE Pay 會傳遞額外的 "transactionId" 參 數


    //非必須欄位
    private String productImageUrl;   //產品影像 URL （或使用品牌 logo URL
    private String mid;                         //LINE 用戶 ID   將要進行付款的 LINE 使用者之 mid
    private String oneTimeKey;          //oneTimeKey，是讀取 LINE Pay app 所提供之二維碼、條碼後之結果。
    // 替代 LINE Pay 會員之 mid。有效時間為 5 分鐘，與 rserve 同時會被刪除
    private String confirmUrlType;    //confirmUrl 類型。買家在 LINE Pay 選擇付款方式並輸入密碼後，被重新導 向到的 URL 所屬的類型。
    //  CLIENT: 手機交易流程  (預設)
    //  SERVER: 網站交易流程。用戶只需要查看 LINE Pay 的付款資訊畫面，然後通知商家 伺服器可以付款。
    private boolean checkConfirmUrlBrowser; //User 前往 confirmUrl 移動時，確認使用的瀏覽器相同與 否
    //  true: 若買家請求付款的瀏覽器與實際打開 confirmUrl 的瀏覽器不同之時，
    // LINE Pay 將會提供請買家回到原本的瀏覽器的介紹頁 面。
    //  false(預設): 不會確認瀏覽器，立即打開 confirmUrl。
    private String cancelUrl;               //取消付款頁面的 URL   當 LINE Pay 用戶取消付款後，
    // 從 LINE 應 用程式付款畫面重新導向的 URL (取消付款 後，
    // 透過行動裝置進入商家應用程式或網站 的商家 URL)。  商家傳送的 URL 會依現況直接使用。
    private String packageName;        //在 Android 各應用程式間轉換時，防止網路釣魚詐騙的資訊。
    private String deliveryPlacePhone;//收件人的聯絡資訊 (用於風險管理)
    private String payType = "PREAPPROVED";                 //付款類型  NORMAL:單筆付款 (預設)  PREAPPROVED:自動付款
    private String langCd;                    //等待付款畫面 (paymentUrl) 的語言代碼。共支援六種語 言。
    private boolean capture;                //指定是否請款  true:呼叫付款 confirm API 時，立即進行付 款授權與請款 (預設)。
    //  false:呼叫付款 confirm API 時，只有經過 授權，然後透過呼叫 "請款 API" 分開請 款，才能完成付款。


    public ReserveApiRequest(String productName, int amount, String currency, String orderId, String confirmUrl) {
        this.productName = productName;
        this.amount = amount;
        this.currency = currency;
        this.orderId = orderId;
        this.confirmUrl = confirmUrl;
    }

    public String connectApi() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productName", productName);
            jsonObject.put("amount", amount);
            jsonObject.put("currency", currency);
            jsonObject.put("orderId", orderId);
            jsonObject.put("confirmUrl", confirmUrl);
            jsonObject.put("payType", payType);
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
                .url("https://api-pay.line.me" + RESERVE_API_URL)
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
         * returnCode : 0000
         * returnMessage : OK
         * info : {"transactionId":123123123123,"paymentUrl":{"web":"http://web-pay.line.me/web/wait?transactionReserveId=blahblah","app":"line://pay/payment/blahblah"},"paymentAccessToken":"187568751124"}
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
             * transactionId : 123123123123
             * paymentUrl : {"web":"http://web-pay.line.me/web/wait?transactionReserveId=blahblah","app":"line://pay/payment/blahblah"}
             * paymentAccessToken : 187568751124
             */

            private long transactionId;
            private PaymentUrlBean paymentUrl;
            private String paymentAccessToken;

            public long getTransactionId() {
                return transactionId;
            }

            public void setTransactionId(long transactionId) {
                this.transactionId = transactionId;
            }

            public PaymentUrlBean getPaymentUrl() {
                return paymentUrl;
            }

            public void setPaymentUrl(PaymentUrlBean paymentUrl) {
                this.paymentUrl = paymentUrl;
            }

            public String getPaymentAccessToken() {
                return paymentAccessToken;
            }

            public void setPaymentAccessToken(String paymentAccessToken) {
                this.paymentAccessToken = paymentAccessToken;
            }

            public class PaymentUrlBean {
                /**
                 * web : http://web-pay.line.me/web/wait?transactionReserveId=blahblah
                 * app : line://pay/payment/blahblah
                 */

                private String web;
                private String app;

                public String getWeb() {
                    return web;
                }

                public void setWeb(String web) {
                    this.web = web;
                }

                public String getApp() {
                    return app;
                }

                public void setApp(String app) {
                    this.app = app;
                }
            }
        }
    }
}
