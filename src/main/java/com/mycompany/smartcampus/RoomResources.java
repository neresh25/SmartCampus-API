/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus;

import com.mycompany.smartcampus.models.Room;
import com.mycompany.smartcampus.models.RoomSummary;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.PathParam;

/**
 *
 * @author ranji
 */
@Path("/rooms")
public class RoomResources {
     private static Map<String, Room> roomInfo = Database.getRooms();
     
     
     
     public RoomResources() {
        
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms(@Context UriInfo uriInfo) {
        
        List<RoomSummary> summaries = new ArrayList<>();
        for (Room room : roomInfo.values()) {
            String href = uriInfo.getAbsolutePathBuilder()
                                 .path(room.getId())
                                 .build()
                                 .toString();
            summaries.add(new RoomSummary(room.getId(), room.getName(), href));
        }
        return Response.ok(summaries).build();
    }
  
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room newRoom) {
        
       
        if (roomInfo.containsKey(newRoom.getId())) {
            
            return Response.status(Response.Status.CONFLICT)
                           .entity("A room with that ID already exists!")
                           .build();
        }
        
       
        roomInfo.put(newRoom.getId(), newRoom);
        
       
        return Response.status(Response.Status.CREATED)
                       .entity(newRoom)
                       .build();
    }
     @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoom(@PathParam("roomId") String roomId) {
        
        Room room = roomInfo.get(roomId); 
        
        if (room == null) {
        
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Room not found!")
                           .build();
        }
        
        
        return Response.ok(room).build();
    }
    
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        
        Room room = roomInfo.get(roomId);
        
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        if (!room.getSensorIds().isEmpty()) {
            
            throw new RoomNotEmpty("Cannot delete a room that still has active sensors!");
        }

        
        
        roomInfo.remove(roomId);
        
        return Response.noContent().build(); 
    }
}
