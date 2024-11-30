package com.z.client;

import com.z.client.net.WebSocketClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebSocketClientApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WebSocketClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        WebSocketClient client = new WebSocketClient("localhost", 8081);
        client.start();
    }
}
