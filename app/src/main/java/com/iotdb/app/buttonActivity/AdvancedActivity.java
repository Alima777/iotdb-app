package com.iotdb.app.buttonActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class AdvancedActivity extends Activity implements View.OnClickListener {

  private String ipAddr = "";
  private String port = "";
  private EditText ipEdit, portEdit;
  private Boolean saveIpConfig;
  private CheckBox saveCheckBox;
  private SharedPreferences loginPreferences;
  private SharedPreferences.Editor loginPrefsEditor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_advanced);

    // init components
    ipEdit = findViewById(R.id.ip);
    portEdit = findViewById(R.id.port);
    saveCheckBox = findViewById(R.id.checkSave2);
    Button advSettingConfirmBtn = findViewById(R.id.submitAdvSettingConfirm);
    advSettingConfirmBtn.setOnClickListener(this);

    loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
    loginPrefsEditor = loginPreferences.edit();
    saveIpConfig = loginPreferences.getBoolean("saveIpConfig", false);
    if (saveIpConfig) {
      saveCheckBox.setChecked(true);
    }
    ipEdit.setText(loginPreferences.getString(appConstant.IP_ADDRESS, appConstant.DEFAULT_IP));
    portEdit.setText(loginPreferences.getString(appConstant.PORT_STR, appConstant.DEFAULT_PORT));
  }

  @Override
  public void onClick(View v) {
    ipAddr = ipEdit.getText().toString();
    port = portEdit.getText().toString();

    if (ipAddr.equals("") || port.equals("")) {
      Toast.makeText(AdvancedActivity.this, "请将以上信息填写完全", Toast.LENGTH_SHORT).show();
      return;
    }

    if (saveCheckBox.isChecked()) {
      loginPrefsEditor.putBoolean("saveIpConfig", true);
      loginPrefsEditor.putString(appConstant.IP_ADDRESS, ipAddr);
      loginPrefsEditor.putString(appConstant.PORT_STR, port);
    } else {
      loginPrefsEditor.clear();
    }
    loginPrefsEditor.commit();

    Intent intent = new Intent();
    intent.putExtra(appConstant.IP_ADDRESS, ipAddr);//设置参数ip
    intent.putExtra(appConstant.PORT_STR, port); //设置参数port
    setResult(RESULT_OK, intent);
    finish();
  }
}
