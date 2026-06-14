package com.lb.strategy;

import com.lb.model.Request;
import com.lb.model.Server;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

//Load Balancer هاد ما الو شغل السسيرفرات بتشتغل لحالها و بتحط حالها ب queue

public class IdleJoinQueueStrategy implements LoadBalancerStrategy {
    
    private final Queue<String> idleQueue = new ConcurrentLinkedQueue<>();
    private final Set<String> inQueue = new HashSet<>();
    
    @Override
    public Server getNextServer(List<Server> servers, Request request) {
        List<Server> availableServers = servers.stream()
                .filter(Server::isOnline)
                .filter(server -> !server.isOverloaded())
                .toList();
        
        if (availableServers.isEmpty()) {
            throw new RuntimeException("No available servers!");
        }
        
        for (Server server : availableServers) {
            if (server.getActiveConnections() == 0 && !inQueue.contains(server.getId())) {
                idleQueue.add(server.getId());
                inQueue.add(server.getId());
            }
        }
        
        Set<String> availableIds = new HashSet<>();
        for (Server server : availableServers) {
            availableIds.add(server.getId());
        }
        
        while (!idleQueue.isEmpty()) {
            String serverId = idleQueue.poll();
            inQueue.remove(serverId);
            
            if (availableIds.contains(serverId)) {
                Optional<Server> selected = availableServers.stream()
                        .filter(s -> s.getId().equals(serverId))
                        .findFirst();
                
                if (selected.isPresent()) {
                    return selected.get();
                }
            }
        }
        
        return availableServers.get(0);
    }
    
    @Override
    public String getStrategyName() {
        return "Idle-Join Queue";
    }
}