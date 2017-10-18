package br.jus.tst.tstunit.jaxrs;

import java.util.Map;

import javax.ws.rs.core.MediaType;

/**
 * Representa uma simulação de requisição a um serviço JAX-RS.
 * 
 * @author Thiago Miranda
 * @since 29 de mar de 2017
 */
public interface MockRequest {

    /**
     * <p>
     * Define <em>path param</em>s a serem utilizados na substituição de valores do template de URI da requisição.
     * </p>
     * <p>
     * Exemplo:
     * </p>
     * 
     * <pre>
     * jaxRsEngine.get("meu-recurso/%d/%s").pathParams(1, "fulano");
     * // gerará uma URI 'meu-recurso/1/fulano'
     * </pre>
     * 
     * @param params
     *            os parâmetros
     * @return {@code this}, para chamadas encadeadas de método
     */
    MockRequest pathParams(Object... params);

    /**
     * <p>
     * Define <em>query param</em>s a serem acrescentadas à requisição.
     * </p>
     * <p>
     * Exemplo:
     * </p>
     * 
     * <pre>
     * jaxRsEngine.get("meu-recurso").queryParam("apenasAtivos", Boolean.TRUE);
     * // gerará uma URI 'meu-recurso?apenasAtivos=true'
     * 
     * jaxRsEngine.get("meu-recurso").queryParam("id", 1, 2, 3);
     * // gerará uma URI 'meu-recurso?id=1&id=2&id=3'
     * </pre>
     * 
     * @param key
     *            a chave do parâmetro
     * @param values
     *            valores do parâmetro
     * @return {@code this}, para chamadas encadeadas de método
     */
    MockRequest queryParam(String key, Object... values);

    /**
     * <p>
     * Define <em>query param</em>s a serem acrescentadas à requisição.
     * </p>
     * <p>
     * Exemplo:
     * </p>
     * 
     * <pre>
     * jaxRsEngine.get("meu-recurso")
     *         .queryParams(java.util.Collections.singletonMap("id", new Object[] { 1, 2, 3 }));
     * // gerará uma URI 'meu-recurso?id=1&amp;id=2&amp;id=3'
     * </pre>
     * 
     * @param params
     *            os parâmetros
     * @return {@code this}, para chamadas encadeadas de método
     */
    MockRequest queryParams(Map<String, Object[]> params);

    /**
     * Executa a requisição REST.
     * 
     * @return a resposta da requisição
     */
    MockResponse executar();

    /**
     * Define o tipo de conteúdo sendo enviado na requisição.
     * 
     * @param contentType
     *            o tipo de conteúdo
     * @return {@code this}, para chamadas encadeadas de método
     */
    MockRequest contentType(String contentType);

    /**
     * Define o tipo de conteúdo sendo enviado na requisição.
     * 
     * @param contentType
     *            o tipo de conteúdo
     * @return {@code this}, para chamadas encadeadas de método
     * 
     * @see MediaType
     */
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

    /**
     * Define o conteúdo da requisição como um <em>array</em> de bytes.
     * 
     * @param conteudo
     *            conteúdo a ser enviado na requisição
     * @return {@code this}, para chamadas encadeadas de método
     */
    MockRequest content(byte[] conteudo);
}
