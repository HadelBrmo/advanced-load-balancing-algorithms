package com.lb.strategy;
import com.lb.model.Request;
import com.lb.model.Server;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SmoothWeightedRoundRobinStrategy implements LoadBalancerStrategy {
    
    private final Map<String, Integer> currentWeights = new HashMap<>();
    
    @Override
    public Server getNextServer(List<Server> servers, Request request) {
        List<Server> availableServers = servers.stream()
                .filter(Server::isOnline)
                .filter(server -> !server.isOverloaded())
                .toList();
        
        if (availableServers.isEmpty()) {
            throw new RuntimeException("No available servers!");
        }
        
        int totalWeight = 0;
        Server bestServer = null;
        int bestCurrentWeight = -1;
        
        for (Server server : availableServers) {
            int currentWeight = currentWeights.getOrDefault(server.getId(), 0);
            
            currentWeight += server.getWeight();
            currentWeights.put(server.getId(), currentWeight);
            
            totalWeight += server.getWeight();
            
            if (currentWeight > bestCurrentWeight) {
                bestCurrentWeight = currentWeight;
                bestServer = server;
            }
        }
        
        if (bestServer != null) {
            int newWeight = currentWeights.get(bestServer.getId()) - totalWeight;
            currentWeights.put(bestServer.getId(), newWeight);
        }
        
        return bestServer;
    }
    
    @Override
    public String getStrategyName() {
        return "Smooth Weighted Round Robin";
    }
}