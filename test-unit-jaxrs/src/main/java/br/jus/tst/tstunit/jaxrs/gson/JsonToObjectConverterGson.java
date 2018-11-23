package br.jus.tst.tstunit.jaxrs.gson;

import java.io.*;
import java.util.*;

import org.apache.commons.io.IOUtils;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import br.jus.tst.tstunit.jaxrs.*;

/**
 * Implementação de {@link JsonToObjectConverter} que utiliza o {@link Gson}.
 * 
 * @author Thiago Miranda
 * @since 12 de jun de 2017
 * 
 * @deprecated Para obter a resposta em JSON, utilizar {@link MockResponse#getObjetoRespostaUsando(JsonToObjectFunction)}. Esta classe será removida em versões
 *             futuras.
 */
@Deprecated
public class JsonToObjectConverterGson implements JsonToObjectConverter {

    private Gson gson;

    /**
     * Cria um novo conversor utilizando a instância de {@link Gson} informada.
     * 
     * @param gson
     *            instância a ser utilizada
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    public JsonToObjectConverterGson(Gson gson) {
        this.gson = Objects.requireNonNull(gson, "gson");
    }

    /**
     * Cria uma nova instância utilizando um {@link Gson} <em>default</em>.
     * 
     * @see Gson#Gson()
     */
    public JsonToObjectConverterGson() {
        this(new Gson());
    }

    @Override
    public <T> T jsonToObject(InputStream stream, TypeReference<T> typeReference) {
        try (JsonReader jsonReader = new JsonReader(new InputStreamReader(stream))) {
            return gson.fromJson(jsonReader, typeReference.getType());
        } catch (IOException exception) {
            throw new JsonConverterException(exception);
        }
    }

    @Override
    public <T> T jsonToObject(String content, TypeReference<T> typeReference) {
        return gson.fromJson(content, typeReference.getType());
    }

    @Override
    public <T> T jsonToObject(InputStream stream, Class<T> type) {
        try (JsonReader jsonReader = new JsonReader(new InputStreamReader(stream))) {
            return gson.fromJson(jsonReader, type);
        } catch (IOException exception) {
            throw new JsonConverterException(exception);
        }
    }

    @Override
    public <T> T jsonToObject(String content, Class<T> type) {
        return gson.fromJson(content, type);
    }

    @Override
    public Optional<InputStream> objectToJson(Object conteudo) {
        try {
            return conteudo != null ? Optional.of(IOUtils.toInputStream(gson.toJson(conteudo))) : Optional.empty();
        } catch (JsonIOException exception) {
            throw new JsonConverterException("Erro ao converter objeto para JSON: " + conteudo, exception);
        }
    }

    @Override
    public Object getUnderlyingImplementation() {
        return gson;
    }
}
