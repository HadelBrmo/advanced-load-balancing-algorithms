package com.lb.strategy;

import com.lb.model.Request;
import com.lb.model.Server;
import java.util.List;

public interface LoadBalancerStrategy {
    
    Server getNextServer(List<Server> servers, Request request);
    
    String getStrategyName();
}