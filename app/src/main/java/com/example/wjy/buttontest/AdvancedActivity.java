package com.example.wjy.buttontest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class AdvancedActivity extends Activity implements View.OnClickListener {

  private String ipAddr = "";
  private String port = "";
  private EditText ipEdit, portEdit;
  private Boolean saveLogin;
  private CheckBox saveCheckBox;
  private SharedPreferences loginPreferences;
  private SharedPreferences.Editor loginPrefsEditor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_advanced);

    loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
    loginPrefsEditor = loginPreferences.edit();
    /**
     * 绑定UI组件给类对象
     */
    ipEdit = (EditText) findViewById(R.id.ip);
    ipAddr = ipEdit.getText().toString();
    portEdit = (EditText) findViewById(R.id.port);
    port = portEdit.getText().toString();
    saveCheckBox = (CheckBox) findViewById(R.id.checkSave2);
    Button btn1 = (Button) findViewById(R.id.submit2);
    btn1.setOnClickListener(this);

    saveLogin = loginPreferences.getBoolean("saveLogin", false);
    ipEdit.setText(loginPreferences.getString("ipaddr", "166.111.7.145"));
    portEdit.setText(loginPreferences.getString("port", "38083"));

    if (saveLogin == true) {
      saveCheckBox.setChecked(true);
    }

  }

  @Override
  public void onClick(View v) {
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    ipAddr = ipEdit.getText().toString();
    port = portEdit.getText().toString();

    if (ipAddr.equals("") || port.equals("")) {
      Toast.makeText(AdvancedActivity.this, "请将以上信息填写完全", Toast.LENGTH_SHORT).show();
      return;
    }

    if (saveCheckBox.isChecked()) {
      loginPrefsEditor.putBoolean("saveLogin", true);
      loginPrefsEditor.putString("ipaddr", ipAddr);
      loginPrefsEditor.putString("port", port);
      loginPrefsEditor.commit();
    } else {
      loginPrefsEditor.clear();
      loginPrefsEditor.commit();
    }

    // 给bnt1添加点击响应事件
    Intent intent = new Intent(AdvancedActivity.this, MainActivity.class);
    intent.putExtra("ip", ipAddr);//设置参数ip
    intent.putExtra("port", port); //设置参数port
    //启动
    startActivity(intent);
  }
}
