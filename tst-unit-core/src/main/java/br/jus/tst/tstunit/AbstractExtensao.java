package br.jus.tst.tstunit;

import java.lang.annotation.Annotation;

/**
 * Implementação base para todas as extensões.
 * 
 * @author Thiago Miranda
 * @since 6 de jul de 2016
 */
public abstract class AbstractExtensao<A extends Annotation> implements Extensao<A> {

    protected Class<?> classeTeste;

    /**
     * Cria uma nova instância da extensão para a classe de teste informada.
     * 
     * @param classeTeste
     *            a classe de teste
     */
    public AbstractExtensao(Class<?> classeTeste) {
        this.classeTeste = classeTeste;
    }

    @Override
    public Class<?> getClasseTeste() {
        return classeTeste;
    }
}
