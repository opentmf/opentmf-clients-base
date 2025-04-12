package org.opentmf.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Random;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.opentmf.commons.util.JacksonUtil;
import org.opentmf.mockserver.callback.DynamicPostCallback;

public class MockServerHelper {

  public static void addingMockServerData(String path, int count) {
    List<String> example = List.of("test1", "test2", "test3", "test4", "test5");
    for (int i = 0; i < count; i++) {
      ObjectNode node = getTestData();
      node.put("orderNumber", i);
      node.put("even", i % 2 == 0);
      ArrayNode arrayNode = JacksonUtil.getDefaultObjectMapper().createArrayNode();
      ObjectNode nodeCharacteristic = JacksonUtil.getDefaultObjectMapper().createObjectNode();
      nodeCharacteristic.put("key", "name");
      nodeCharacteristic.put("value", example.get(i % 5));
      arrayNode.add(nodeCharacteristic);
      node.set("characteristics", arrayNode);
      addDataToMockServerCache(path, node);
    }
  }

  public static ObjectNode getTestData() {
    ObjectNode node = JacksonUtil.getDefaultObjectMapper().createObjectNode();
    node.put("description", "test_getAll");
    node.put("name", "test");
    node.put("randomNumber", new Random().nextInt(5));
    return node;
  }

  private static String addDataToMockServerCache(String path, ObjectNode json) {
    HttpRequest httpRequest =
        new HttpRequest().withPath(path).withBody(JacksonUtil.objectToJson(json));
    DynamicPostCallback dynamicPostCallback = new DynamicPostCallback();
    HttpResponse response = dynamicPostCallback.handle(httpRequest);
    assertEquals(200, response.getStatusCode());
    JsonNode responseJson = JacksonUtil.jsonToTree(response.getBodyAsString());
    assertNotNull(responseJson.get("id").asText());
    return responseJson.get("id").asText();
  }
}
