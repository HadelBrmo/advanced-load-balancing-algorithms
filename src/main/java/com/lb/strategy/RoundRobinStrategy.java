package com.lb.strategy;

import com.lb.model.Request;
import com.lb.model.Server;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinStrategy implements LoadBalancerStrategy {
    
    private final AtomicInteger counter = new AtomicInteger(0);
    
    @Override
    public Server getNextServer(List<Server> servers, Request request) {
        List<Server> availableServers = servers.stream()
                .filter(Server::isOnline)
                .filter(server -> !server.isOverloaded())
                .toList();
        
        if (availableServers.isEmpty()) {
            throw new RuntimeException("No available servers!");
        }
        
        int index = counter.getAndIncrement() % availableServers.size();
        
        return availableServers.get(index);
    }
    
    @Override
    public String getStrategyName() {
        return "Round Robin";
    }
}