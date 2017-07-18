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

    /**
     * Define o conteúdo da requisição como um objeto, utilizando o conversor informado para transformá-lo em um JSON.
     * 
     * @param conteudo
     *            o objeto a ser gravado na requisição
     * @param converter
     *            conversor de objetos Java em JSON
     * @return {@code this}, para chamadas encadeadas de método
     * 
     * @deprecated Para gravar um conteúdo em JSON, utilizar {@link #content(Object, ObjectToJsonFunction)}. Este método será removido em versões futuras.
     */
    @Deprecated
    MockRequest content(Object conteudo, JsonToObjectConverter converter);

    /**
     * Define o conteúdo da requisição como um objeto, utilizando a função informada para transformá-lo em um JSON.
     * 
     * @param conteudo
     *            o objeto a ser gravado na requisição
     * @param converter
     *            conversor de objetos Java em JSON
     * @return {@code this}, para chamadas encadeadas de método
     */
    MockRequest content(Object conteudo, ObjectToJsonFunction converter);

    MockRequest content(byte[] conteudo);
}
