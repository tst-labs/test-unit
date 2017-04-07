package br.jus.tst.tstunit.jaxrs.jackson;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.jus.tst.tstunit.jaxrs.*;

/**
 * Implementação de {@link JsonToObjectConverter} que utiliza o {@link ObjectMapper} do pacote <code>com.fasterxml</code>.
 * 
 * @author Thiago Miranda
 * @since 4 de abr de 2017
 */
public class JsonToObjectConverterFasterxml implements JsonToObjectConverter {

    private ObjectMapper objectMapper;

    public JsonToObjectConverterFasterxml(ObjectMapper objectMapper) {
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

    private <T> com.fasterxml.jackson.core.type.TypeReference<T> toRealTypeReference(TypeReference<T> typeReference) {
        return new com.fasterxml.jackson.core.type.TypeReference<T>() {
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
    public Optional<InputStream> objectToJson(Object conteudo) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            objectMapper.writeValue(outputStream, conteudo);
            outputStream.flush();
        } catch (IOException exception) {
            throw new JsonConverterException("Erro ao converter objeto em JSON: " + conteudo, exception);
        }

        byte[] bytes = outputStream.toByteArray();
        return Optional.of(new ByteArrayInputStream(bytes));
    }

    @Override
    public Object getUnderlyingImplementation() {
        return objectMapper;
    }
}
