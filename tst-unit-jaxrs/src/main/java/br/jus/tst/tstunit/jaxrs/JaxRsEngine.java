package br.jus.tst.tstunit.jaxrs;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

/**
 * <p>
 * Classe principal da biblioteca.
 * </p>
 * 
 * <p>
 * Os métodos que iniciam a criação de requisições HTTP aceitam um parâmetro {@code uriTemplate} que pode corresponder a uma URI completa ou também pode
 * corresponder a uma String contendo os placeholders utilizados por {@link String#format(String, Object...)}.
 * </p>
 * 
 * <p>
 * Exemplo:
 * 
 * <pre>
 * JaxRsEngine engine = ...;
 * engine.get("/recursos/%d").pathParams(1); // irá gerar uma URI "/recursos/1"
 * </pre>
 * </p>
 * 
 * @author Thiago Miranda
 * @since 29 de mar de 2017
 */
public interface JaxRsEngine {

    /**
     * Inicia a criação de uma requisição HTTP GET.
     * 
     * @param uriTemplate
     *            template da URI da requisição
     * @return a requisição sendo criada
     */
    MockRequest get(String uriTemplate);

    /**
     * Inicia a criação de uma requisição HTTP POST.
     * 
     * @param uriTemplate
     *            template da URI da requisição
     * @return a requisição sendo criada
     */
    MockRequest post(String uriTemplate);

    /**
     * Inicia a criação de uma requisição HTTP PUT.
     * 
     * @param uriTemplate
     *            template da URI da requisição
     * @return a requisição sendo criada
     */
    MockRequest put(String uriTemplate);

    /**
     * Inicia a criação de uma requisição HTTP DELETE.
     * 
     * @param uriTemplate
     *            template da URI da requisição
     * @return a requisição sendo criada
     */
    MockRequest delete(String uriTemplate);

    /**
     * Registra uma instância como sendo um recurso JAX-RS (um objeto cuja classe está anotada com {@literal @}{@link Path}, por exemplo).
     * 
     * @param instancia
     *            a instância a ser registrada
     * @return {@code this}, para chamadas encadeadas de método
     */
    JaxRsEngine registrarRecurso(Object instancia);

    /**
     * Registra um {@link Provider} do JAX-RS.
     * 
     * @param providerClass
     *            classe a ser registrada como <em>provider</em>
     * @return {@code this}, para chamadas encadeadas de método
     */
    JaxRsEngine registrarProvider(Class<?> providerClass);

    /**
     * Define um objeto no contexto do JAX-RS.
     * 
     * @param classe
     *            classe do objeto a ser definido
     * @param instancia
     *            objeto a ser definido no contexto
     * @param <T>
     *            o tipo de objeto
     * @return {@code this}, para chamadas encadeadas de método
     */
    <T> JaxRsEngine definirObjetoContexto(Class<T> classe, T instancia);

    /**
     * Obtém a instância do framework sendo utilizado como implementação.
     * 
     * @return a instância
     */
    Object getImplementacaoSubjacente();
}
