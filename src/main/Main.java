package main;

import com.lb.core.LoadBalancer;

import com.lb.model.Request;

import com.lb.model.Server;

import com.lb.strategy.*;

import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=================================================");
        System.out.println("|    🚀 Adaptive Load Balancer System 🚀       |");
        System.out.println("=================================================\n");
        
        LoadBalancer loadBalancer = new LoadBalancer();
        
        Server server1 = new Server("Server-1", "192.168.1.10", 1);
        Server server2 = new Server("Server-2", "192.168.1.11", 2);
        Server server3 = new Server("Server-3", "192.168.1.12", 3);
        Server server4 = new Server("Server-4", "192.168.1.13", 4);
        
        loadBalancer.addServer(server1);
        loadBalancer.addServer(server2);
        loadBalancer.addServer(server3);
        loadBalancer.addServer(server4);
        
        System.out.println("✅ Added 4 servers successfully!\n");
        
        boolean running = true;
        
        while (running) {
            printMenu();
            System.out.print("\n➡️  Enter your choice (0-13): ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); 
                
                switch (choice) {
                    case 1 -> testStrategy(loadBalancer, new RoundRobinStrategy(), "Round Robin", scanner);
                    case 2 -> testStrategy(loadBalancer, new LeastConnectionsStrategy(), "Least Connections", scanner);
                    case 3 -> testStrategy(loadBalancer, new WeightedRoundRobinStrategy(), "Weighted Round Robin", scanner);
                    case 4 -> testStrategy(loadBalancer, new SmoothWeightedRoundRobinStrategy(), "Smooth Weighted RR", scanner);
                    case 5 -> testStrategy(loadBalancer, new ConsistentHashingStrategy(), "Consistent Hashing", scanner);
                    case 6 -> testStrategy(loadBalancer, new WeightedLeastConnectionsStrategy(), "Weighted Least Connections", scanner);
                    case 7 -> testStrategy(loadBalancer, new LatencyBasedStrategy(), "Latency-Based", scanner);
                    case 8 -> testStrategy(loadBalancer, new PerformanceBasedStrategy(), "Performance-Based", scanner);
                    case 9 -> testStrategy(loadBalancer, new AdaptiveFeedbackStrategy(), "Adaptive-Feedback", scanner);
                    case 10 -> testStrategy(loadBalancer, new ServerMeshStrategy(), "Server-Mesh", scanner);
                    case 11 -> testStrategy(loadBalancer, new IdleJoinQueueStrategy(), "Idle-Join Queue", scanner);
                    case 12 -> loadBalancer.performHealthCheck();
                    case 13 -> loadBalancer.printStatistics();
                    case 0 -> {
                        running = false;
                        System.out.println("\n👋 Goodbye! Thank you for using Load Balancer.");
                    }
                    default -> System.out.println("❌ Invalid choice! Please enter a number between 0 and 13.");
                }
                
                if (running && choice != 12 && choice != 13) {
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                }
                
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
                scanner.nextLine();
            }
        }
        
        scanner.close();
    }
    
  
    private static void printMenu() {
        System.out.println("================================================");
        System.out.println("|         📋 Select Load Balancing Strategy   |");
        System.out.println("================================================");
        System.out.println("|  1.  Round Robin                             |");
        System.out.println("|  2.  Least Connections                       |");
        System.out.println("|  3.  Weighted Round Robin                    |");
        System.out.println("|  4.  Smooth Weighted Round Robin             |");
        System.out.println("|  5.  Consistent Hashing                      |");
        System.out.println("|  6.  Weighted Least Connections              |");
        System.out.println("|  7.  Latency-Based                           |");
        System.out.println("|  8.  Performance-Based                       |");
        System.out.println("|  9.  Adaptive-Feedback                       |");
        System.out.println("|  10. Server-Mesh                             |");
        System.out.println("|  11. Idle-Join Queue                         |");
        System.out.println("|==============================================|");
        System.out.println("|  12. View Server Health                      |");
        System.out.println("|  13. View Statistics                         |");
        System.out.println("|  0.  Exit                                    |");
        System.out.println("================================================");
    }
    
    
    private static void testStrategy(LoadBalancer loadBalancer, LoadBalancerStrategy strategy, 
                                     String strategyName, Scanner scanner) {
        System.out.println("\n===============================================");
        System.out.println("║  Testing: " + strategyName);
        System.out.println("================================================\n");
        
        loadBalancer.setStrategy(strategy);
        loadBalancer.resetStatistics();
        
        System.out.print("📊 How many requests to send? (default 10): ");
        String input = scanner.nextLine();
        int numRequests = 10;
        
        
        try {
            if (!input.trim().isEmpty()) {
                numRequests = Integer.parseInt(input);
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠️  Invalid number, using default: 10");
        }
        
        System.out.println("\n📤 Sending " + numRequests + " requests...\n");
        System.out.println("─".repeat(70));
        
        for (int i = 1; i <= numRequests; i++) {
            String requestId = "REQ-" + String.format("%03d", i);
            String clientIp = "10.0.0." + (i % 255);
            
            Request request = new Request(requestId, clientIp);
            
            try {
                Server selectedServer = loadBalancer.dispatchRequest(request);
                
                Thread.sleep(50 + (int)(Math.random() * 100));
                loadBalancer.releaseRequest(selectedServer);
                
            } catch (Exception e) {
                System.out.printf("❌ Request %s failed: %s%n", requestId, e.getMessage());
            }
        }
        
        System.out.println("─".repeat(70));
        
        System.out.println("\n📊 Results:");
        loadBalancer.printStatistics();
    }
}