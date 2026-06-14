package com.lb.simulator;

import com.lb.model.Server;
import java.util.List;
import java.util.Random;

public class ServerSimulator {
    
    private final Random random;
    
    public ServerSimulator() {
        this.random = new Random();
    }
    
    
    public void simulateRandomCpuChanges(List<Server> servers) {
        for (Server server : servers) {
            if (server.isOnline()) {
                double newCpu = 10 + (random.nextDouble() * 70);
                server.setCpuUsage(newCpu);
            }
        }
    }
    
    
    public void simulateRandomLatencyChanges(List<Server> servers) {
        for (Server server : servers) {
            if (server.isOnline()) {
                // Latency بين 5ms و 100ms
                long newLatency = 5 + random.nextInt(95);
                server.setCurrentLatencyMs(newLatency);
            }
        }
    }
    
   
    public void simulateOverload(Server server, double cpuUsage, int connections) {
        server.setCpuUsage(cpuUsage);
        
        int currentConnections = server.getActiveConnections();
        for (int i = currentConnections; i < connections; i++) {
            server.handleRequest();
        }
        
        System.out.printf("⚠️  Server %s overloaded: CPU=%.1f%%, Connections=%d%n",
                server.getId(), cpuUsage, connections);
    }
    
   
    public void simulateRandomFailures(List<Server> servers, double failureProbability) {
        for (Server server : servers) {
            if (random.nextDouble() < failureProbability) {
                server.setOnline(false);
                System.out.println("💥 Server " + server.getId() + " FAILED!");
            }
        }
    }
    
  
    public void recoverFailedServers(List<Server> servers) {
        for (Server server : servers) {
            if (!server.isOnline()) {
                server.setOnline(true);
                System.out.println("✅ Server " + server.getId() + " recovered!");
            }
        }
    }
    
    
    public void simulateGradualLoadIncrease(List<Server> servers, int steps, long delayMs) {
        System.out.println("📈 Simulating gradual load increase over " + steps + " steps...");
        
        for (int step = 1; step <= steps; step++) {
            double loadFactor = (double) step / steps;
            
            for (Server server : servers) {
                if (server.isOnline()) {
                    double newCpu = server.getCpuUsage() + (20 * loadFactor);
                    server.setCpuUsage(Math.min(newCpu, 100.0));
                    
                    long newLatency = server.getCurrentLatencyMs() + (int)(50 * loadFactor);
                    server.setCurrentLatencyMs(newLatency);
                }
            }
            
            System.out.printf("   Step %d/%d completed%n", step, steps);
            
            if (step < steps && delayMs > 0) {
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    
    public void resetAllServers(List<Server> servers) {
        for (Server server : servers) {
            server.setCpuUsage(0.0);
            server.setCurrentLatencyMs(10 + random.nextInt(20));
            server.setOnline(true);
            
            while (server.getActiveConnections() > 0) {
                server.releaseRequest();
            }
        }
        
        System.out.println("✅ All servers reset to normal state!");
    }
}
