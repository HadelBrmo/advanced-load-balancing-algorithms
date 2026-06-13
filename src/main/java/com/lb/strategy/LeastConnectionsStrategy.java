package com.lb.strategy;

import com.lb.model.Request;
import com.lb.model.Server;
import java.util.List;
import java.util.Optional;

public class LeastConnectionsStrategy implements LoadBalancerStrategy {
    
    @Override
    public Server getNextServer(List<Server> servers, Request request) {
        List<Server> availableServers = servers.stream()
                .filter(Server::isOnline)
                .filter(server -> !server.isOverloaded())
                .toList();
        
        if (availableServers.isEmpty()) {
            throw new RuntimeException("No available servers!");
        }
        
        Optional<Server> minServer = availableServers.stream()
                .min((s1, s2) -> Integer.compare(
                    s1.getActiveConnections(), 
                    s2.getActiveConnections()
                ));
        
        return minServer.orElseThrow(() -> new RuntimeException("No server found!"));
    }
    
    @Override
    public String getStrategyName() {
        return "Least Connections";
    }
}