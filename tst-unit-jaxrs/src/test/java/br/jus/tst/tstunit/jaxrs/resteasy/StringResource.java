package br.jus.tst.tstunit.jaxrs.resteasy;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

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

    @GET
    @Path("{NUMERO_STRING}")
    @Produces(MediaType.TEXT_PLAIN)
    public Integer converterParaNumero(@PathParam("NUMERO_STRING") String numero, @QueryParam("validar") @DefaultValue("true") Boolean validar) {
        if (BooleanUtils.isTrue(validar) && !NumberUtils.isNumber(numero)) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        } else {
            return Integer.valueOf(numero);
        }
    }

    @PUT
    @Path("{NUMERO}")
    @Produces(MediaType.TEXT_PLAIN)
    public String atualizarString(@PathParam("NUMERO") int numero, String valor) {
        return String.format("String nº %d atualizada para \"%s\"", numero, valor);
    }
}
