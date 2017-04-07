package br.jus.tst.tstunit.jaxrs;

import java.io.InputStream;
import java.util.Optional;

/**
 * Utilizado para obter instâncias de objetos Java a partir de JSONs.
 * 
 * @author Thiago Miranda
 * @since 4 de abr de 2017
 */
public interface JsonToObjectConverter {

    <T> T jsonToObject(InputStream stream, TypeReference<T> typeReference);

    <T> T jsonToObject(String content, TypeReference<T> typeReference);

    <T> T jsonToObject(InputStream stream, Class<T> type);

    <T> T jsonToObject(String content, Class<T> type);

    Optional<InputStream> objectToJson(Object conteudo);

    Object getUnderlyingImplementation();
}
