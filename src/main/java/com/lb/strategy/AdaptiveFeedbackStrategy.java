package com.lb.strategy;

import com.lb.model.Request;
import com.lb.model.Server;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class AdaptiveFeedbackStrategy implements LoadBalancerStrategy {
    
    private final Map<String, int[]> serverStats = new HashMap<>(); 
    
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
                    double score1 = calculateAdaptiveScore(s1);
                    double score2 = calculateAdaptiveScore(s2);
                    return Double.compare(score1, score2);
                });
        
        return bestServer.orElseThrow(() -> new RuntimeException("No server found!"));
    }
    
    private double calculateAdaptiveScore(Server server) {
        int[] stats = serverStats.getOrDefault(server.getId(), new int[]{1, 0});
        int successes = stats[0];
        int failures = stats[1];
        
        // Success Rate
        double successRate = (double) successes / (successes + failures);
        
        double latencyPenalty = 1.0 / (server.getCurrentLatencyMs() + 1);
        
        return (successRate * 0.7) + (latencyPenalty * 0.3);
    }
    
    public void recordRequestResult(String serverId, boolean success) {
        int[] stats = serverStats.computeIfAbsent(serverId, k -> new int[]{1, 0});
        if (success) {
            stats[0]++;
        } else {
            stats[1]++;
        }
    }
    
    @Override
    public String getStrategyName() {
        return "Adaptive-Feedback";
    }
}