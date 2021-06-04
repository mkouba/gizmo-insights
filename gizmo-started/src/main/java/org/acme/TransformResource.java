package org.acme;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.acme.gizmonster.TransformService;

@Path("/transform")
public class TransformResource {

    @Inject
    TransformService service;

    @POST
    @Path("{value}")
    @Produces(MediaType.TEXT_PLAIN)
    public String transform(@PathParam("value") String value) {
        return service.transform(value);
    }
}