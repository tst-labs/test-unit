package br.jus.tst.tstunit.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.*;

/**
 * Classe que auxilia na identificação de anotações em classes e métodos de teste.
 * 
 * @author Thiago Miranda
 * @since 21 de jul de 2016
 */
public class AnnotationExtractor implements Serializable {

    private static final long serialVersionUID = -4382015102432111794L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationExtractor.class);

    private final Class<?> classeTeste;

    /**
     * Cria uma nova instância para a classe de teste informada.
     * 
     * @param classeTeste
     *            a classe de teste
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    public AnnotationExtractor(Class<?> classeTeste) {
        this.classeTeste = Objects.requireNonNull(classeTeste, "classeTeste");
    }

    /**
     * <p>
     * Identifica todas as anotações presentes na classe de teste ou no método informado. A ordem das anotações dentro da lista segue o padrão de 1º anotações
     * da classe -> 2º anotações do método.
     * </p>
     * 
     * @param method
     *            o método e teste
     * @param annotationType
     *            a classe de anotação desejada
     * @param <T>
     *            o tipo da anotação
     * @return a lista contendo as anotações (pode estar vazia, mas nunca será {@code null}
     */
    public <T extends Annotation> List<T> getAnnotationsFromMethodOrClass(FrameworkMethod method, Class<T> annotationType) {
        List<T> annotations = new ArrayList<>();
        CollectionUtils.addAll(annotations, classeTeste.getDeclaredAnnotationsByType(annotationType));
        CollectionUtils.addAll(annotations, method.getMethod().getDeclaredAnnotationsByType(annotationType));
        LOGGER.debug("Anotações identificadas: {}", annotations);
        return annotations;
    }
}
