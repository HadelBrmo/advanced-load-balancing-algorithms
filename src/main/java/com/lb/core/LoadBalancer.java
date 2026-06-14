package com.lb.core;

import com.lb.model.Request;
import com.lb.model.Server;
import com.lb.strategy.LoadBalancerStrategy;
import com.lb.strategy.RoundRobinStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LoadBalancer {
    
    private final List<Server> servers = new CopyOnWriteArrayList<>();
    
    private LoadBalancerStrategy strategy;
    
    private long totalRequests = 0;
    
    private long successfulRequests = 0;
    
    public LoadBalancer() {
        this.strategy = new RoundRobinStrategy();
    }
    
    public LoadBalancer(LoadBalancerStrategy strategy) {
        this.strategy = strategy;
    }
    
  
    public void addServer(Server server) {
        servers.add(server);
        System.out.println("✅ Server added: " + server.getId());
    }
    
    
    public void removeServer(String serverId) {
        servers.removeIf(server -> server.getId().equals(serverId));
        System.out.println("❌ Server removed: " + serverId);
    }
    
    
    public List<Server> getServers() {
        return new ArrayList<>(servers);
    }
    
    
   
    public void setStrategy(LoadBalancerStrategy strategy) {
        this.strategy = strategy;
        System.out.println("🔄 Strategy changed to: " + strategy.getStrategyName());
    }
    
   
    public LoadBalancerStrategy getStrategy() {
        return strategy;
    }
    
    
    public Server dispatchRequest(Request request) {
        List<Server> healthyServers = filterHealthyServers();
        
        if (healthyServers.isEmpty()) {
            throw new RuntimeException("⚠️ No healthy servers available!");
        }
        
        Server selectedServer = strategy.getNextServer(healthyServers, request);
        
        selectedServer.handleRequest();
        
        totalRequests++;
        successfulRequests++;
        
        System.out.printf("📩 Request %s → %s | Active Connections: %d%n",
                request.getRequestId(),
                selectedServer.getId(),
                selectedServer.getActiveConnections());
        
        return selectedServer;
    }
    
    
    public void releaseRequest(Server server) {
        server.releaseRequest();
    }
    
    
    
    private List<Server> filterHealthyServers() {
        return servers.stream()
                .filter(Server::isOnline)
                .filter(server -> !server.isOverloaded())
                .toList();
    }
    
   
    public void performHealthCheck() {
        System.out.println("\n === Health Check ===");
        
        for (Server server : servers) {
            String status = server.isOnline() ? "ONLINE" : "OFFLINE";
            String load = server.isOverloaded() ? "⚠️ OVERLOADED" : "✅ OK";
            
            System.out.printf("Server %s: %s | %s | CPU: %.1f%% | Connections: %d | Latency: %dms%n",
                    server.getId(),
                    status,
                    load,
                    server.getCpuUsage(),
                    server.getActiveConnections(),
                    server.getCurrentLatencyMs());
        }
        
        System.out.println("======================\n");
    }
    
   
    public void printStatistics() {
        System.out.println("\n📊 === Load Balancer Statistics ===");
        System.out.println("Strategy: " + strategy.getStrategyName());
        System.out.println("Total Servers: " + servers.size());
        System.out.println("Healthy Servers: " + filterHealthyServers().size());
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Successful Requests: " + successfulRequests);
        
        if (totalRequests > 0) {
            double successRate = (double) successfulRequests / totalRequests * 100;
            System.out.printf("Success Rate: %.2f%%%n", successRate);
        }
        
        System.out.println("===================================\n");
    }
    
    
    public void resetStatistics() {
        totalRequests = 0;
        successfulRequests = 0;
    }
}