package com.onlydevs.bookstore.conf;

import com.onlydevs.bookstore.endpoint.rest.client.ApiClient;

public class TestUtils {
  public static ApiClient createApiClient(int port) {
    ApiClient client = new ApiClient();
    client.setScheme("http");
    client.setHost("localhost");
    client.setPort(port);
    return client;
  }
}
