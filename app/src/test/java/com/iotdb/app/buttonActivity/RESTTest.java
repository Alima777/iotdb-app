package com.iotdb.app.buttonActivity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RESTTest implements Serializable {

  static final String SUCCESSFUL_RESPONSE = "{\"result\":\"successful operation\"}";

  private Client client = ClientBuilder.newClient();
  private static final String USER_LOGIN = "http://%s:%s/user/login?username=root&password=root";
  private static final String CREATE_URL = "http://%s:%s/timeSeries";
  private static final String DELETE_URL = "http://%s:%s/timeSeries/delete";
  private static final String INSERT_URL = "http://%s:%s/insert";
  private static final String TEST_IP = "127.0.0.1";
  private static final String TEST_PORT = "8282";

  @Before
  public void login() {
    Response response = client.target(String.format(USER_LOGIN, TEST_IP, TEST_PORT))
        .request(MediaType.APPLICATION_JSON).get();
    Assert.assertEquals(SUCCESSFUL_RESPONSE, response.readEntity(String.class));
  }

  /**
   * create time series
   */
  @Test
  public void CreateTimeSeriesTest() {
    JSONArray createArray = new JSONArray();
    JSONObject timeSeries = new JSONObject();
    timeSeries.put("timeSeries", "root.sg.d1.s1");
    timeSeries.put("dataType", "INT64");
    timeSeries.put("encoding", "RLE");
    createArray.add(timeSeries);

    WebTarget target = client.target(String.format(CREATE_URL, TEST_IP, TEST_PORT));
    Response response = target.request(MediaType.APPLICATION_JSON)
        .post(Entity.entity(createArray, MediaType.APPLICATION_JSON));
    Assert.assertEquals(SUCCESSFUL_RESPONSE, response.readEntity(String.class));
  }

   /**
   * Insert data test
   */
  @Test
  public void InsertDataTest() {
    JSONArray insertArray = new JSONArray();
    JSONObject insertRow = new JSONObject();
    insertRow.put(appConstant.IS_NEED_INFER_TYPE, false);
    insertRow.put(appConstant.DEVICE_ID, "root.sg.d1");
    JSONArray measurements = new JSONArray();
    measurements.add("s1");
    insertRow.put(appConstant.MEASUREMENTS, measurements);
    insertRow.put(appConstant.TIMESTAMP, 10086);
    JSONArray values = new JSONArray();
    values.add(10086);
    insertRow.put(appConstant.VALUES, values);

    insertArray.add(insertRow);

    WebTarget target = client.target(String.format(INSERT_URL, TEST_IP, TEST_PORT));
    Response response = target.request(MediaType.APPLICATION_JSON)
        .post(Entity.entity(insertArray, MediaType.APPLICATION_JSON));
    Assert.assertEquals(SUCCESSFUL_RESPONSE, response.readEntity(String.class));
  }

  /**
   * delete time series
   */
  @Test
  public void DeleteTimeSeriesTest() {
    JSONArray deleteArray = new JSONArray();
    deleteArray.add("root.sg.d1.s1");

    WebTarget target = client.target(String.format(DELETE_URL, TEST_IP, TEST_PORT));
    Response response = target.request(MediaType.APPLICATION_JSON)
        .post(Entity.entity(deleteArray, MediaType.APPLICATION_JSON));
    Assert.assertEquals(SUCCESSFUL_RESPONSE, response.readEntity(String.class));
  }

}
