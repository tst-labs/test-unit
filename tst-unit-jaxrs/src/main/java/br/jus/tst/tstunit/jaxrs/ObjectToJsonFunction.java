package br.jus.tst.tstunit.jaxrs;

import java.io.OutputStream;

/**
 * Permite a conversão de um objeto Java em um JSON.
 * 
 * @author Thiago Miranda
 * @since 18 de jul de 2017
 */
@FunctionalInterface
public interface ObjectToJsonFunction {

    /**
     * Converte o objeto informado em um JSON, gravando-o em um <em>stream</em>.
     * 
     * @param stream
     *            utilizado para gravar o JSON gerado
     * @param value
     *            objeto a ser convertido
     * @throws Exception
     *             caso ocorra algum erro ao executar a operação
     */
    // lança Exception para evitar que o código cliente (tipicamente um teste) precise fazer try-catch
    void apply(OutputStream stream, Object value) throws Exception; // NOSONAR
}
