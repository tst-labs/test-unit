package br.jus.tst.tstunit.jaxrs.resteasy;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.*;
import java.util.*;

import javax.ws.rs.core.*;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.Validate;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.mock.MockHttpResponse;

import br.jus.tst.tstunit.jaxrs.*;
import br.jus.tst.tstunit.jaxrs.jackson.JsonConverterException;

/**
 * Implementação de resposta REST utilizando o RestEasy.
 * 
 * @author Thiago Miranda
 * @since 29 de mar de 2017
 */
public class ResteasyResponse implements MockResponse {

    private MockHttpResponse httpResponse;
    private Class<?> tipoObjetoResposta;
    private TypeReference<?> typeReferenceResposta;

    public ResteasyResponse(MockHttpResponse response) {
        this.httpResponse = Objects.requireNonNull(response, "response");
    }

    @Override
    public MockResponse deveRetornarStatusOk() {
        assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.SC_OK);
        return this;
    }

    @Override
    public MockResponse deveRetornarRespostaDoTipo(MediaType contentType) {
        List<Object> headersContentType = ListUtils.emptyIfNull(httpResponse.getOutputHeaders().get(HttpHeaders.CONTENT_TYPE));
        assertThat(headersContentType).overridingErrorMessage("Header %s ausente da resposta", HttpHeaders.CONTENT_TYPE).isNotEmpty();

        MediaType contentTypeResposta = MediaType.valueOf(headersContentType.get(0).toString());
        assertThat(contentTypeResposta.isCompatible(contentType))
                .overridingErrorMessage("O tipo retornado %s não é compatível com o tipo informado %s", contentTypeResposta, contentType).isTrue();

        return this;
    }

    @Override
    public MockResponse deveRetornarRespostaDoTipo(String contentType) {
        return deveRetornarRespostaDoTipo(MediaType.valueOf(contentType));
    }

    @Override
    public MockResponse deveRetornarObjetoDoTipo(Class<?> tipo) {
        this.tipoObjetoResposta = Objects.requireNonNull(tipo, "tipo");
        return this;
    }

    @Override
    public MockResponse deveRetornarObjetoDoTipo(TypeReference<?> typeReference) {
        this.typeReferenceResposta = Objects.requireNonNull(typeReference, "typeReferenceResposta");
        return this;
    }

    @Override
    public InputStream getConteudoResposta() {
        return new ByteArrayInputStream(httpResponse.getOutput());
    }

    @Override
    public String getConteudoRespostaComoString() {
        return httpResponse.getContentAsString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getObjetoRespostaUsando(JsonToObjectConverter converter) {
        Objects.requireNonNull(converter, "converter");
        Validate.validState(tipoObjetoResposta != null || typeReferenceResposta != null, "Tipo de objeto de resposta não definido");

        try {
            return (T) (tipoObjetoResposta != null ? converter.jsonToObject(httpResponse.getContentAsString(), tipoObjetoResposta)
                    : converter.jsonToObject(httpResponse.getContentAsString(), typeReferenceResposta));
        } catch (JsonConverterException exception) {
            throw new JaxRsException("Erro ao obter corpo da resposta como " + tipoObjetoResposta, exception);
        }
    }

    @Override
    public Object getImplementacaoSubjacente() {
        return httpResponse;
    }
}