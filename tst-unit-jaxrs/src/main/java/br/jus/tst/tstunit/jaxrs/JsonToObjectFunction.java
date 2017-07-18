package br.jus.tst.tstunit.jaxrs;

import java.io.InputStream;

/**
 * Permite a conversão de um conteúdo JSON em um objeto Java.
 * 
 * @author Thiago Miranda
 * @since 18 de jul de 2017
 *
 * @param <T>
 *            o tipo de objeto gerado
 */
@FunctionalInterface
public interface JsonToObjectFunction<T> {

    /**
     * Converte o JSON representando por um <em>stream</em> em um objeto Java.
     * 
     * @param stream
     *            fonte de dados JSON
     * @return o objeto gerado
     * @throws Exception
     *             caso ocorra algum erro ao executar a operação
     */
    // lança Exception para evitar que o código cliente (tipicamente um teste) precise fazer try-catch
    T apply(InputStream stream) throws Exception; // NOSONAR
}
