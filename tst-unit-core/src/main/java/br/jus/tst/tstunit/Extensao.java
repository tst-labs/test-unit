package br.jus.tst.tstunit;

import java.util.Optional;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.*;

/**
 * 
 * @author Thiago Miranda
 * @since 5 de jul de 2016
 */
public interface Extensao {

    /**
     * 
     * @param classeTeste
     * @return
     */
    boolean isHabilitada(Class<?> classeTeste);

    /**
     * @param classeTeste
     * @return
     */
    default <T> Optional<T> getInstanciaPersonalizadaParaTestes(Class<T> classeTeste) {
        return Optional.empty();
    };

    /**
     * 
     * @param instancia
     * @return
     */
    default void beforeTestes(Object instancia) {
    }

    /**
     * 
     * @param instancia
     */
    default void afterTestes(Class<?> classeTeste) {
    }

    /**
     * Inicializa a extensão. Este método é invocado assim que a execução dos testes é acionada.
     * 
     * @param classeTeste
     *            classe de teste
     * @param configuracao
     * @param notifier
     *            notificador do JUnit
     * @see Runner#run(RunNotifier)
     */
    void inicializar(Class<?> classeTeste, Configuracao configuracao, RunNotifier notifier);

    /**
     * Obtém um {@link Statement} configurado para a extensão.
     * 
     * @param classeTeste
     * @param defaultStatement
     * @param method
     *            o método de teste sendo executado
     * @return o Statement criado
     * @throws TstUnitException
     */
    Statement criarStatement(Class<?> classeTeste, Statement defaultStatement, FrameworkMethod method) throws TstUnitException;
}
