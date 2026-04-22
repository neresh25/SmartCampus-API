/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus;

/**
 *
 * @author ranji
 */

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider 
public class RoomNotEmptyMapper implements ExceptionMapper<RoomNotEmpty> {
    @Override
    public Response toResponse(RoomNotEmpty exception) {
        
        
        return Response.status(Response.Status.CONFLICT)
                       .entity("{\"error\": \"Conflict\", \"message\": \"" + exception.getMessage() + "\"}")
                       .type(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                       .build();
    }
}
