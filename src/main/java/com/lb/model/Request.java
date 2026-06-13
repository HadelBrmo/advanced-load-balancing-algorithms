package com.lb.model;

public class Request {
    private final String requestId;
    private final String clientIp;
    
    //مفيد لقياس الـ Latency (كم أخذ الطلب من وقت)
    private final long timestamp;

    public Request(String requestId, String clientIp) {
        this.requestId = requestId;
        this.clientIp = clientIp;
        this.timestamp = System.currentTimeMillis();
    }

    public String getRequestId() { return requestId; }
    public String getClientIp() { return clientIp; }
    public long getTimestamp() { return timestamp; }
}