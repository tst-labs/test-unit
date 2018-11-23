package br.jus.tst.tstunit.jaxrs.resteasy;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

/**
 * Classe utilizada nos testes.
 * 
 * @author Thiago Miranda
 * @since 30 de mar de 2017
 */
@Path("strings")
public class StringResource {

    @Inject
    private CharacterResource characterResource;

    @GET
    @Path("{NUMERO}")
    @Produces(MediaType.TEXT_PLAIN)
    public String converterParaString(@PathParam("NUMERO") int numero, @QueryParam("validar") @DefaultValue("true") List<Boolean> validar) {
        if (validar.contains(Boolean.TRUE) && numero <= 0) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        } else {
            return String.valueOf(numero);
        }
    }

    @Path("{NUMERO}/chars")
    public CharacterResource chars(@PathParam("NUMERO") int numero) {
        return characterResource;
    }

    @PUT
    @Path("{NUMERO}")
    @Produces(MediaType.TEXT_PLAIN)
    public String atualizarString(@PathParam("NUMERO") int numero, String valor) {
        return String.format("String nº %d atualizada para \"%s\"", numero, valor);
    }
}

class CharacterResource {

    @PathParam("NUMERO")
    private int numero;

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public String chars() {
        return String.valueOf(numero).toCharArray().toString();
    }
}
