package com.lb.simulator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MetricsCollector {
    
    private final Map<String, ServerMetrics> serverMetrics;
    private long totalRequests;
    private long successfulRequests;
    private long failedRequests;
    private long totalLatency;
    private final List<Long> requestTimestamps;
    
    public MetricsCollector() {
        this.serverMetrics = new ConcurrentHashMap<>();
        this.totalRequests = 0;
        this.successfulRequests = 0;
        this.failedRequests = 0;
        this.totalLatency = 0;
        this.requestTimestamps = Collections.synchronizedList(new ArrayList<>());
    }
    
   
    public void recordSuccess(String serverId, long latencyMs) {
        totalRequests++;
        successfulRequests++;
        totalLatency += latencyMs;
        requestTimestamps.add(System.currentTimeMillis());
        
        serverMetrics.computeIfAbsent(serverId, k -> new ServerMetrics())
                .recordSuccess(latencyMs);
    }
    
    
    public void recordFailure(String serverId) {
        totalRequests++;
        failedRequests++;
        
        serverMetrics.computeIfAbsent(serverId, k -> new ServerMetrics())
                .recordFailure();
    }
    
    
    public double calculateThroughput(long timeWindowMs) {
        long now = System.currentTimeMillis();
        long cutoff = now - timeWindowMs;
        
        long count = requestTimestamps.stream()
                .filter(timestamp -> timestamp >= cutoff)
                .count();
        
        return (double) count / (timeWindowMs / 1000.0);
    }
    
   
    public double getAverageLatency() {
        if (successfulRequests == 0) return 0.0;
        return (double) totalLatency / successfulRequests;
    }
    
   
    public double getSuccessRate() {
        if (totalRequests == 0) return 0.0;
        return (double) successfulRequests / totalRequests * 100;
    }
    
    
    public void printReport() {
        System.out.println("\n╔═══════════════════════════════════════════════╗");
        System.out.println("║  📊 Performance Report                        ║");
        System.out.println("╚═══════════════════════════════════════════════╝\n");
        
        System.out.println("┌─────────────────────────────────────────────┐");
        System.out.println("│  Overall Statistics                         │");
        System.out.println("├─────────────────────────────────────────────┤");
        System.out.printf("│  Total Requests:     %15d │%n", totalRequests);
        System.out.printf("│  Successful:         %15d │%n", successfulRequests);
        System.out.printf("│  Failed:             %15d │%n", failedRequests);
        System.out.printf("│  Success Rate:       %14.2f%% │%n", getSuccessRate());
        System.out.printf("│  Avg Latency:        %14.2f ms │%n", getAverageLatency());
        System.out.printf("│  Throughput (1min):  %14.2f req/s │%n", calculateThroughput(60000));
        System.out.println("└─────────────────────────────────────────────┘\n");
        
        System.out.println("┌─────────────────────────────────────────────┐");
        System.out.println("│  Per-Server Statistics                      │");
        System.out.println("├─────────────────────────────────────────────┤");
        
        for (Map.Entry<String, ServerMetrics> entry : serverMetrics.entrySet()) {
            String serverId = entry.getKey();
            ServerMetrics metrics = entry.getValue();
            
            System.out.printf("│  %-20s                         │%n", serverId);
            System.out.printf("│    Requests: %10d                  │%n", metrics.totalRequests);
            System.out.printf("│    Avg Latency: %8.2f ms              │%n", metrics.getAverageLatency());
            System.out.println("│                                     │");
        }
        
        System.out.println("└─────────────────────────────────────────────┘\n");
    }
    
    
    public void reset() {
        totalRequests = 0;
        successfulRequests = 0;
        failedRequests = 0;
        totalLatency = 0;
        requestTimestamps.clear();
        serverMetrics.clear();
    }
    
    
    private static class ServerMetrics {
        private long totalRequests;
        private long successfulRequests;
        private long totalLatency;
        
        public void recordSuccess(long latencyMs) {
            totalRequests++;
            successfulRequests++;
            totalLatency += latencyMs;
        }
        
        public void recordFailure() {
            totalRequests++;
        }
        
        public double getAverageLatency() {
            if (successfulRequests == 0) return 0.0;
            return (double) totalLatency / successfulRequests;
        }
    }
}
