package br.jus.tst.tstunit;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.*;

/**
 * Uma extensão adiciona novas funcionalidades ao contexto de testes.
 * 
 * @author Thiago Miranda
 * @since 5 de jul de 2016
 */
public interface Extensao<A extends Annotation> {

    /**
     * Obtém a classe de testes.
     * 
     * @return a classe de testes.
     */
    Class<?> getClasseTeste();

    /**
     * Verifica se a extensão está habilitada para a classe de teste informada.
     * 
     * @param classeTeste
     *            a classe de teste
     * @return {@code true}/{@code false}
     */
    boolean isHabilitada();

    /**
     * Obtém uma instância da classe de testes com algum comportamento customizado para que funcione com essa extensão habilitada.
     * 
     * @param classeTeste
     *            a classe de teste
     * @return a instância persoanlizada - vazio indica que será utilizada a instância padrão
     */
    default <T> Optional<T> getInstanciaPersonalizadaParaTestes() {
        return Optional.empty();
    };

    /**
     * Método a ser executado antes dos testes da classe de testes serem executados.
     * 
     * @param instancia
     *            a instância utilizada para testes
     */
    default void beforeTestes(Object instancia) {
    }

    /**
     * Método a ser executado após todos os testes da classe de testes serem executados.
     */
    default void afterTestes() {
    }

    /**
     * Inicializa a extensão. Este método é invocado assim que a execução dos testes é acionada.
     * 
     * @param configuracao
     *            as configurações do TST Unit
     * @param notifier
     *            notificador do JUnit
     * @throws TstUnitException
     *             caso ocorra algum erro durante a operação
     * @see Runner#run(RunNotifier)
     */
    void inicializar(Configuracao configuracao, RunNotifier notifier) throws TstUnitException;

    /**
     * Obtém um {@link Statement} configurado para a extensão.
     * 
     * @param defaultStatement
     * @param method
     *            o método de teste sendo executado
     * @return o Statement criado
     * @throws TstUnitException
     *             caso ocorra algum erro durante a operação
     */
    Statement criarStatement(Statement defaultStatement, FrameworkMethod method) throws TstUnitException;
}
