package br.jus.tst.tstunit;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.junit.runners.model.*;

/**
 * Implementação base para todas as extensões.
 * 
 * @author Thiago Miranda
 * @since 6 de jul de 2016
 */
public abstract class AbstractExtensao<A extends Annotation> implements Extensao<A>, Serializable {

    private static final long serialVersionUID = 6185295908403836977L;

    protected final Class<?> classeTeste;

    /**
     * Cria uma nova instância da extensão para a classe de teste informada.
     * 
     * @param classeTeste
     *            a classe de teste
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    public AbstractExtensao(Class<?> classeTeste) {
        this.classeTeste = Objects.requireNonNull(classeTeste, "classeTeste");
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

    @Override
    public Statement criarStatement(Statement defaultStatement, FrameworkMethod method) throws TestUnitException {
        assertExtensaoHabilitada();
        return defaultStatement;
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
