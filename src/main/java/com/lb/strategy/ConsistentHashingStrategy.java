package com.lb.strategy;
import com.lb.model.Request;
import com.lb.model.Server;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

//ميزة متقدمة جداً اسمها Virtual Nodes (العقد الافتراضية)

public class ConsistentHashingStrategy implements LoadBalancerStrategy {
    
    private final TreeMap<Integer, Server> hashRing = new TreeMap<>();
    private final int VIRTUAL_NODES = 150; 
    
    @Override
    public Server getNextServer(List<Server> servers, Request request) {
        List<Server> availableServers = servers.stream()
                .filter(Server::isOnline)
                .filter(server -> !server.isOverloaded())
                .toList();
        
        if (availableServers.isEmpty()) {
            throw new RuntimeException("No available servers!");
        }
        
        hashRing.clear();
        for (Server server : availableServers) {
            for (int i = 0; i < VIRTUAL_NODES; i++) {
                String virtualNodeKey = server.getId() + "#VN" + i;
                int hash = virtualNodeKey.hashCode();
                hashRing.put(hash, server);
            }
        }
        
        int requestHash = request.getClientIp().hashCode();
        
        SortedMap<Integer, Server> tailMap = hashRing.tailMap(requestHash);
        
        int ringHash = tailMap.isEmpty() ? hashRing.firstKey() : tailMap.firstKey();
        
        return hashRing.get(ringHash);
    }
    
    @Override
    public String getStrategyName() {
        return "Consistent Hashing";
    }
}