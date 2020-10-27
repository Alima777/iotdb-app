package com.iotdb.app.buttonActivity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iotdb.app.buttonActivity.exception.RESTfulException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RESTful {

  private Client client = ClientBuilder.newClient();
  private String ipAddr;
  private int port;
  private String device;

  public RESTful(String ipAddr, String port, String imei, String userName) {
    this.ipAddr = ipAddr;
    this.port = Integer.parseInt(port);
    this.device = String.format("root.%s.%s", imei, userName);
  }

  public void login() throws RESTfulException {
    Response response = client.target(String.format(appConstant.USER_LOGIN, ipAddr, port))
        .request(MediaType.APPLICATION_JSON).get();
    if (!response.readEntity(String.class).equals(appConstant.SUCCESSFUL_RESPONSE)) {
      throw new RESTfulException("Login in error");
    }
  }

  public void createTimeSeries(String measurement, String dataType, String Encoding)
      throws RESTfulException {
    JSONArray createArray = new JSONArray();
    JSONObject timeSeries = new JSONObject();
    StringBuilder timeSeriesStr = new StringBuilder();
    timeSeriesStr.append(device).append(".").append(measurement);
    timeSeries.put("timeSeries", timeSeriesStr.toString());
    timeSeries.put("dataType", dataType);
    timeSeries.put("encoding", Encoding);
    createArray.add(timeSeries);

    WebTarget target = client.target(String.format(appConstant.CREATE_URL, ipAddr, port));
    Response response = target.request(MediaType.APPLICATION_JSON)
        .post(Entity.entity(createArray, MediaType.APPLICATION_JSON));

    if (!response.readEntity(String.class).equals(appConstant.SUCCESSFUL_RESPONSE)) {
      throw new RESTfulException("cannot create timeSeries to " + timeSeriesStr);
    }
  }

  public void insertData(String measurement, long timestamp, Object value)
      throws RESTfulException {
    JSONArray insertArray = new JSONArray();
    JSONObject insertRow = new JSONObject();
    insertRow.put(appConstant.IS_NEED_INFER_TYPE, false);
    insertRow.put(appConstant.DEVICE_ID, device);
    JSONArray measurements = new JSONArray();
    measurements.add(measurement);
    insertRow.put(appConstant.MEASUREMENTS, measurements);
    insertRow.put(appConstant.TIMESTAMP, timestamp);
    JSONArray values = new JSONArray();
    values.add(value);
    insertRow.put(appConstant.VALUES, values);

    insertArray.add(insertRow);

    WebTarget target = client.target(String.format(appConstant.INSERT_URL, ipAddr, port));
    Response response = target.request(MediaType.APPLICATION_JSON)
        .post(Entity.entity(insertArray, MediaType.APPLICATION_JSON));
    if (!response.readEntity(String.class).equals(appConstant.SUCCESSFUL_RESPONSE)) {
      throw new RESTfulException("cannot insert data to " + device);
    }
  }

  /**
   * delete time series
   */
  public void deleteTimeSeriesTest(String timeSeriesStr) throws RESTfulException {
    JSONArray deleteArray = new JSONArray();
    deleteArray.add(timeSeriesStr);

    WebTarget target = client.target(String.format(appConstant.DELETE_URL, ipAddr, port));
    Response response = target.request(MediaType.APPLICATION_JSON)
        .post(Entity.entity(deleteArray, MediaType.APPLICATION_JSON));

    if (!response.readEntity(String.class).equals(appConstant.SUCCESSFUL_RESPONSE)) {
      throw new RESTfulException("cannot delete timeSeries " + timeSeriesStr);
    }
  }

}
