package org.opentsdb.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.opentsdb.client.request.QueryBuilder;
import org.opentsdb.client.request.SubQueries;
import org.opentsdb.client.response.SimpleHttpResponse;
import org.opentsdb.client.util.Aggregator;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * Created by shifeng on 2016/5/19.
 * MyProject
 */
public class QueryTest {
    @Test
    public void queryTest() throws IOException {
        HttpClientImpl client = new HttpClientImpl("http://168.61.12.113:4242");


        QueryBuilder builder = QueryBuilder.getInstance();
        SubQueries subQueries = new SubQueries();
        String zimsum = Aggregator.zimsum.toString();
        subQueries.addMetric("cpu.busy").addTag("endpoint", "168.61.2.47");
        long now = new Date().getTime() / 1000;
        builder.getQuery().addStart(1541004988).addEnd(now).addSubQuery(subQueries);
        System.out.println(builder.build());

        try {
            SimpleHttpResponse response = client.pushQueries(builder,
                    ExpectResponse.STATUS_CODE);
            String content = response.getContent();
            int statusCode = response.getStatusCode();
            if (statusCode == 200) {
                JSONArray jsonArray = JSON.parseArray(content);
                for (Object object : jsonArray) {
                    JSONObject json = (JSONObject) JSON.toJSON(object);
                    String dps = json.getString("dps");
                    Map<String, String> map = JSON.parseObject(dps, Map.class);
                    for (Map.Entry entry : map.entrySet()) {
                        System.out.println("Time:" + entry.getKey() + ",Value:" + entry.getValue());
                        Double.parseDouble(String.valueOf(entry.getValue()));
                    }
                }
            }
            //System.out.println(jsonArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
