package com.example.wjy.buttontest;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DisplayActivity extends Activity {

  private WebView showWv;
  private String ip;
  private String imei;
  private String userName;

  //http://ip:3000/d/imei/userName
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_display);
    Intent intent = getIntent();
    ip = intent.getStringExtra("ip");
    imei = intent.getStringExtra("imei");
    userName = intent.getStringExtra("username");
    String urlStr = "http://%s:33000/d/%s/%s";
//        urlEt = findViewById(R.id.ip);
    String url = String.format(urlStr, ip, imei, userName);
    showWv = findViewById(R.id.show);
    try {
      showWv.loadUrl(url);
    } catch (Throwable e) {
      e.printStackTrace();
    }
//        urlEt.setOnEditorActionListener((view, actionId, event) -> {
//            if (actionId == EditorInfo.IME_ACTION_GO) {
//                String urlStr = urlEt.getText().toString();
//                showWv.loadUrl(urlStr);
//            }
//            return true;
//        });

    showWv.setWebViewClient(new WebViewClient());
    showWv.getSettings().setJavaScriptEnabled(true);
  }
}

