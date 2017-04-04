package br.jus.tst.tstunit.jaxrs.jackson;

import java.io.*;
import java.util.Objects;

import org.codehaus.jackson.map.ObjectMapper;

import br.jus.tst.tstunit.jaxrs.*;

/**
 * Implementação de {@link JsonToObjectConverter} que utiliza o {@link ObjectMapper} do pacote <code>org.codehaus</code>.
 * 
 * @author Thiago Miranda
 * @since 4 de abr de 2017
 */
public class JsonToObjectConverterCodehaus implements JsonToObjectConverter {

    private ObjectMapper objectMapper;

    public JsonToObjectConverterCodehaus(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper");
    }

    @Override
    public <T> T jsonToObject(InputStream stream, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(stream, toRealTypeReference(typeReference));
        } catch (IOException exception) {
            throw new JsonConverterException("Erro ao processar JSON: " + stream, exception);
        }
    }

    @Override
    public <T> T jsonToObject(String content, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(content, toRealTypeReference(typeReference));
        } catch (IOException exception) {
            throw new JsonConverterException("Erro ao processar JSON: " + content, exception);
        }
    }

    private <T> org.codehaus.jackson.type.TypeReference<T> toRealTypeReference(TypeReference<T> typeReference) {
        return new org.codehaus.jackson.type.TypeReference<T>() {
        };
    }

    @Override
    public <T> T jsonToObject(InputStream stream, Class<T> type) {
        try {
            return objectMapper.readValue(stream, type);
        } catch (IOException exception) {
            throw new JsonConverterException("Erro ao processar JSON: " + stream, exception);
        }
    }

    @Override
    public <T> T jsonToObject(String content, Class<T> type) {
        try {
            return objectMapper.readValue(content, type);
        } catch (IOException exception) {
            throw new JsonConverterException("Erro ao processar JSON: " + content, exception);
        }
    }

    @Override
    public Object getUnderlyingImplementation() {
        return objectMapper;
    }
}