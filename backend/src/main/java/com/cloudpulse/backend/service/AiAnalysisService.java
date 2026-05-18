package com.cloudpulse.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiAnalysisService {

    private final PrometheusService prometheusService;

    /**
     * Generates a precise, metrics-grounded root-cause summary for an incident,
     * carefully separating facts from suggestions as mandated by AGENTS.md rules.
     */
    public String generateIncidentAnalysis(String serviceName, String metricType, Double value) {
        log.info("Generating AI root-cause analysis for service={} metricType={} value={}", serviceName, metricType, value);
        
        if ("CPU".equalsIgnoreCase(metricType)) {
            return String.format(
                "[FACT]\n" +
                "- CPU utilization has spiked to %.2f%% on %s, breaching the critical SLA threshold of 80%%.\n\n" +
                "[SUGGESTION]\n" +
                "- The high CPU load is likely caused by computational bottlenecks, thread pool exhaustion, or an unthrottled request spike.\n" +
                "- This might indicate active load spikes or inefficient loops inside active microservice endpoints.\n\n" +
                "[RECOMMENDED ACTION]\n" +
                "- Inspect active system threads to identify blocking execution contexts.\n" +
                "- Scale replica instances to distribute load, or deploy a rate limiter on the target routes.",
                value, serviceName
            );
        } else if ("MEMORY".equalsIgnoreCase(metricType)) {
            return String.format(
                "[FACT]\n" +
                "- System memory allocation is at %.2f%% on %s, exceeding the critical limit of 90%%.\n\n" +
                "[SUGGESTION]\n" +
                "- Near-heap exhaustion indicates potential memory leaks, large object cache retention, or undersized JVM container allocations.\n\n" +
                "[RECOMMENDED ACTION]\n" +
                "- Capture a heap dump immediately for analysis using Eclipse Memory Analyzer (MAT).\n" +
                "- Review cache eviction policies and trigger manual Garbage Collection (GC) if necessary.",
                value, serviceName
            );
        } else if ("ERROR_RATE".equalsIgnoreCase(metricType)) {
            return String.format(
                "[FACT]\n" +
                "- Monitored HTTP error rate has spiked to %.2f%% on %s.\n\n" +
                "[SUGGESTION]\n" +
                "- Client requests are receiving HTTP 500 Internal Server Errors.\n" +
                "- This is typically due to unhandled controller exceptions, database connection timeouts, or downstream service outages.\n\n" +
                "[RECOMMENDED ACTION]\n" +
                "- Review backend application stack traces for unexpected RuntimeExceptions.\n" +
                "- Validate database pool availability and verify downstream network path integrity.",
                value, serviceName
            );
        } else {
            return String.format(
                "[FACT]\n" +
                "- Node anomaly detected on service %s: %s stands at %.2f.\n\n" +
                "[SUGGESTION]\n" +
                "- Performance characteristics diverge from standard baseline limits.\n\n" +
                "[RECOMMENDED ACTION]\n" +
                "- Monitor metric logs closely and check trace dependencies.",
                serviceName, metricType, value
            );
        }
    }

    /**
     * Resolves natural language user queries about system telemetry by retrieving
     * live Prometheus metrics.
     */
    public String handleTelemetryChat(String userQuery) {
        log.info("Processing AI Chat query: {}", userQuery);
        String query = userQuery.toLowerCase();

        // Get live telemetry for contextual grounding
        double cpu = prometheusService.getCpuUsage();
        double memory = prometheusService.getMemoryUsage();
        double rps = prometheusService.getRequestsPerSecond();
        double errorRate = prometheusService.getErrorRate();

        if (query.contains("cpu") || query.contains("processor") || query.contains("load")) {
            return String.format(
                "[FACT]\n" +
                "- Live monitored CPU utilization is currently at %.2f%%.\n\n" +
                "[SUGGESTION]\n" +
                "%s\n" +
                "- Normal operations usually stay under 60%%. If you are experiencing spikes, check for concurrent load-injector stress tests.",
                cpu,
                cpu >= 80 ? "- CPU is currently in a CRITICAL state. Node performance may degrade." : "- CPU is currently operating in a HEALTHY state."
            );
        } else if (query.contains("memory") || query.contains("ram") || query.contains("heap")) {
            return String.format(
                "[FACT]\n" +
                "- Live memory allocation is currently at %.2f%%.\n\n" +
                "[SUGGESTION]\n" +
                "%s\n" +
                "- JVM GC routines will auto-reclaim memory. Monitor this value for continuous growth which indicating leaks.",
                memory,
                memory >= 90 ? "- Memory usage is near exhaustion. Action is required immediately." : "- Memory allocation is within normal parameters."
            );
        } else if (query.contains("error") || query.contains("500") || query.contains("failed") || query.contains("stability")) {
            return String.format(
                "[FACT]\n" +
                "- Current HTTP transaction error rate is %.2f%%.\n\n" +
                "[SUGGESTION]\n" +
                "%s\n" +
                "- Any error rate above 1.0%% creates automated system alerts to prevent service outages.",
                errorRate,
                errorRate >= 1.0 ? "- High error rates detected. Node is responding with failure codes." : "- Error rate is healthy and well within limits (0.0%)."
            );
        } else if (query.contains("traffic") || query.contains("rps") || query.contains("request") || query.contains("throughput")) {
            return String.format(
                "[FACT]\n" +
                "- Live server request rate stands at %.2f requests per second (RPS).\n\n" +
                "[SUGGESTION]\n" +
                "- This traffic represents live transaction volume. If RPS surges past standard bounds, evaluate if it is a DDoS attempt or standard marketing-event load.",
                rps
            );
        } else if (query.contains("health") || query.contains("status") || query.contains("system") || query.contains("overview")) {
            boolean allHealthy = cpu < 80 && memory < 90 && errorRate < 1.0;
            return String.format(
                "[FACT]\n" +
                "- Monitored System Telemetry Overview:\n" +
                "  * CPU Utilization: %.2f%%\n" +
                "  * Memory Allocation: %.2f%%\n" +
                "  * HTTP Error Rate: %.2f%%\n" +
                "  * Request Rate: %.2f RPS\n\n" +
                "[SUGGESTION]\n" +
                "- Status: %s\n" +
                "- Suggestions: %s",
                cpu, memory, errorRate, rps,
                allHealthy ? "SYSTEM FULLY OPERATIONAL" : "DEGRADED PERFORMANCE ALERT",
                allHealthy ? "All key node indices are within safe boundaries." : "Some metrics have breached SLA warning limits. Check the Incident log immediately."
            );
        } else {
            return "[FACT]\n" +
                   "- You have accessed the CloudPulse AI Command Assistant.\n\n" +
                   "[SUGGESTION]\n" +
                   "- I can analyze live metrics and explain incident root-causes.\n" +
                   "- Try asking queries like:\n" +
                   "  * 'Why did CPU spike?'\n" +
                   "  * 'Analyze system memory status'\n" +
                   "  * 'What is the overall system health?'\n" +
                   "  * 'Show active HTTP error rate'";
        }
    }
}
