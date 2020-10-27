package com.iotdb.app.buttonActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.iotdb.app.buttonActivity.exception.RESTfulException;
import java.util.List;

public class CollectActivity extends Activity implements View.OnClickListener {

  private MyBroadcastReceiver myBroadcastReceiver;
  //请求状态码
  private static int REQUEST_PERMISSION_CODE = 1;

  private boolean networkConn;
  private boolean enableTemperature = false;
  private boolean hasCreateTimeSeries = false;

  private CheckBox tempCheckBox;
  private Button startCollectBtn;
  private Button stopCollectBtn;
  private Button viewDataBtn;

  private String ipAddr;
  private String port;
  private String userName;
  private String phoneIMEI;
  private RESTful restful;

  private Handler mHandler = new Handler();
  private Runnable getDataThread;

  private int timeInterval;
  private SensorManager mSensorManager;
  private Sensor tempSensor;
  private float tempValue;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_collect);

    // 初始化控件
    initNetBroadcast();
    startCollectBtn = findViewById(R.id.startCollect);
    stopCollectBtn = findViewById(R.id.stopCollect);
    viewDataBtn = findViewById(R.id.viewData);
    stopCollectBtn.setEnabled(false);
    stopCollectBtn.setBackgroundResource(R.drawable.shape1);
    tempCheckBox = findViewById(R.id.temperature);

    // 获取连接信息
    Intent intent = getIntent();
    this.ipAddr = intent.getStringExtra(appConstant.IP_STR);
    this.port = intent.getStringExtra(appConstant.PORT_STR);
    this.userName = intent.getStringExtra(appConstant.USERNAME_STR);
    this.phoneIMEI = getIMIE();
    this.restful = new RESTful(ipAddr, port, phoneIMEI, userName);
    try {
      restful.login();
    } catch (RESTfulException e) {
      System.out.println(e.getMessage());
    }

    // 获取温度传感器
    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    List<Sensor> allSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
    for (Sensor sensor : allSensors) {
      if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) { // 环境温度
        tempSensor = sensor;
        tempCheckBox.setChecked(true);
        break;
      }
    }

    if (tempSensor != null) {
      mSensorManager
          .registerListener(temperatureListener, tempSensor, SensorManager.SENSOR_DELAY_GAME);
    } else {
      tempCheckBox.setClickable(false);
      tempCheckBox.setEnabled(false);
    }

    // 注册 button 活动
    startCollectBtn.setOnClickListener(this);
    stopCollectBtn.setOnClickListener(view -> {
      if (stopCollectBtn.isEnabled()) {
        if (!networkConn) {
          Toast.makeText(CollectActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
        } else {
          stopCollectBtn.setEnabled(false);
          stopCollectBtn.setBackgroundResource(R.drawable.shape1);
          startCollectBtn.setEnabled(true);
          startCollectBtn.setBackgroundResource(R.drawable.shape);
          Toast.makeText(CollectActivity.this, "停止采集数据", Toast.LENGTH_SHORT).show();
          mHandler.removeCallbacks(getDataThread);
        }
      }
    });

    viewDataBtn.setOnClickListener(view -> {
      if (!networkConn) {
        Toast.makeText(CollectActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
        //return;
      } else {
        // 给bnt1添加点击响应事件
        Intent intent1 = new Intent(CollectActivity.this, DisplayActivity.class);
        intent1.putExtra(appConstant.IP_STR, ipAddr);//设置参数ip
        intent1.putExtra(appConstant.USERNAME_STR, userName); //设置参数user
        intent1.putExtra(appConstant.IMEI_STR, phoneIMEI); //设置参数pswd
        //启动
        startActivity(intent1);
      }
    });

/*    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);*/
  }


  @Override
  public void onClick(View v) {
    EditText intervalEdit = findViewById(R.id.interval);
    timeInterval = Integer.parseInt(intervalEdit.getText().toString());

    if (startCollectBtn.isEnabled()) {
      if (networkConn) {
        mHandler.post(getDataThread = new Runnable() {
          @Override
          public void run() {
            if (!hasCreateTimeSeries) {
              initialTimeSeriesInfo();
              hasCreateTimeSeries = true;
            }
            if (tempCheckBox.isChecked()) {
              try {
                restful.insertData("temperature", System.currentTimeMillis(), tempValue);
              } catch (RESTfulException e) {
                System.out.println(e.getMessage());
              }
            }
            mHandler.postDelayed(this, timeInterval);
          }
        });
        Toast.makeText(CollectActivity.this, "开始采集数据", Toast.LENGTH_SHORT).show();
      }

      startCollectBtn.setEnabled(false);
      startCollectBtn.setBackgroundResource(R.drawable.shape1);
      stopCollectBtn.setEnabled(true);
      stopCollectBtn.setBackgroundResource(R.drawable.shape);
    } else {
      Toast.makeText(CollectActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    // 取消监听
    if (tempSensor != null) {
      mSensorManager.unregisterListener(temperatureListener);
    }
    unregisterReceiver(myBroadcastReceiver);
  }

  private void initNetBroadcast() {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    myBroadcastReceiver = new MyBroadcastReceiver();
    registerReceiver(myBroadcastReceiver, intentFilter);
  }

  private SensorEventListener temperatureListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      // 温度传感器返回当前的温度，单位是摄氏度（°C）
      tempValue = event.values[0];
    }

    // 当传感器精度发生改变时回调该方法
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
  };

  @SuppressLint("MissingPermission")
  private String getIMIE() {
    // 获取电话管理对象
    TelephonyManager mTelephonyManager = (TelephonyManager) this
        .getSystemService(Context.TELEPHONY_SERVICE);
    if (mTelephonyManager.getDeviceId() != null) {
      return mTelephonyManager.getDeviceId();
    } else {
      return Settings.Secure
          .getString(this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }
  }

  /**
   * TimeSeries: root.deviceId.userName.SensorName
   */
  public void initialTimeSeriesInfo() {
    // 创建时间序列
    if (enableTemperature) {
      try {
        restful.createTimeSeries("temperature", "FLOAT", "RLE");
      } catch (RESTfulException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
          Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
      if (networkInfo != null && networkInfo.isAvailable()) {
        Toast.makeText(context, "网络已连接", Toast.LENGTH_SHORT).show();
        networkConn = true;
      } else {
        Toast.makeText(context, "当前网络不可用，请检查网络连接", Toast.LENGTH_SHORT).show();
        networkConn = false;
      }
    }
  }
}