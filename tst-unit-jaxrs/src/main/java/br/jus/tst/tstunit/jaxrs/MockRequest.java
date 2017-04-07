package br.jus.tst.tstunit.jaxrs;

import javax.ws.rs.core.MediaType;

/**
 * Representa uma simulação de requisição a um serviço JAX-RS.
 * 
 * @author Thiago Miranda
 * @since 29 de mar de 2017
 */
public interface MockRequest {

    MockRequest pathParams(Object... params);

    /**
     * Executa a requisição REST.
     * 
     * @return a resposta da requisição
     */
    MockResponse executar();

    MockRequest contentType(String contentType);

    MockRequest contentType(MediaType contentType);

    MockRequest content(Object conteudo, JsonToObjectConverter converter);

    MockRequest content(byte[] conteudo);
}
