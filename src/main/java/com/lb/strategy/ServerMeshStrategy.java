package com.lb.strategy;

import com.lb.model.Request;
import com.lb.model.Server;
import java.util.*;


public class ServerMeshStrategy implements LoadBalancerStrategy {
    
    private final Map<String, List<String>> meshTopology = new HashMap<>();
    private int roundRobinIndex = 0;
    
    @Override
    public Server getNextServer(List<Server> servers, Request request) {
        List<Server> availableServers = servers.stream()
                .filter(Server::isOnline)
                .filter(server -> !server.isOverloaded())
                .toList();
        
        if (availableServers.isEmpty()) {
            throw new RuntimeException("No available servers!");
        }
        
        if (meshTopology.isEmpty()) {
            buildMeshTopology(servers);
        }
        
        Server primaryServer = availableServers.get(roundRobinIndex % availableServers.size());
        roundRobinIndex++;
        
        if (primaryServer.isOverloaded()) {
            List<String> neighbors = meshTopology.getOrDefault(primaryServer.getId(), Collections.emptyList());
            
            for (String neighborId : neighbors) {
                Optional<Server> neighbor = availableServers.stream()
                        .filter(s -> s.getId().equals(neighborId))
                        .findFirst();
                
                if (neighbor.isPresent() && !neighbor.get().isOverloaded()) {
                    return neighbor.get();
                }
            }
        }
        
        return primaryServer;
    }
    
    private void buildMeshTopology(List<Server> servers) {
        for (int i = 0; i < servers.size(); i++) {
            String serverId = servers.get(i).getId();
            List<String> neighbors = new ArrayList<>();
            
            if (i + 1 < servers.size()) {
                neighbors.add(servers.get(i + 1).getId());
            }
            if (i + 2 < servers.size()) {
                neighbors.add(servers.get(i + 2).getId());
            }
            if (i == 0 && servers.size() > 2) {
                neighbors.add(servers.get(servers.size() - 1).getId());
            }
            
            meshTopology.put(serverId, neighbors);
        }
    }
    
    @Override
    public String getStrategyName() {
        return "Server-Mesh";
    }
}