package com.cloudpulse.backend.service;

import com.cloudpulse.backend.dto.PrometheusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrometheusServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PrometheusService prometheusService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(prometheusService, "prometheusUrl", "http://localhost:9090");
    }

    private PrometheusResponse createMockResponse(String valueStr) {
        PrometheusResponse response = new PrometheusResponse();
        response.setStatus("success");
        
        PrometheusResponse.Data data = new PrometheusResponse.Data();
        data.setResultType("vector");
        
        PrometheusResponse.Result result = new PrometheusResponse.Result();
        List<Object> valList = new ArrayList<>();
        valList.add(1620000000L);
        valList.add(valueStr);
        result.setValue(valList);
        
        data.setResult(List.of(result));
        response.setData(data);
        return response;
    }

    @Test
    void testGetCpuUsage_Success() {
        PrometheusResponse mockRes = createMockResponse("0.456");
        when(restTemplate.getForObject(contains("system_cpu_usage"), eq(PrometheusResponse.class)))
                .thenReturn(mockRes);

        double cpu = prometheusService.getCpuUsage();
        assertEquals(45.6, cpu, 0.01);
    }

    @Test
    void testGetMemoryUsage_Success() {
        PrometheusResponse mockRes = createMockResponse("0.789");
        when(restTemplate.getForObject(contains("system_memory_usage"), eq(PrometheusResponse.class)))
                .thenReturn(mockRes);

        double mem = prometheusService.getMemoryUsage();
        assertEquals(78.9, mem, 0.01);
    }

    @Test
    void testGetRequestsPerSecond_Success() {
        PrometheusResponse mockRes = createMockResponse("150.5");
        when(restTemplate.getForObject(contains("http_server_requests_seconds_count"), eq(PrometheusResponse.class)))
                .thenReturn(mockRes);

        double rps = prometheusService.getRequestsPerSecond();
        assertEquals(150.5, rps, 0.01);
    }

    @Test
    void testGetErrorRate_Success() {
        PrometheusResponse mockRes = createMockResponse("2.3");
        when(restTemplate.getForObject(contains("error_rate"), eq(PrometheusResponse.class)))
                .thenReturn(mockRes);

        double err = prometheusService.getErrorRate();
        assertEquals(2.3, err, 0.01);
    }

    @Test
    void testGetMetric_ErrorResponse_ReturnsZero() {
        when(restTemplate.getForObject(anyString(), eq(PrometheusResponse.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        double cpu = prometheusService.getCpuUsage();
        assertEquals(0.0, cpu);
    }
}
