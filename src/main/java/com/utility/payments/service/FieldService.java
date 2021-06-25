package com.utility.payments.service;


import com.utility.payments.model.Field;
import com.utility.payments.repository.FieldRepository;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/fields")
public class FieldService {

    @Autowired
    public FieldRepository fieldRepository;
    @Context
    private Request request;
    @Context
    MessageContext context;
    @Context
    private HttpServletRequest servletRequest;

    @GET
    @Path("/")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getAccounts(@Context UriInfo info) {
        List<PathSegment> uriPathSegments = info.getPathSegments();
        //if (uriPathSegments != null) {
        PathSegment segment = info.getPathSegments().get(0);
        String name = segment.getMatrixParameters().getFirst("name");
        String id = segment.getMatrixParameters().getFirst("id");
        if (name != null) {
            Field field = fieldRepository.findByName(name.trim());
            return Response.ok(field).build();
        } else if (id != null) {
            Field field = fieldRepository.findOne(Integer.valueOf(id.trim()));
            return Response.ok(field).build();
        }

        else {
            List<Field> fieldList = fieldRepository.findAll();
            return Response.ok(fieldList).build();
        }
        //}

    }

    @GET
    @Path("/{name :.+}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getAccounts(@PathParam("name") String name) {
        Field field = fieldRepository.findByName(name.trim());
        return Response.ok(field).build();
    }


}
