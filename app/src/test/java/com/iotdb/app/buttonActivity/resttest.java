package com.iotdb.app.buttonActivity;

import com.alibaba.fastjson.JSON;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class resttest implements Serializable {

  private static final long serialVersionUID = 1L;
  private String user;
  private String password;
  private String imei;
  private long time;
  private Map<String, Float> values = new HashMap<>();

  public resttest(String user, String password, String imei, long time, Map<String, Float> values) {
    this.user = user;
    this.password = password;
    this.imei = imei;
    this.time = time;
    this.values = values;
  }

  public static void main(String[] args) {
//    register();
    insertData();
  }


  /**
   * register time series
   */
  public static void register() {
    String url = "http://183.173.79.221:8083/api/ios/registerPhone";
    try {
      String response = sendPostEncoded(url, "imei=phone2&sensor=s3&user=gou&password=aaa");
      System.out.println(response);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * write data point
   */
  public static void insertData() {
    Map<String, Float> values = new HashMap<>();
    values.put("s3", 0.1f);
    resttest point = new resttest("gou", "aaa", "phone2", 10L, values);
    String url = "http://183.173.79.221:8083/api/ios/addDatum";
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


  public static String sendPostEncoded(String url, String param) throws IOException {
    PrintWriter out = null;
    BufferedReader in = null;
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
      out = new PrintWriter(conn.getOutputStream());
      // 发送请求参数
      if (param != null) {
        out.print(param);
      }
      // flush输出流的缓冲
      out.flush();
      // 定义BufferedReader输入流来读取URL的响应
      in = new BufferedReader(
          new InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        result += line;
      }
    } catch (Exception e) {
      throw e;
    }
    //使用finally块来关闭输出流、输入流
    finally {
      try {
        if (out != null) {
          out.close();
        }
        if (in != null) {
          in.close();
        }
      } catch (IOException e) {
        throw e;
      }
    }
    return result;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public Map<String, Float> getValues() {
    return values;
  }

  public void setValues(Map<String, Float> values) {
    this.values = values;
  }
}
