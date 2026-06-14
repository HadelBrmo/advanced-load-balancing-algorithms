package com.lb.simulator;
import com.lb.model.Request;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrafficGenerator {
    
    private final Random random;
    private int requestCounter;
    
    public TrafficGenerator() {
        this.random = new Random();
        this.requestCounter = 0;
    }
    
    
    public Request generateRequest() {
        requestCounter++;
        String requestId = "REQ-" + String.format("%06d", requestCounter);
        String clientIp = generateRandomIp();
        
        return new Request(requestId, clientIp);
    }
    
    
    public List<Request> generateRequests(int count) {
        List<Request> requests = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            requests.add(generateRequest());
        }
        return requests;
    }
    
   
    public List<Request> generateBurst(int count, long delayMs) {
        List<Request> requests = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            requests.add(generateRequest());
            
            if (delayMs > 0) {
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        return requests;
    }
    
   
    public void generateContinuousStream(int requestsPerSecond, int durationSeconds, 
                                         RequestHandler handler) {
        long intervalMs = 1000 / requestsPerSecond;
        long endTime = System.currentTimeMillis() + (durationSeconds * 1000);
        
        System.out.printf("🌊 Starting continuous traffic: %d requests/sec for %d seconds\n", 
                requestsPerSecond, durationSeconds);
        
        while (System.currentTimeMillis() < endTime) {
            long startTime = System.currentTimeMillis();
            
            Request request = generateRequest();
            handler.handle(request);
            
            long elapsed = System.currentTimeMillis() - startTime;
            long sleepTime = intervalMs - elapsed;
            
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        System.out.println("✅ Traffic stream completed!");
    }
   
    private String generateRandomIp() {
        return String.format("%d.%d.%d.%d",
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256));
    }
    
    
    @FunctionalInterface
    public interface RequestHandler {
        void handle(Request request);
    }
}
