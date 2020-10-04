package com.example.wjy.buttontest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnClickListener {

  private String ipAddr = "";
  private String port = "";
  private String user = "";
  private String pwd = "";
  private Boolean saveLogin;
  private EditText userEdit, passEdit;
  private CheckBox saveCheckBox;
  private SharedPreferences loginPreferences;
  private SharedPreferences.Editor loginPrefsEditor;

  private AlertDialog alertDialog;

  //读写权限
  private static String[] PERMISSIONS_STORAGE = {
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.READ_PHONE_STATE,
      Manifest.permission.BODY_SENSORS,
  };
  //请求状态码
  private static int REQUEST_PERMISSION_CODE = 1;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
    loginPrefsEditor = loginPreferences.edit();

    Intent infoIntent = getIntent();
    if (infoIntent.getStringExtra("ip") != null) {
      ipAddr = infoIntent.getStringExtra("ip");
      loginPrefsEditor.putString("ipaddr", ipAddr);
    } else {
      ipAddr = loginPreferences.getString("ipaddr", "166.111.7.145");
    }

    if (infoIntent.getStringExtra("port") != null) {
      port = infoIntent.getStringExtra("port");
      loginPrefsEditor.putString("port", port);
    } else {
      port = loginPreferences.getString("port", "38083");
    }
    userEdit = findViewById(R.id.user);
    user = userEdit.getText().toString();
    passEdit = findViewById(R.id.pswd);
    pwd = passEdit.getText().toString();
    saveCheckBox = findViewById(R.id.checkSave);

    Button btn1 = findViewById(R.id.submit);
    btn1.setOnClickListener(this);

    Button btn2 = findViewById(R.id.submit3);
    btn2.setOnClickListener(v -> {
      Intent intent1 = new Intent(MainActivity.this, AdvancedActivity.class);
      startActivity(intent1);
    });

    saveLogin = loginPreferences.getBoolean("saveLogin", false);
    if (saveLogin == true) {
      userEdit.setText(loginPreferences.getString("username", ""));
      passEdit.setText(loginPreferences.getString("password", ""));
      saveCheckBox.setChecked(true);
    }

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
        != PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
        != PackageManager.PERMISSION_GRANTED
    ) {
//            Toast.makeText(this, "需要授权才能使用我们的服务!", Toast.LENGTH_LONG).show();
      ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
    }
  }

  @Override
  public void onClick(View v) {

    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(userEdit.getWindowToken(), 0);
    user = userEdit.getText().toString();
    pwd = passEdit.getText().toString();

    if (ipAddr.equals("") || port.equals("") || user.equals("") || pwd.equals("")) {
      Toast.makeText(MainActivity.this, "请将以上信息填写完全", Toast.LENGTH_SHORT).show();
      return;
    }

    if (saveCheckBox.isChecked()) {
      loginPrefsEditor.putBoolean("saveLogin", true);
      loginPrefsEditor.putString("username", user);
      loginPrefsEditor.putString("password", pwd);
      loginPrefsEditor.commit();
    } else {
      loginPrefsEditor.clear();
      loginPrefsEditor.commit();
    }

    // 给bnt1添加点击响应事件
    Intent intent = new Intent(MainActivity.this, CollectActivity.class);
    intent.putExtra("ip", ipAddr);//设置参数ip
    intent.putExtra("port", port); //设置参数port
    intent.putExtra("username", user); //设置参数user
    intent.putExtra("password", pwd); //设置参数pswd
    //启动
    startActivity(intent);

  }
}

