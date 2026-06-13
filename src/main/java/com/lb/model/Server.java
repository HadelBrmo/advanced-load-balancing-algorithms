package com.lb.model;

import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private final String id;
    private final String ipAddress;
    private final int weight;
    
    //بديل لعدد الاتصالات النشطة
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private volatile double cpuUsage;
    private volatile long currentLatencyMs;
    private volatile boolean isOnline = true;

    public Server(String id, String ipAddress, int weight) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.weight = weight;
        this.cpuUsage = 0.0;
        this.currentLatencyMs = 10;
    }

    public void handleRequest() {
        if (!isOnline) throw new RuntimeException("Server is offline!");
        activeConnections.incrementAndGet();
    }

    public void releaseRequest() {
        activeConnections.decrementAndGet();
    }

    public String getId() { return id; }
    public String getIpAddress() { return ipAddress; }
    public int getWeight() { return weight; }
    public int getActiveConnections() { return activeConnections.get(); }
    public double getCpuUsage() { return cpuUsage; }
    public long getCurrentLatencyMs() { return currentLatencyMs; }
    public boolean isOnline() { return isOnline; }

    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }
    public void setCurrentLatencyMs(long currentLatencyMs) { this.currentLatencyMs = currentLatencyMs; }
    public void setOnline(boolean online) { isOnline = online; }

    public boolean isOverloaded() {
        return cpuUsage > 90.0 || activeConnections.get() > 150;
    }

    @Override
    public String toString() {
        return String.format("Server[%s | IP:%s | Conn:%d | CPU:%.1f%% | Latency:%dms | Online:%b]",
                id, ipAddress, activeConnections.get(), cpuUsage, currentLatencyMs, isOnline);
    }
}