package com.cloudpulse.backend.service;

import com.cloudpulse.backend.dto.PrometheusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
public class PrometheusService {

    private final RestTemplate restTemplate;

    @Value("${prometheus.url:http://localhost:9090}")
    private String prometheusUrl;

    public PrometheusService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getCpuUsage() {
        double val = queryMetric("system_cpu_usage");
        return val * 100.0; // system_cpu_usage is 0.0 to 1.0, convert to %
    }

    public double getMemoryUsage() {
        double val = queryMetric("system_memory_usage");
        return val * 100.0;
    }

    public double getRequestsPerSecond() {
        return queryMetric("rate(http_server_requests_seconds_count[1m])");
    }

    public double getErrorRate() {
        return queryMetric("error_rate");
    }

    private double queryMetric(String query) {
        try {
            String url = prometheusUrl + "/api/v1/query?query=" + java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8.name());
            PrometheusResponse response = restTemplate.getForObject(url, PrometheusResponse.class);
            if (response != null && "success".equals(response.getStatus()) && response.getData() != null) {
                var results = response.getData().getResult();
                if (results != null && !results.isEmpty()) {
                    var valueList = results.get(0).getValue();
                    if (valueList != null && valueList.size() >= 2) {
                        String valueStr = String.valueOf(valueList.get(1));
                        return Double.parseDouble(valueStr);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch metric from Prometheus for query: {}", query, e);
        }
        return 0.0;
    }
}
