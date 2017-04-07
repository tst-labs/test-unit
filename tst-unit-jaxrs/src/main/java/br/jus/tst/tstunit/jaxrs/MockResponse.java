package br.jus.tst.tstunit.jaxrs;

import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpStatus;

/**
 * Representa uma simulação de resposta de um serviço JAX-RS.
 * 
 * @author Thiago Miranda
 * @since 29 de mar de 2017
 */
public interface MockResponse {

    /**
     * Asserção de que o código HTTP da resposta seja {@link HttpStatus#SC_OK}.
     * 
     * @return {@code this}, para chamadas encadeadas de método
     * @throws AssertionError
     *             caso a asserção falhe
     */
    MockResponse deveRetornarStatusOk();

    /**
     * Asserção de que o código HTTP da resposta seja igual ao informado.
     * 
     * @return {@code this}, para chamadas encadeadas de método
     * @throws AssertionError
     *             caso a asserção falhe
     */
    MockResponse deveRetornarStatus(Status status);

    /**
     * 
     * @param tipo
     * @return {@code this}, para chamadas encadeadas de método
     * @throws AssertionError
     *             caso a asserção falhe
     */
    MockResponse deveRetornarObjetoDoTipo(Class<?> tipo);

    /**
     * 
     * @param typeReference
     * @return {@code this}, para chamadas encadeadas de método
     * @throws AssertionError
     *             caso a asserção falhe
     */
    MockResponse deveRetornarObjetoDoTipo(TypeReference<?> typeReference);

    /**
     * 
     * @param contentType
     * @return {@code this}, para chamadas encadeadas de método
     * @throws AssertionError
     *             caso a asserção falhe
     */
    MockResponse deveRetornarRespostaDoTipo(String contentType);

    /**
     * 
     * @param contentType
     * @return {@code this}, para chamadas encadeadas de método
     * @throws AssertionError
     *             caso a asserção falhe
     */
    MockResponse deveRetornarRespostaDoTipo(MediaType contentType);

    /**
     * 
     * @param location
     * @return {@code this}, para chamadas encadeadas de método
     * @throws AssertionError
     *             caso a asserção falhe
     */
    MockResponse deveRetornarHeader(String headerName);

    /**
     * Asserção de que a resposta deve conter um <em>header</em> com o nome e valor informados. Observar o tipo do valor do <em>header</em>, que também será
     * checado (por exemplo, uma {@link URI} {@code "/teste/1"} será considerada diferente de uma String {@code "/teste/1"}).
     * 
     * @param location
     * @return {@code this}, para chamadas encadeadas de método
     * @throws AssertionError
     *             caso a asserção falhe
     */
    MockResponse deveRetornarHeader(String headerName, Object headerValue);

    /**
     * 
     * @param converter
     * @return
     * @throws JaxRsException
     *             caso ocorra algum erro ao converter o conteúdo da resposta
     */
    <T> T getObjetoRespostaUsando(JsonToObjectConverter converter);

    /**
     * Obtém o conteúdo da resposta como um <em>stream</em>
     * 
     * @return <em>stream</em> de leitura do conteúdo da resposta
     */
    InputStream getConteudoResposta();

    /**
     * Obtém o conteúdo da resposta no formato de uma String.
     * 
     * @return o conteúdo da resposta como String
     * @throws JaxRsException
     *             caso ocorra algum erro ao converter o conteúdo da resposta em String
     */
    String getConteudoRespostaComoString();

    Object getImplementacaoSubjacente();
}
