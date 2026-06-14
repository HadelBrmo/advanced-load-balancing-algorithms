package com.lb.strategy;

import com.lb.model.Request;
import com.lb.model.Server;
import java.util.List;
import java.util.Optional;

//هون باخد بعين الاعتبار الاتصالات النشطة مع الون يلي هوي قدرة السيرفر 

public class WeightedLeastConnectionsStrategy implements LoadBalancerStrategy {
    
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
                .min((s1, s2) -> {
                    double ratio1 = (double) s1.getActiveConnections() / s1.getWeight();
                    double ratio2 = (double) s2.getActiveConnections() / s2.getWeight();
                    return Double.compare(ratio1, ratio2);
                });
        
        return bestServer.orElseThrow(() -> new RuntimeException("No server found!"));
    }
    
    @Override
    public String getStrategyName() {
        return "Weighted Least Connections";
    }
}