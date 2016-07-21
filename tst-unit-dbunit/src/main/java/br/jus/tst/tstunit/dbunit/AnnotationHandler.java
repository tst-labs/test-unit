package br.jus.tst.tstunit.dbunit;

import java.util.Optional;

import org.junit.runners.model.FrameworkMethod;

/**
 * Responsável por identificar e processar anotações em classes e métodos de teste.
 * 
 * @author Thiago Miranda
 * @since 21 de jul de 2016
 *
 * @param <T>
 *            o tipo de instância criada
 */
@FunctionalInterface
public interface AnnotationHandler<T> {

    /**
     * Obtém uma instância de {@code T} a partir do método de testes informado.
     * 
     * @param method
     *            o método de testes
     * @return a instância criada - vazio caso a anotação não exista na classe nem no método de testes
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    Optional<T> processar(FrameworkMethod method);
}