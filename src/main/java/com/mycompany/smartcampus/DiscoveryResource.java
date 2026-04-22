package com.mycompany.smartcampus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/") 
public class DiscoveryResource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getDiscovery() {
        
        Map<String, Object> info = new HashMap<>(); 
        
        info.put("version", "v1");
        info.put("status", "System Online");
        info.put("message", "Welcome to the Smart Campus API");
        
        
        info.put("contact", "admin@smartcampus.westminster.ac.uk"); 
        
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("rooms", "/api/v1/rooms");
        endpoints.put("sensors", "/api/v1/sensors");
        info.put("endpoints", endpoints);
        
        return info;
    }
}
