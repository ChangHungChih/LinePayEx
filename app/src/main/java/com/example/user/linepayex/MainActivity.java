package com.example.user.linepayex;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    int linePaySupportedVersion = 230;

    Button btnReserve;
    Button btnSetOrderId;
    Button btnRefund;
    Button btnPaymentCheck;
    Button btnOneTimeKeyPay;
    Button btnOneTimeKeyRefund;
    Button btnStatusCheck;
    Button btnClear;
    TextView tvResult;
    TextView tvShowPayment;
    EditText etOrderId;

    String orderId;
    String lineUrl;
    public static String transactionId;
    public static String regKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.wtf(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult1);
        etOrderId = findViewById(R.id.etSetOrderId);
        btnReserve = findViewById(R.id.btnLineReserve);
        btnReserve.setOnClickListener(this);
        btnSetOrderId = findViewById(R.id.btnLaunchUri);
        btnSetOrderId.setOnClickListener(this);
        btnRefund = findViewById(R.id.btnLineRefund);
        btnRefund.setOnClickListener(this);
        btnPaymentCheck = findViewById(R.id.btnPaymentCheck);
        btnPaymentCheck.setOnClickListener(this);
        btnOneTimeKeyPay = findViewById(R.id.btnOneTimeKeyPay);
        btnOneTimeKeyPay.setOnClickListener(this);
        btnOneTimeKeyRefund = findViewById(R.id.btnOneTimeKeyRefund);
        btnOneTimeKeyRefund.setOnClickListener(this);
        btnStatusCheck = findViewById(R.id.btnStatusCheck);
        btnStatusCheck.setOnClickListener(this);
        btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);
        tvShowPayment = findViewById(R.id.tvPaymentCheck);


        setText();
    }

    @Override
    protected void onStart() {
        Log.wtf(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.wtf(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.wtf(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.wtf(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        Log.wtf(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLineReserve:
//                checkRegKey(regKey);
//                if (regKey == null) {
//                    onReserveClick();
//                } else {
//                    preapprovedPay();
//                }

                onReserveClick();
                break;
            case R.id.btnLaunchUri:
                String input = etOrderId.getText().toString();
                if (input != null && input.length() > 0) {
                    lineUrl = input;
                    setText();
                }
                launchUri(lineUrl);
                break;
            case R.id.btnLineRefund:
                onRefundClick();
                break;
            case R.id.btnPaymentCheck:
                onPaymentCheckClick();
                break;
            case R.id.btnOneTimeKeyPay:
                onOneTimeKeyPayClick();
                break;
            case R.id.btnOneTimeKeyRefund:
                onOneTimeKeyRefundClick();
                break;
            case R.id.btnStatusCheck:
                onStatusCheckClick();
                break;
            case R.id.btnClear:
                onClear();
                break;
        }
    }

    private void onReserveClick() {
        orderId = String.valueOf(System.currentTimeMillis());
        setText();
        if (orderId == null) {
            Toast.makeText(this, "請設定訂單編號", Toast.LENGTH_SHORT).show();
            return;
        }
        final ReserveApiRequest reserveApiRequest = new ReserveApiRequest("吃吃", 1, "TWD", orderId, "lafresh://auth_activity");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String body = reserveApiRequest.connectApi();
                final ReserveApiRequest.Response response = new Gson().fromJson(body, ReserveApiRequest.Response.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvShowPayment.setText(body);
                        lineUrl = response.getInfo().getPaymentUrl().getApp();
                        setText();
                    }
                });
                String paymentUrl = response.getInfo().getPaymentUrl().getApp();


                Context context = MainActivity.this;
                try {
                    PackageManager pm = context.getPackageManager();
                    PackageInfo packageInfo = pm.getPackageInfo("jp.naver.line.android", 0);
                    int versionCode = packageInfo.versionCode;
                    if (linePaySupportedVersion <= versionCode) {
//                        launchUri(paymentUrl);
                    } else {
                        confirmLineInstall(context);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    confirmLineInstall(context);
                }
            }
        }).start();

    }

    private void confirmLineInstall(final Context context) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(context).setTitle("LINE Pay").setMessage(getString(R.string.linepay_confirm))
                        .setCancelable(false).setPositiveButton(getString(R.string.linepay_install),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                launchUri("market://details?id=jp.naver.line.android");
                            }
                        }).setNegativeButton(getString(R.string.linepay_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
            }
        });

    }

    private void launchUri(String uriString) {
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        Log.wtf(TAG, "launchUri : " + uriString);
    }

    private void onLaunchAuthActivityClick() {
        String uriString = "lafresh://auth_activity";
        launchUri(uriString);
    }

    private void onClear() {
        regKey = null;
        setText();
    }

    private void preapprovedPay() {
        orderId = String.valueOf(System.currentTimeMillis());
        setText();
        if (orderId == null) {
            Toast.makeText(this, "請設定訂單編號", Toast.LENGTH_SHORT).show();
            return;
        }

        final PreapprovedPayApiRequest preapprovedPayApiRequest = new PreapprovedPayApiRequest(regKey, "吃吃吃吃吃", 1, "TWD", orderId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String body = preapprovedPayApiRequest.connectApi();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvShowPayment.setText(body);
                    }
                });
            }
        }).start();
    }

    private void checkRegKey(String checkRegKey) {
        if (checkRegKey == null) {
            return;
        }
        final RegKeyCheckApiRequest regKeyCheckApiRequest = new RegKeyCheckApiRequest(checkRegKey);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String body = regKeyCheckApiRequest.connectApi();
                final RegKeyCheckApiRequest.Response response = new Gson().fromJson(body, RegKeyCheckApiRequest.Response.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvShowPayment.setText(body);
                        if (!"0000".equals(response.getReturnCode())) {
                            //regKey不對 清空會走第一次付款流程
                            regKey = null;
                            setText();
                        }

                    }
                });
            }
        }).start();
    }

    public void setText() {
        tvResult.setText("orderId : " + orderId + "\n transactionId : " + transactionId + "\n regKey : " + regKey + "\nurl : " + lineUrl);
    }


    private void onRefundClick() {
        int refundAmount = 0; //預退款金額 如果為0預設全退
        final RefundApiRequest refundApiRequest = new RefundApiRequest(refundAmount, transactionId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String body = refundApiRequest.connectApi();
                final RefundApiRequest.Response response = new Gson().fromJson(body, RefundApiRequest.Response.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = "退款狀態 : " + response.getReturnMessage() + "\n";
                        tvShowPayment.setText(msg);
                    }
                });
            }
        }).start();
    }

    private void onPaymentCheckClick() {
        final PaymentCheckApiRequest paymentCheckApiRequest = new PaymentCheckApiRequest(transactionId, orderId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String body = paymentCheckApiRequest.connectApi();
                final PaymentCheckApiRequest.Response response = new Gson().fromJson(body, PaymentCheckApiRequest.Response.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = "查詢狀態 : " + response.getReturnMessage() + "\n";
                        if ("0000".equals(response.getReturnCode())) {
                            msg += "交易編號 : " + response.getInfo().get(0).getTransactionId() + "\n" +
                                    "交易日期與時間 : " + response.getInfo().get(0).getTransactionDate() + "\n" +
                                    "交易類型 : " + response.getInfo().get(0).getTransactionType() + "\n" +
                                    "產品名稱 : " + response.getInfo().get(0).getProductName() + "\n" +
                                    "使用的付款方式 : " + response.getInfo().get(0).getPayInfo().get(0).getMethod() + "\n" +
                                    "交易金額 : " + response.getInfo().get(0).getPayInfo().get(0).getAmount();
                        }
                        tvShowPayment.setText(msg);
                    }
                });
            }
        }).start();
    }

    private void onOneTimeKeyPayClick() {
        String oneTimeKey = etOrderId.getText().toString().trim();
        final PaymentApiRequest paymentApiRequest =
                new PaymentApiRequest("繼續吃", 1.0, "TWD", orderId, oneTimeKey);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String body = paymentApiRequest.connectApi();
                PaymentApiRequest.Response response = new Gson().fromJson(body, PaymentApiRequest.Response.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvShowPayment.setText(body);
                    }
                });
            }
        }).start();
    }

    private void onOneTimeKeyRefundClick() {
        final OneTimeKeyRefundApiRequest refundApiRequest = new OneTimeKeyRefundApiRequest(orderId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String body = refundApiRequest.connectApi();
                OneTimeKeyRefundApiRequest.Response response = new Gson().fromJson(body, OneTimeKeyRefundApiRequest.Response.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvShowPayment.setText(body);
                    }
                });
            }
        }).start();
    }

    private void onStatusCheckClick() {
        final StatusCheckApiRequest statusCheckApiRequest = new StatusCheckApiRequest(orderId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String body = statusCheckApiRequest.connectApi();
                StatusCheckApiRequest.Response response = new Gson().fromJson(body, StatusCheckApiRequest.Response.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvShowPayment.setText(body);
                    }
                });
            }
        }).start();
    }
}
