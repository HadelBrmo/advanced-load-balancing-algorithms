package com.lb.strategy;

import com.lb.model.Request;
import com.lb.model.Server;
import java.util.List;
import java.util.Optional;


public class PerformanceBasedStrategy implements LoadBalancerStrategy {
    
    @Override
    public Server getNextServer(List<Server> servers, Request request) {
        List<Server> availableServers = servers.stream()
                .filter(Server::isOnline)
                .filter(server -> !server.isOverloaded())
                .toList();
        
        if (availableServers.isEmpty()) {
            throw new RuntimeException("No available servers!");
        }
        
        Optional<Server> bestServer = availableServers.stream()
                .max((s1, s2) -> {
                    double score1 = calculatePerformanceScore(s1);
                    double score2 = calculatePerformanceScore(s2);
                    return Double.compare(score1, score2);
                });
        
        return bestServer.orElseThrow(() -> new RuntimeException("No server found!"));
    }
    
    private double calculatePerformanceScore(Server server) {
        double latencyScore = 100.0 / (server.getCurrentLatencyMs() + 1); 
        double cpuScore = 100.0 - server.getCpuUsage(); 
        double connectionScore = 100.0 / (server.getActiveConnections() + 1);
        
        return (latencyScore * 0.5) + (cpuScore * 0.3) + (connectionScore * 0.2);
    }
    
    @Override
    public String getStrategyName() {
        return "Performance-Based";
    }
}


















/*
بنحسب Score لكل خادم بناءً على عدة عوامل:
Latency (الأقل أفضل)
CPU Usage (الأقل أفضل)
Active Connections (الأقل أفضل)
*/