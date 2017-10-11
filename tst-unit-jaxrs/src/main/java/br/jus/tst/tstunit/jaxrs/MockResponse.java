package br.jus.tst.tstunit.jaxrs;

import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

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
     * @param status
     *            status HTTP esperado
     * @return {@code this}, para chamadas encadeadas de método
     * @throws AssertionError
     *             caso a asserção falhe
     */
    MockResponse deveRetornarStatus(Status status);

    /**
     * Asserção de que a resposta retorne uma representação de objeto do tipo informado.
     * 
     * @param tipo
     *            tipo de objeto esperado
     * @return {@code this}, para chamadas encadeadas de método
     * @throws AssertionError
     *             caso a asserção falhe
     * @deprecated Para obter a resposta em JSON, utilizar {@link #getObjetoRespostaUsando(JsonToObjectFunction)}. Este método será removido em versões futuras.
     */
    @Deprecated
    MockResponse deveRetornarObjetoDoTipo(Class<?> tipo);

    /**
     * Asserção de que a resposta retorne uma representação de objeto do tipo informado.
     * 
     * @param typeReference
     *            tipo de objeto esperado
     * @return {@code this}, para chamadas encadeadas de método
     * @throws AssertionError
     *             caso a asserção falhe
     * @deprecated Para obter a resposta em JSON, utilizar {@link #getObjetoRespostaUsando(JsonToObjectFunction)}. Este método será removido em versões futuras.
     */
    @Deprecated
    MockResponse deveRetornarObjetoDoTipo(TypeReference<?> typeReference);

    /**
     * Asserção de que a resposta retorne conteúdo do tipo informado.
     * 
     * @param contentType
     *            tipo de conteúdo esperado
     * @return {@code this}, para chamadas encadeadas de método
     * @throws AssertionError
     *             caso a asserção falhe
     */
    default MockResponse deveRetornarRespostaDoTipo(String contentType) {
        return deveRetornarRespostaDoTipo(MediaType.valueOf(contentType));
    }

    /**
     * Asserção de que a resposta retorne conteúdo do tipo informado.
     * 
     * @param contentType
     *            tipo de conteúdo esperado
     * @return {@code this}, para chamadas encadeadas de método
     * @throws AssertionError
     *             caso a asserção falhe
     */
    MockResponse deveRetornarRespostaDoTipo(MediaType contentType);

    /**
     * Asserção de que a resposta não possua corpo.
     * 
     * @return {@code this}, para chamadas encadeadas de método
     * @throws AssertionError
     *             caso a asserção falhe
     */
    MockResponse naoDeveRetornarConteudo();

    /**
     * Asserção de que a resposta possua o cabeçalho informado.
     * 
     * @param headerName
     *            nome do cabeçalho HTTP esperado
     * @return {@code this}, para chamadas encadeadas de método
     * @throws AssertionError
     *             caso a asserção falhe
     */
    MockResponse deveRetornarHeader(String headerName);

    /**
     * Asserção de que a resposta deve conter um <em>header</em> com o nome e valor informados. Observar o tipo do valor do <em>header</em>, que também será
     * checado (por exemplo, uma {@link URI} {@code "/teste/1"} será considerada diferente de uma String {@code "/teste/1"}).
     * 
     * @param headerName
     *            nome do cabeçalho HTTP esperado
     * @param headerValue
     *            valor esperado para o cabeçalho HTTP
     * @return {@code this}, para chamadas encadeadas de método
     * @throws AssertionError
     *             caso a asserção falhe
     */
    MockResponse deveRetornarHeader(String headerName, Object headerValue);

    /**
     * Obtém um POJO representando o JSON retornado como resposta.
     * 
     * @param converter
     *            instância a ser utilizada para converter a resposta JSON em um POJO.
     * @param <T>
     *            o tipo de objeto esperado
     * @return a instância criada a partir do JSON de resposta
     * @throws JaxRsException
     *             caso ocorra algum erro ao converter o conteúdo da resposta
     * 
     * @deprecated Para obter a resposta em JSON, utilizar {@link #getObjetoRespostaUsando(JsonToObjectFunction)}. Este método será removido em versões futuras.
     */
    @Deprecated
    <T> T getObjetoRespostaUsando(JsonToObjectConverter converter);

    /**
     * <p>
     * Obtém um POJO representando o JSON retornado como resposta.
     * </p>
     * <p>
     * Exemplo de utilização:
     * </p>
     * 
     * <pre>
     * MockResponse response = ...;
     * Objeto obj = response.getObjetoRespostaUsando((stream) -&gt; meuConversor.readObject(stream));
     * </pre>
     * 
     * @param conversor
     *            função a a ser utilizada para converter a resposta JSON em um POJO.
     * @param <T>
     *            o tipo de objeto esperado
     * @return a instância criada a partir do JSON de resposta
     * @throws NullPointerException
     *             caso seja informado {@code null}
     * @throws JaxRsException
     *             caso ocorra algum erro ao converter o conteúdo da resposta
     */
    default <T> T getObjetoRespostaUsando(JsonToObjectFunction<T> conversor) {
        try {
            return Objects.requireNonNull(conversor, "conversor").apply(getConteudoResposta());
        } catch (Exception exception) { // NOSONAR
            throw new JaxRsException("Erro ao obter conteúdo da resposta JSON", exception);
        }
    }

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

    /**
     * Fornece acesso à API sendo utilizada para simular os acessos HTTP.
     * 
     * @return a instância de acesso à API
     */
    Object getImplementacaoSubjacente();
}
