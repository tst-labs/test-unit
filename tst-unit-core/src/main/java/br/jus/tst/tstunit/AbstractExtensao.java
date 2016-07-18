package br.jus.tst.tstunit;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;

import org.apache.commons.lang3.Validate;

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

    @Override
    @SuppressWarnings("unchecked")
    public boolean isHabilitada() {
        return getClasseTeste().getAnnotation((Class<A>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]) != null;
    }

    /**
     * Verifica se a extensão está habilitada. Caso não esteja, uma exceção é lançada.
     * 
     * @throws IllegalStateException
     *             caso a extensão não esteja habilitada
     * @see #isHabilitada()
     */
    protected void assertExtensaoHabilitada() {
        Validate.validState(isHabilitada(), "Extensão não habilitada");
    }
}
