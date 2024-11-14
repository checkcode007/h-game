package com.z.dbes.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.reactor.IOReactorExceptionHandler;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BaseElasticsearchConfig {
    private static Logger log = LoggerFactory.getLogger(BaseElasticsearchConfig.class);

    private String clusterName;

    private String clusterNodes;

    private String clusterPorts;


    private static String schema = "http";
    private static boolean uniqueConnectTimeConfig = true;
    private static boolean uniqueConnectNumConfig = true;
    private int connectTimeOut = 20000;
    private int socketTimeOut = 60_000;
    private int connectionRequestTimeOut = 10000;
    private int maxConnectNum = 100;
    private int maxConnectPerRoute = 30;


    public String getSchema() {
        return schema;
    }

    public boolean getUniqueConnectTimeConfig() {
        return uniqueConnectTimeConfig;
    }

    public boolean getUniqueConnectNumConfig() {
        return uniqueConnectNumConfig;
    }



    @Bean("ElasticsearchClient")
    public ElasticsearchClient createClient () {
        String[] hosts = getClusterNodes().split(",");
        String[] ports = getClusterPorts().split(",");
        HttpHost[] httpHosts = new HttpHost[hosts.length];

        for (int i = 0; i < hosts.length; i++) {
            httpHosts[i] = new HttpHost(hosts[i], Integer.parseInt(ports[i]), schema);
        }
        RestClientBuilder builder = RestClient.builder(httpHosts);
        // 可选配置连接超时
        if (uniqueConnectTimeConfig) {
            setConnectTimeOutConfig(builder);
        }
        // 可选配置连接数量
        if (uniqueConnectNumConfig) {
            setMutiConnectConfig(builder);
        }
//        builder.setCompressionEnabled(true);
//        builder.setHttpClientConfigCallback(httpClientBuilder ->
//                httpClientBuilder.addInterceptorFirst(new GzipRequestInterceptor())
//        );
        RestClient restClient = builder.build();
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);
       log.info("ElasticsearchClient============" + getClusterNodes() + "===" + getClusterPorts()+"------>"+client);
        return client;
    }


    /**
     * 异步httpclient的连接延时配置
     */
    public void setConnectTimeOutConfig(RestClientBuilder builder) {
        builder.setRequestConfigCallback(
                config -> config.setConnectTimeout(connectTimeOut)
                        .setConnectionRequestTimeout(socketTimeOut)
                        .setSocketTimeout(connectionRequestTimeOut));

    }

    /**
     * 异步httpclient的连接数配置
     */
    public void setMutiConnectConfig(RestClientBuilder builder) {
        builder.setHttpClientConfigCallback(
                httpClientBuilder -> {
                    httpClientBuilder.setMaxConnTotal(maxConnectNum);
                    httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
                    List<Header> headers = new ArrayList<>(4);
                    headers.add(new BasicHeader("Connection", "keep-alive"));
                    headers.add(new BasicHeader("Keep-Alive", "720"));
                    httpClientBuilder.setDefaultHeaders(headers);
                    httpClientBuilder.setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE);
                    try {
                        DefaultConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
                        ioReactor.setExceptionHandler(new IOReactorExceptionHandler() {
                            @Override
                            public boolean handle(IOException e) {
                                log.debug("System may be unstable: IOReactor encountered a checked exception : " + e.getMessage());
                                log.debug("start setHttpClientConfigCallback handle IOException e printStackTrace");
                                e.printStackTrace();
                                log.debug("end setHttpClientConfigCallback handle IOException e printStackTrace");
                                // Return true to note this exception as handled, it will not be re-thrown
                                return true;
                            }
                            @Override
                            public boolean handle(RuntimeException e) {
                                log.debug("System may be unstable: IOReactor encountered a runtime exception : " + e.getMessage() + ",e is {}", e);
                                log.debug("start setHttpClientConfigCallback handle RuntimeException e printStackTrace ");
                                e.printStackTrace();
                                log.debug("end setHttpClientConfigCallback handle RuntimeException e printStackTrace ");
                                // Return true to note this exception as handled, it will not be re-thrown
                                return true;
                            }
                        });
                        httpClientBuilder.setConnectionManager(new PoolingNHttpClientConnectionManager(ioReactor));
                    } catch (IOReactorException e) {
                        throw new RuntimeException(e);
                    }
                    return httpClientBuilder;
                }
        );
    }



    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public String getClusterPorts() {
        return clusterPorts;
    }

    public void setClusterPorts(String clusterPorts) {
        this.clusterPorts = clusterPorts;
    }

}
