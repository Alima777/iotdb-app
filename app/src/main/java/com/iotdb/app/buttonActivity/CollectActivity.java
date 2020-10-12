package com.iotdb.app.buttonActivity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
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
import com.alibaba.fastjson.JSON;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectActivity extends Activity implements View.OnClickListener {

  //    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
  //    private String curTime = df.format(new Date());
  private long curTime = System.currentTimeMillis();
  private MyBroadcastReceiver myBroadcastReceiver;
  private int interval;

  //读写权限
  private static String[] PERMISSIONS_STORAGE = {
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.READ_PHONE_STATE,
      Manifest.permission.BODY_SENSORS
  };
  //请求状态码
  private static int REQUEST_PERMISSION_CODE = 1;
  private boolean endCollect = false;

  private float tempValue = 0;
  private ArrayList<Float> oriValue = new ArrayList<>(3);
  private ArrayList<Float> acceValue = new ArrayList<>(3);
  private float stepValue = 0;
  private float hrateValue = 0;
  private float humidValue = 0;
  private ArrayList<Float> gravValue = new ArrayList<>(3);
  private ArrayList<Float> gyroValue = new ArrayList<>(3);
  private ArrayList<Float> magValue = new ArrayList<>(3);
  private ArrayList<Double> gpsValue = new ArrayList<>(3);
  private float pressureValue = 0;
  private float lightValue = 0;

  private boolean enableTemperature = false;
  private boolean ori = false;
  private boolean acce = false;
  private boolean step = false;
  private boolean hrate = false;
  private boolean humid = false;
  private boolean grav = false;
  private boolean gyro = false;
  private boolean pressure = false;
  private boolean light = false;
  private boolean mag = false;
  private boolean gps = false;

  private Button startCollectBtn;
  private Button stopCollectBtn;
  private Button viewDataBtn;

  private CheckBox tempCheck;
  private CheckBox oriCheck;
  private CheckBox acceCheck;
  private CheckBox stepCheck;
  private CheckBox hrateCheck;
  private CheckBox humidCheck;
  private CheckBox gravCheck;
  private CheckBox gyroCheck;
  private CheckBox pressureCheck;
  private CheckBox lightCheck;
  private CheckBox magCheck;
  private CheckBox gpsCheck;

  private String ipAddr;
  private String port;
  private String user;
  private String password;

  private Handler mHandler = new Handler();
  private Runnable mRunnable;

  private boolean networkConn;
  private boolean createOrNot = true;

  private SensorManager mSensorManager;
  private String phoneIMEI;

  private SensorEventListener temperatureListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      float[] values = event.values;
      tempValue = values[0];
      //temperatureValue.setText(sb.toString());
    }

    // 当传感器精度发生改变时回调该方法
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
  };

  private SensorEventListener acceleratorListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      float[] values = event.values;
      StringBuilder sb = new StringBuilder();
      sb.append("X方向的加速度：");
      sb.append(values[0]);
      sb.append("\nY方向的加速度：");
      sb.append(values[1]);
      sb.append("\nZ方向的加速度：");
      sb.append(values[2]);
      acceValue.add(values[0]);
      acceValue.add(values[1]);
      acceValue.add(values[2]);
      //acceValue.setText(sb.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
  };

  private SensorEventListener orientationListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      float[] values = event.values;
      oriValue.add(values[0]);
      oriValue.add(values[1]);
      oriValue.add(values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
  };

  private SensorEventListener stepListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      float[] values = event.values;
      //sb.append(values[0]);
      stepValue = values[0];
    }

    // 当传感器精度发生改变时回调该方法
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
  };

  private SensorEventListener heartRateListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      float[] values = event.values;
      //sb.append(values[0]);
      hrateValue = values[0];
    }

    // 当传感器精度发生改变时回调该方法
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
  };

  private SensorEventListener humidListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      float[] values = event.values;
      humidValue = values[0];
    }

    // 当传感器精度发生改变时回调该方法
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
  };

  private SensorEventListener gravityListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      float[] values = event.values;
      //sb.append("\nX轴方向上的重力：");
      //sb.append(values[0]);
      //sb.append("\nY轴方向上的重力：");
      //sb.append(values[1]);
      //sb.append("\nZ轴方向上的重力：");
      //sb.append(values[2]);
      gravValue.add(values[0]);
      gravValue.add(values[1]);
      gravValue.add(values[2]);
    }

    // 当传感器精度发生改变时回调该方法
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
  };

  private SensorEventListener gyroListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      float[] values = event.values;
      gyroValue.add(values[0]);
      gyroValue.add(values[1]);
      gyroValue.add(values[2]);
    }

    // 当传感器精度发生改变时回调该方法
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
  };

  private SensorEventListener magnetListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      float[] values = event.values;
      magValue.add(values[0]);
      magValue.add(values[1]);
      magValue.add(values[2]);
    }

    // 当传感器精度发生改变时回调该方法
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
  };

  private SensorEventListener lightListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      float[] values = event.values;
      lightValue = values[0];
    }

    // 当传感器精度发生改变时回调该方法
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
  };

  private SensorEventListener pressureListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      float[] values = event.values;
      pressureValue = values[0];
    }

    // 当传感器精度发生改变时回调该方法
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
  };

  private LocationListener gpsListener = new LocationListener() {

    /**
     * 位置信息变化时触发
     */
    public void onLocationChanged(Location location) {

      Log.d("经度--->", String.valueOf(location.getLongitude()));
      Log.d("纬度--->", String.valueOf(location.getLatitude()));
      Log.d("海拔--->", String.valueOf(location.getAltitude()));
      gpsValue.add(location.getLongitude());
      gpsValue.add(location.getLatitude());
      gpsValue.add(location.getAltitude());
    }

    /**
     * GPS状态变化时触发
     */
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /**
     * GPS开启时触发
     */
    public void onProviderEnabled(String provider) {
    }

    /**
     * GPS禁用时触发
     */
    public void onProviderDisabled(String provider) {
    }
  };


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_collect);

    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    myBroadcastReceiver = new MyBroadcastReceiver();
    registerReceiver(myBroadcastReceiver, intentFilter);

    startCollectBtn = findViewById(R.id.startCollect);
    stopCollectBtn = findViewById(R.id.stopCollect);
    viewDataBtn = findViewById(R.id.viewData);
    stopCollectBtn.setEnabled(false);
    stopCollectBtn.setBackgroundResource(R.drawable.shape1);

    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    Criteria criteria = new Criteria();
    criteria.setAccuracy(Criteria.ACCURACY_FINE);  //高精度
    criteria.setAltitudeRequired(true);  //包含高度信息
    criteria.setBearingRequired(true); //包含方位信息
    criteria.setSpeedRequired(true); //包含速度信息
    String bestProvider = locationManager.getBestProvider(criteria, true);
    // 获取位置信息
    // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
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

    Location location = locationManager.getLastKnownLocation(bestProvider);
    //updateView(location);
    // 监听状态
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, gpsListener);

    tempCheck = findViewById(R.id.temperature);
    oriCheck = findViewById(R.id.orientation);
    acceCheck = findViewById(R.id.accelerometer);
    stepCheck = findViewById(R.id.step);
    hrateCheck = findViewById(R.id.heartRate);
    humidCheck = findViewById(R.id.humid);
    gravCheck = findViewById(R.id.gravity);
    gyroCheck = findViewById(R.id.gyro);
    magCheck = findViewById(R.id.magnet);
    lightCheck = findViewById(R.id.light);
    pressureCheck = findViewById(R.id.pressure);
    gpsCheck = findViewById(R.id.gps);

    Intent intent = getIntent();
    ipAddr = intent.getStringExtra(appConstant.IP_STR);
    port = intent.getStringExtra(appConstant.PORT_STR);
    user = intent.getStringExtra(appConstant.USERNAME_STR);
    password = intent.getStringExtra(appConstant.PASSWORD_STR);
    Log.d("IP 地址 --->", ipAddr);
    Log.d("端口号 --->", port);
    // 获取电话管理对象
    TelephonyManager mTelephonyManager = (TelephonyManager) this
        .getSystemService(Context.TELEPHONY_SERVICE);

    if (mTelephonyManager.getDeviceId() != null) {
      phoneIMEI = mTelephonyManager.getDeviceId();
    } else {
      phoneIMEI = Settings.Secure
          .getString(this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    Log.d("本机 IMEI --->", phoneIMEI);
    // String phoneSerial = Build.SERIAL;
    // ID : 修订版本列表???
    String phoneID = Build.ID;
    Log.d("本机 ID --->", phoneID);

    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    List<Sensor> allSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
    for (Sensor sen : allSensors) {
      switch (sen.getType()) {
        case Sensor.TYPE_ACCELEROMETER://加速度传感器
          acce = true;
          acceCheck.setChecked(true);  //checkbox默认勾选
          break;
        case Sensor.TYPE_STEP_COUNTER://计步传感器
          step = true;
          stepCheck.setChecked(true);
          break;
        case Sensor.TYPE_ORIENTATION:// 方向
          ori = true;
          oriCheck.setChecked(true);
          break;
        case Sensor.TYPE_HEART_RATE:  //心率传感器
          hrate = true;
          hrateCheck.setChecked(true);
          break;
        case Sensor.TYPE_RELATIVE_HUMIDITY: // 相对湿度
          humid = true;
          humidCheck.setChecked(true);
          break;
        case Sensor.TYPE_AMBIENT_TEMPERATURE: // 环境温度
          enableTemperature = true;
          tempCheck.setChecked(true);
          break;
        case Sensor.TYPE_GRAVITY: // 重力
          grav = true;
          gravCheck.setChecked(true);
          break;
        case Sensor.TYPE_GYROSCOPE:  //陀螺仪传感器
          gyro = true;
          gyroCheck.setChecked(true);
          break;
        case Sensor.TYPE_LIGHT:  //光线传感线
          light = true;
          lightCheck.setChecked(true);
          break;
        case Sensor.TYPE_PRESSURE:  //压力传感线
          pressure = true;
          pressureCheck.setChecked(true);
          break;
        case Sensor.TYPE_MAGNETIC_FIELD:  // 磁场
          mag = true;
          magCheck.setChecked(true);
          break;
        default:
          break;
      }
    }

    if (!acce) {
      acceCheck.setClickable(false);
      acceCheck.setEnabled(false);
    }
    if (!enableTemperature) {
      tempCheck.setClickable(false);
      tempCheck.setEnabled(false);
    }
    if (!hrate) {
      hrateCheck.setClickable(false);
      hrateCheck.setEnabled(false);
    }
    if (!humid) {
      humidCheck.setClickable(false);
      humidCheck.setEnabled(false);
    }
    if (!step) {
      stepCheck.setClickable(false);
      stepCheck.setEnabled(false);
    }
    if (!grav) {
      gravCheck.setClickable(false);
      gravCheck.setEnabled(false);
    }
    if (!gyro) {
      gyroCheck.setClickable(false);
      gyroCheck.setEnabled(false);
    }
    if (!light) {
      lightCheck.setClickable(false);
      lightCheck.setEnabled(false);
    }
    if (!pressure) {
      pressureCheck.setClickable(false);
      pressureCheck.setEnabled(false);
    }
    if (!mag) {
      magCheck.setClickable(false);
      magCheck.setEnabled(false);
    }
    if (!ori) {
      oriCheck.setClickable(false);
      oriCheck.setEnabled(false);
    }

    if (!isOPen(this)) {
      openGPS(this);
    }
    gpsCheck.setChecked(true);

    startCollectBtn.setOnClickListener(this);

    stopCollectBtn.setOnClickListener(view -> {
      if (stopCollectBtn.isEnabled()) {
        if (!networkConn) {
          Toast.makeText(CollectActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
          //return;
        } else {
          stopCollectBtn.setEnabled(false);
          stopCollectBtn.setBackgroundResource(R.drawable.shape1);
          startCollectBtn.setEnabled(true);
          startCollectBtn.setBackgroundResource(R.drawable.shape);
          Toast.makeText(CollectActivity.this, "停止采集数据", Toast.LENGTH_SHORT).show();
          endCollect = true;
          mHandler.removeCallbacks(mRunnable);
          Log.d("停止", endCollect ? "yes" : "no");
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
        intent1.putExtra(appConstant.USERNAME_STR, user); //设置参数user
        intent1.putExtra(appConstant.IMEI_STR, phoneIMEI); //设置参数pswd
        //启动
        startActivity(intent1);
      }
    });

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
  }

  @Override
  public void onClick(View v) {
    endCollect = false;
    EditText intervalEdit = findViewById(R.id.interval);
    interval = Integer.parseInt(intervalEdit.getText().toString());

    if (startCollectBtn.isEnabled()) {
      if (networkConn) {
        mHandler.post(mRunnable = new Runnable() {
          @Override
          public void run() {
            if (createOrNot) {
              initialTimeSeriesInfo();
              createOrNot = false;
            }
            curTime = System.currentTimeMillis();
            /**
             * Map<String, Float> values = new HashMap<>();
             * values.put("s3", 0.1f);
             * RestfulPoint point = new RestfulPoint("gou", "aaa", "phone2", 10L, values);
             */
            if (tempCheck.isChecked()) {
              Map<String, Float> values = new HashMap<>();
              values.put("temperature", tempValue);
              RestfulPoint point = new RestfulPoint(user, password, phoneIMEI, curTime, values);
              RestfulPoint.insertData(ipAddr, port, point);
            }
            if (acceCheck.isChecked()) {
              Map<String, Float> values = new HashMap<>();
              values.put("accelerometer", acceValue.get(0));
              values.put("accelerometer_y", acceValue.get(1));
              values.put("accelerometer_z", acceValue.get(2));
              RestfulPoint point = new RestfulPoint(user, password, phoneIMEI, curTime, values);
              RestfulPoint.insertData(ipAddr, port, point);
            }
            if (oriCheck.isChecked()) {
              Map<String, Float> values = new HashMap<>();
              values.put("orient_x", oriValue.get(0));
              values.put("orient_y", oriValue.get(1));
              values.put("orient_z", oriValue.get(2));
              RestfulPoint point = new RestfulPoint(user, password, phoneIMEI, curTime, values);
              RestfulPoint.insertData(ipAddr, port, point);
            }
            if (stepCheck.isChecked()) {
              Map<String, Float> values = new HashMap<>();
              values.put("step", stepValue);
              RestfulPoint point = new RestfulPoint(user, password, phoneIMEI, curTime, values);
              RestfulPoint.insertData(ipAddr, port, point);
            }
            if (humidCheck.isChecked()) {
              Map<String, Float> values = new HashMap<>();
              values.put("humid", humidValue);
              RestfulPoint point = new RestfulPoint(user, password, phoneIMEI, curTime, values);
              RestfulPoint.insertData(ipAddr, port, point);
            }
            if (hrateCheck.isChecked()) {
              Map<String, Float> values = new HashMap<>();
              values.put("heartrate", hrateValue);
              RestfulPoint point = new RestfulPoint(user, password, phoneIMEI, curTime, values);
              RestfulPoint.insertData(ipAddr, port, point);
            }
            if (gyroCheck.isChecked()) {
              Map<String, Float> values = new HashMap<>();
              values.put("gyro_x", gyroValue.get(0));
              values.put("gyro_y", gyroValue.get(1));
              values.put("gyro_z", gyroValue.get(2));
              RestfulPoint point = new RestfulPoint(user, password, phoneIMEI, curTime, values);
              RestfulPoint.insertData(ipAddr, port, point);
            }
            if (gravCheck.isChecked()) {
              Map<String, Float> values = new HashMap<>();
              values.put("gravity_x", gravValue.get(0));
              values.put("gravity_y", gravValue.get(1));
              values.put("gravity_z", gravValue.get(2));
              RestfulPoint point = new RestfulPoint(user, password, phoneIMEI, curTime, values);
              RestfulPoint.insertData(ipAddr, port, point);
            }
            if (lightCheck.isChecked()) {
              Map<String, Float> values = new HashMap<>();
              values.put("light", lightValue);
              RestfulPoint point = new RestfulPoint(user, password, phoneIMEI, curTime, values);
              RestfulPoint.insertData(ipAddr, port, point);
            }
            if (pressureCheck.isChecked()) {
              Map<String, Float> values = new HashMap<>();
              values.put("pressure", pressureValue);
              RestfulPoint point = new RestfulPoint(user, password, phoneIMEI, curTime, values);
              RestfulPoint.insertData(ipAddr, port, point);
            }
            if (magCheck.isChecked()) {
              Map<String, Float> values = new HashMap<>();
              values.put("magnet_x", magValue.get(0));
              values.put("magnet_y", magValue.get(1));
              values.put("magnet_z", magValue.get(2));
              RestfulPoint point = new RestfulPoint(user, password, phoneIMEI, curTime, values);
              RestfulPoint.insertData(ipAddr, port, point);
            }
            if (gpsCheck.isChecked()) {
              Map<String, Float> values = new HashMap<>();
              double gps0 = gpsValue.get(0);
              double gps1 = gpsValue.get(1);
              double gps2 = gpsValue.get(2);
              values.put("longitude", (float) gps0);
              values.put("latitude", (float) gps1);
              values.put("altitude", (float) gps2);
              RestfulPoint point = new RestfulPoint(user, password, phoneIMEI, curTime, values);
              RestfulPoint.insertData(ipAddr, port, point);
            }
            mHandler.postDelayed(this, interval);
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
  protected void onResume() {
    super.onResume();
    // 为加速度传感器注册监听器
    if (acce) {
      mSensorManager.registerListener(acceleratorListener,
          mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
          SensorManager.SENSOR_DELAY_GAME);
    }
    // 为温度传感器注册监听器
    if (enableTemperature) {
      mSensorManager.registerListener(temperatureListener,
          mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE),
          SensorManager.SENSOR_DELAY_GAME);
    }
    // 为方向传感器注册监听器
    if (ori) {
      mSensorManager.registerListener(orientationListener,
          mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
          SensorManager.SENSOR_DELAY_GAME);
    }
    // 为陀螺仪传感器注册监听器
    if (gyro) {
      mSensorManager
          .registerListener(gyroListener,
              mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
              SensorManager.SENSOR_DELAY_GAME);
    }
    // 为磁场传感器注册监听器
    if (mag) {
      mSensorManager.registerListener(magnetListener,
          mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
          SensorManager.SENSOR_DELAY_GAME);
    }
    // 为重力传感器注册监听器
    if (grav) {
      mSensorManager
          .registerListener(gravityListener,
              mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
              SensorManager.SENSOR_DELAY_GAME);
    }
    // 为光传感器注册监听器
    if (light) {
      mSensorManager
          .registerListener(lightListener, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
              SensorManager.SENSOR_DELAY_GAME);
    }
    // 为压力传感器注册监听器
    if (pressure) {
      mSensorManager
          .registerListener(pressureListener,
              mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
              SensorManager.SENSOR_DELAY_GAME);
    }
    if (humid) {
      mSensorManager.registerListener(humidListener,
          mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY),
          SensorManager.SENSOR_DELAY_GAME);
    }
    if (hrate) {
      mSensorManager.registerListener(heartRateListener,
          mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE),
          SensorManager.SENSOR_DELAY_GAME);
    }
    if (step) {
      mSensorManager
          .registerListener(stepListener,
              mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
              SensorManager.SENSOR_DELAY_GAME);
    }

  }

  @Override
  protected void onStop() {
    super.onStop();
    // 取消监听
    if (enableTemperature) {
      mSensorManager.unregisterListener(temperatureListener);
    }
    if (acce) {
      mSensorManager.unregisterListener(acceleratorListener);
    }
    if (ori) {
      mSensorManager.unregisterListener(orientationListener);
    }
    if (step) {
      mSensorManager.unregisterListener(stepListener);
    }
    if (gyro) {
      mSensorManager.unregisterListener(gyroListener);
    }
    if (grav) {
      mSensorManager.unregisterListener(gravityListener);
    }
    if (humid) {
      mSensorManager.unregisterListener(humidListener);
    }
    if (hrate) {
      mSensorManager.unregisterListener(heartRateListener);
    }
    if (light) {
      mSensorManager.unregisterListener(lightListener);
    }
    if (pressure) {
      mSensorManager.unregisterListener(pressureListener);
    }
    if (mag) {
      mSensorManager.unregisterListener(magnetListener);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(myBroadcastReceiver);
  }

  public void initialTimeSeriesInfo() {
    /**
     * imei=phone2&sensor=s3&user=gou&password=aaa
     */
    String tmpString = "imei=%s&sensor=%s&user=%s&password=%s";

    System.out.println("initial here!!!");
    // 创建时间序列
    if (enableTemperature) {
      String param = String.format(tmpString, phoneIMEI, "temperature", user, password);
      RestfulPoint.register(ipAddr, port, param);
    }
    if (acce) {
      String param = String.format(tmpString, phoneIMEI, "accelerometer", user, password);
      RestfulPoint.register(ipAddr, port, param);
      String param1 = String.format(tmpString, phoneIMEI, "accelerometer_y", user, password);
      RestfulPoint.register(ipAddr, port, param1);
      String param2 = String.format(tmpString, phoneIMEI, "accelerometer_z", user, password);
      RestfulPoint.register(ipAddr, port, param2);
    }
    if (ori) {
      String param = String.format(tmpString, phoneIMEI, "orient_x", user, password);
      RestfulPoint.register(ipAddr, port, param);
      String param1 = String.format(tmpString, phoneIMEI, "orient_y", user, password);
      RestfulPoint.register(ipAddr, port, param1);
      String param2 = String.format(tmpString, phoneIMEI, "orient_z", user, password);
      RestfulPoint.register(ipAddr, port, param2);
    }
    if (step) {
      String param = String.format(tmpString, phoneIMEI, "step", user, password);
      RestfulPoint.register(ipAddr, port, param);
    }
    if (hrate) {
      String param = String.format(tmpString, phoneIMEI, "heartrate", user, password);
      RestfulPoint.register(ipAddr, port, param);
    }
    if (humid) {
      String param = String.format(tmpString, phoneIMEI, "humid", user, password);
      RestfulPoint.register(ipAddr, port, param);
    }
    if (grav) {
      String param = String.format(tmpString, phoneIMEI, "gravity_x", user, password);
      RestfulPoint.register(ipAddr, port, param);
      String param1 = String.format(tmpString, phoneIMEI, "gravity_y", user, password);
      RestfulPoint.register(ipAddr, port, param1);
      String param2 = String.format(tmpString, phoneIMEI, "gravity_z", user, password);
      RestfulPoint.register(ipAddr, port, param2);
    }
    if (gyro) {
      String param = String.format(tmpString, phoneIMEI, "gyro_x", user, password);
      RestfulPoint.register(ipAddr, port, param);
      String param1 = String.format(tmpString, phoneIMEI, "gyro_y", user, password);
      RestfulPoint.register(ipAddr, port, param1);
      String param2 = String.format(tmpString, phoneIMEI, "gyro_z", user, password);
      RestfulPoint.register(ipAddr, port, param2);
    }
    if (pressure) {
      String param = String.format(tmpString, phoneIMEI, "pressure", user, password);
      RestfulPoint.register(ipAddr, port, param);
    }
    if (light) {
      String param = String.format(tmpString, phoneIMEI, "light", user, password);
      RestfulPoint.register(ipAddr, port, param);
    }
    if (mag) {
      String param = String.format(tmpString, phoneIMEI, "magnet_x", user, password);
      RestfulPoint.register(ipAddr, port, param);
      String param1 = String.format(tmpString, phoneIMEI, "magnet_y", user, password);
      RestfulPoint.register(ipAddr, port, param1);
      String param2 = String.format(tmpString, phoneIMEI, "magnet_z", user, password);
      RestfulPoint.register(ipAddr, port, param2);
    }
    String param = String.format(tmpString, phoneIMEI, "longitude", user, password);
    RestfulPoint.register(ipAddr, port, param);
    String param1 = String.format(tmpString, phoneIMEI, "latitude", user, password);
    RestfulPoint.register(ipAddr, port, param1);
    String param2 = String.format(tmpString, phoneIMEI, "altitude", user, password);
    RestfulPoint.register(ipAddr, port, param2);
  }

  public static boolean isOPen(final Context context) {
    LocationManager locationManager
        = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
    boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
    boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    if (gps || network) {
      return true;
    }
    return false;
  }

  public static void openGPS(Context context) {
    Intent GPSIntent = new Intent();
    GPSIntent.setClassName("com.android.settings",
        "com.android.settings.widget.SettingsAppWidgetProvider");
    GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
    GPSIntent.setData(Uri.parse("custom:3"));
    try {
      PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
    } catch (PendingIntent.CanceledException e) {
      Log.d("Error while opening GPS", e.getMessage());
      e.printStackTrace();
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


class RestfulPoint implements Serializable {

  private static final long serialVersionUID = 1L;
  private String user;
  private String password;
  private String IMEI;
  private long time;
  private Map<String, Float> values;

  public RestfulPoint(String user, String password, String IMEI, long time,
      Map<String, Float> values) {
    this.user = user;
    this.password = password;
    this.IMEI = IMEI;
    this.time = time;
    this.values = values;
  }

  /**
   * write data point
   */
  public static void insertData(String ip, String port, RestfulPoint point) {
    String urlTmp = "http://%s:%s/api/ios/addDatum";
    String url = String.format(urlTmp, ip, port);
    String body = JSON.toJSONString(point);
    try {
      sendPostJson(url, body);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void sendPostJson(String urls, String body) throws IOException {
    URL url = new URL(urls);
    URLConnection connection = url.openConnection();
    connection.setRequestProperty("accept", "application/json");
    connection.setRequestProperty("connection", "Keep-Alive");
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setRequestProperty("user-agent",
        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
    connection.setDoInput(true);
    connection.setDoOutput(true);

    try (PrintWriter out = new PrintWriter(connection.getOutputStream())) {
      out.print(body);
      out.flush();
    }

    try (BufferedReader in = new BufferedReader(
        new InputStreamReader(connection.getInputStream()))) {
      StringBuilder result = new StringBuilder();
      String line;
      while ((line = in.readLine()) != null) {
        result.append(line);
      }
      System.out.println("responce: " + result);
    }
  }

  /**
   * register time series
   */
  public static void register(String ip, String port, String param) {
    String url = "http://%s:%s/api/ios/registerPhone";
    url = String.format(url, ip, port);
    try {
      String response = sendPostEncoded(url, param);
      System.out.println(response);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String sendPostEncoded(String url, String param) throws IOException {
    PrintWriter printWriter = null;
    BufferedReader bufferedReader = null;
    String result = "";
    try {
      URL realUrl = new URL(url);
      // 打开和URL之间的连接
      URLConnection conn = realUrl.openConnection();
      // 设置通用的请求属性
      conn.setRequestProperty("accept", "*/*");
      conn.setRequestProperty("connection", "Keep-Alive");
      conn.setRequestProperty("user-agent",
          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
      // 发送POST请求必须设置如下两行
      conn.setDoOutput(true);
      conn.setDoInput(true);
      // 获取URLConnection对象对应的输出流
      printWriter = new PrintWriter(conn.getOutputStream());
      // 发送请求参数
      if (param != null) {
        printWriter.print(param);
      }
      // flush输出流的缓冲
      printWriter.flush();
      // 定义BufferedReader输入流来读取URL的响应
      bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        result += line;
      }
    } finally { //使用finally块来关闭输出流、输入流
      if (printWriter != null) {
        printWriter.close();
      }
      if (bufferedReader != null) {
        bufferedReader.close();
      }
    }
    return result;
  }
}

