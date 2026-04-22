/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus;

/**
 *
 * @author ranji
 */

import com.mycompany.smartcampus.models.Sensor;
import com.mycompany.smartcampus.models.SensorReading;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SensorReadingResource {
    private String sensorId;
    
    
    private Map<String, List<SensorReading>> historyDb = Database.getReadingsHistory();
    private Map<String, Sensor> sensorDb = Database.getSensors();
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings() {
        
        List<SensorReading> readings = historyDb.getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(readings).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading newReading) {
        
        Sensor parentSensor = sensorDb.get(sensorId);
        
        
        if ("MAINTENANCE".equalsIgnoreCase(parentSensor.getStatus())) {
            throw new SensorUnavailableException("Sensor '" + sensorId + "' is currently in MAINTENANCE mode and cannot accept new readings.");
        }

        List<SensorReading> history = historyDb.getOrDefault(sensorId, new ArrayList<>());
        
        history.add(newReading);
        
        historyDb.put(sensorId, history);
        parentSensor.setCurrentValue(newReading.getValue());
        return Response.status(Response.Status.CREATED)
                       .entity(newReading)
                       .build();
    }
}
