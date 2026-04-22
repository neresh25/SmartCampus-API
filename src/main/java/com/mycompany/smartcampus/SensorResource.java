/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus;

/**
 *
 * @author ranji
 */

import com.mycompany.smartcampus.models.Room;
import com.mycompany.smartcampus.models.Sensor;
import java.util.ArrayList;
import java.util.Collection;

import java.util.Map;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;



@Path("/sensors")
public class SensorResource {
     
    private static Map<String, Sensor> sensorInfo = Database.getSensors();
    
   
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSensors(@QueryParam("type") String type) {
        
        Collection<Sensor> allSensors = sensorInfo.values();
        
        if (type != null && !type.trim().isEmpty()) {
            
            List<Sensor> filteredList = new ArrayList<>();
            
            
            for (Sensor s : allSensors) {
               
                if (s.getType().equalsIgnoreCase(type)) {
                    filteredList.add(s);
                }
            }
            
            
            return Response.ok(filteredList).build();
        }
        
        return Response.ok(allSensors).build();
    }

    @GET
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensor(@javax.ws.rs.PathParam("sensorId") String sensorId) {
        Sensor sensor = sensorInfo.get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Sensor not found!")
                           .build();
        }
        return Response.ok(sensor).build();
    }
      
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor newSensor, @Context UriInfo uriInfo) {
        
        if (sensorInfo.containsKey(newSensor.getId())) {
            Map<String, String> err = new java.util.HashMap<>();
            err.put("error", "Conflict");
            err.put("message", "A sensor with ID '" + newSensor.getId() + "' already exists.");
            return Response.status(Response.Status.CONFLICT)
                           .entity(err)
                           .type(MediaType.APPLICATION_JSON)
                           .build();
        }

        Map<String, Room> globalRooms = Database.getRooms();
        if (!globalRooms.containsKey(newSensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Verification failed! Room ID '" + newSensor.getRoomId() + "' does not exist!");
        }
        
        sensorInfo.put(newSensor.getId(), newSensor);
        
        Room assignedRoom = globalRooms.get(newSensor.getRoomId());
        assignedRoom.getSensorIds().add(newSensor.getId());

        // Build the Location header pointing to the new sensor's detail endpoint.
        java.net.URI location = uriInfo.getAbsolutePathBuilder()
                                       .path(newSensor.getId())
                                       .build();

        return Response.status(Response.Status.CREATED)
                       .entity(newSensor)
                       .location(location)
                       .build();
    }
     @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@javax.ws.rs.PathParam("sensorId") String sensorId) {
        
      
        if (!sensorInfo.containsKey(sensorId)) {
           
            throw new javax.ws.rs.WebApplicationException(
                Response.status(Response.Status.NOT_FOUND).entity("Sensor not found!").build()
            );
        }
        
        
        return new SensorReadingResource(sensorId);
    }

}
