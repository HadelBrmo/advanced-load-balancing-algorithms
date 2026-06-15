package com.lb.strategy;
import com.lb.model.Request;
import com.lb.model.Server;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class WeightedRoundRobinStrategy implements LoadBalancerStrategy {
    
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
        
        List<Server> weightedList = new ArrayList<>();
        for (Server server : availableServers) {
            for (int i = 0; i < server.getWeight(); i++) {
                weightedList.add(server);
            }
        }
        
        int index = counter.getAndIncrement() % weightedList.size();
        return weightedList.get(index);
    }
    
    @Override
    public String getStrategyName() {
        return "Weighted Round Robin";
    }
}