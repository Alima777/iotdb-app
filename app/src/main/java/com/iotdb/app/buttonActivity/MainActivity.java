package com.iotdb.app.buttonActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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

@RequiresApi(api = VERSION_CODES.KITKAT_WATCH)
public class MainActivity extends AppCompatActivity implements OnClickListener {

  //请求状态码
  private int REQUEST_PERMISSION_CODE = 1;
  private int REQUSET_IPCONFIG_CODE = 2;

  private String ipAddr = "";
  private String port = "";
  private String user = "";
  private String pwd = "";
  private EditText userEdit;
  private EditText passEdit;
  private CheckBox saveCheckBox;
  private SharedPreferences.Editor loginPrefsEditor;

  private AlertDialog alertDialog;

  //读写权限
  private static String[] PERMISSIONS_STORAGE = {
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.READ_PHONE_STATE,
      Manifest.permission.BODY_SENSORS,
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    userEdit = findViewById(R.id.user);
    passEdit = findViewById(R.id.pswd);
    saveCheckBox = findViewById(R.id.checkSave);

    Button loginConfirmButton = findViewById(R.id.submitLoginConfirmButton);
    loginConfirmButton.setOnClickListener(this);

    Button advSettingButton = findViewById(R.id.submitAdvSettingButton);
    advSettingButton.setOnClickListener(v -> {
      Intent intent = new Intent(MainActivity.this, AdvancedActivity.class);
      startActivityForResult(intent, REQUSET_IPCONFIG_CODE);
    });

    SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
    loginPrefsEditor = loginPreferences.edit();

    ipAddr = loginPreferences.getString(appConstant.IP_ADDRESS, appConstant.DEFAULT_IP);
    port = loginPreferences.getString(appConstant.PORT_STR, appConstant.DEFAULT_PORT);
    boolean saveLogin = loginPreferences.getBoolean("saveUserInfo", false);
    if (saveLogin) {
      userEdit.setText(loginPreferences.getString(appConstant.USERNAME_STR, ""));
      passEdit.setText(loginPreferences.getString(appConstant.PASSWORD_STR, ""));
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
      loginPrefsEditor.putBoolean("saveUserInfo", true);
      loginPrefsEditor.putString(appConstant.USERNAME_STR, user);
      loginPrefsEditor.putString(appConstant.PASSWORD_STR, pwd);
    } else {
      loginPrefsEditor.clear();
    }
    loginPrefsEditor.commit();

    // 给 confirm button 添加点击响应事件
    Intent intent = new Intent(MainActivity.this, CollectActivity.class);
    intent.putExtra(appConstant.IP_STR, ipAddr); //设置参数ip
    intent.putExtra(appConstant.PORT_STR, port); //设置参数port
    intent.putExtra(appConstant.USERNAME_STR, user); //设置参数user
    intent.putExtra(appConstant.PASSWORD_STR, pwd); //设置参数pwd
    //启动
    startActivity(intent);

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    // judge requestCode if multi intents are requested for result
    if (resultCode != RESULT_OK || data == null) {
      return;
    }
    ipAddr = data.getStringExtra(appConstant.IP_ADDRESS);
    port = data.getStringExtra(appConstant.PORT_STR);
  }
}

