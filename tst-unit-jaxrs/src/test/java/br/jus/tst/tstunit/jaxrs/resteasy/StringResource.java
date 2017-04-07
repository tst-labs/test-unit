package br.jus.tst.tstunit.jaxrs.resteasy;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Classe utilizada nos testes.
 * 
 * @author Thiago Miranda
 * @since 30 de mar de 2017
 */
@Path("strings")
public class StringResource {

    @GET
    @Path("{NUMERO}")
    @Produces(MediaType.TEXT_PLAIN)
    public String converterParaString(@PathParam("NUMERO") int numero) {
        return String.valueOf(numero);
    }

    @PUT
    @Path("{NUMERO}")
    @Produces(MediaType.TEXT_PLAIN)
    public String atualizarString(@PathParam("NUMERO") int numero, String valor) {
        return String.format("String nº %d atualizada para \"%s\"", numero, valor);
    }
}
