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
    private static final int TAMANHO_PADRAO_LISTA = 2;

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
     * Identifica uma anotação presente em na classe de teste ou no método informado.
     * </p>
     * <p>
     * A lista retornada pode ter de 0 (nenhum) a 2 elementos, sendo que:
     * </p>
     * <ul>
     * <li><strong>Vazia</strong>: indica que nenhuma anotação do tipo informado existe na classe nem no método</li>
     * <li><strong>1 elemento</strong>: indica que a anotação foi encontrada uma única vez - pode ser da classe ou do método</li>
     * <li><strong>2 elementos</strong>: indica que a anotação foi encontrada tanto na classe quanto no método. Nesse caso, os elementos estarão nessa ordem
     * (classe, método)</li>
     * </ul>
     * 
     * @param method
     *            o método e teste
     * @param annotationType
     *            o tipo de anotação desejado
     * @return a lista contendo as anotações (pode estar vazia, mas nunca será {@code null}
     */
    public <T extends Annotation> List<T> getAnnotationsFromMethodOrClass(FrameworkMethod method, Class<T> annotationType) {
        List<T> annotations = new ArrayList<>(TAMANHO_PADRAO_LISTA);
        CollectionUtils.addIgnoreNull(annotations, classeTeste.getAnnotation(annotationType));
        CollectionUtils.addIgnoreNull(annotations, method.getAnnotation(annotationType));
        LOGGER.debug("Anotações identificadas: {}", annotations);
        return annotations;
    }
}
