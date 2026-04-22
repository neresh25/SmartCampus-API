package com.mycompany.smartcampus;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;


@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        
        System.err.println("Unexpected Error: " + exception.getMessage());
        exception.printStackTrace();

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "An unexpected error occurred. Please contact the administrator.");
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity(errorResponse)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
