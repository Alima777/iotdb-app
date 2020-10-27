package com.iotdb.app.buttonActivity;

public class appConstant {

  static final String PORT_STR = "port";
  static final String USERNAME_STR = "username";
  static final String PASSWORD_STR = "password";
  static final String IP_STR = "ip";
  static final String IP_ADDRESS = "ipAddr";
  static final String IMEI_STR = "imei";
  static final String DEFAULT_IP = "192.168.184.1";
  static final String DEFAULT_PORT = "8282";

  static final String IS_NEED_INFER_TYPE = "isNeedInferType";
  static final String DEVICE_ID = "deviceId";
  static final String MEASUREMENTS = "measurements";
  static final String TIMESTAMP = "timestamp";
  static final String VALUES = "values";

  static final String USER_LOGIN = "http://%s:%s/user/login?username=root&password=root";
  static final String CREATE_URL = "http://%s:%s/timeSeries";
  static final String DELETE_URL = "http://%s:%s/timeSeries/delete";
  static final String INSERT_URL = "http://%s:%s/insert";
  static final String SUCCESSFUL_RESPONSE = "{\"result\":\"successful operation\"}";
}
