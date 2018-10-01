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

public class PreapprovedPayApiRequest {
    private String TAG = "PreapprovedPayApiRequest";
    private static String PREAPPROVED_PAY_API_URL = "/v2/payments/preapprovedPay/{regKey}/payment";

    public static final MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json");

    private String regKey;
    //必須欄位
    private String productName;         //產品名稱 (charset:"UTF-8")
    private int amount;                         //付款金額
    private String currency;                 //付款貨幣 (ISO 4217)
    // 支援的貨幣如下：  USD  JPY   TWD  THB
    private String orderId;                   //商家與該筆付款請求對應的訂單編號  這是商家自行管理的唯一編號。

    private boolean capture;                //指定是否請款  true:呼叫付款 confirm API 時，立即進行付 款授權與請款 (預設)。
    //  false:呼叫付款 confirm API 時，只有經過 授權，然後透過呼叫 "請款 API" 分開請 款，才能完成付款。


    public PreapprovedPayApiRequest(String regKey, String productName, int amount, String currency, String orderId) {
        this.regKey = regKey;
        this.productName = productName;
        this.amount = amount;
        this.currency = currency;
        this.orderId = orderId;
    }


    public String connectApi() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productName", productName);
            jsonObject.put("amount", amount);
            jsonObject.put("currency", currency);
            jsonObject.put("orderId", orderId);
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
                .url("https://api-pay.line.me" + PREAPPROVED_PAY_API_URL.replace("{regKey}", regKey))
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

}
