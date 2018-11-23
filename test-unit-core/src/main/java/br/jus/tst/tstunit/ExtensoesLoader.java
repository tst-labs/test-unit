package br.jus.tst.tstunit;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.*;

/**
 * Classe responsável por carregar as extensões existentes.
 * 
 * @author Thiago Miranda
 * @since 11 de jul de 2016
 * @see Extensao
 */
public class ExtensoesLoader implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensoesLoader.class);

    private static final long serialVersionUID = -6036814877869370373L;

    private final String basePackage;
    private final Class<?> classeTeste;

    /**
     * Cria uma nova instância que utilizará como base o pacote informado para escanear extensões.
     * 
     * @param basePackage
     *            pacote-base para a varredura por extensões
     * @param classeTeste
     *            classe com os testes a serem executados
     * @throws NullPointerException
     *             caso qualquer parâmetro seja {@code null}
     */
    public ExtensoesLoader(String basePackage, Class<?> classeTeste) {
        this.basePackage = Objects.requireNonNull(basePackage, "basePackage");
        this.classeTeste = Objects.requireNonNull(classeTeste, "classeTeste");
    }

    /**
     * Carrega todas as extensões disponíveis no <em>classpath</em>.
     * 
     * @return as extensões carregadas
     * @throws TstUnitRuntimeException
     *             caso ocorra algum erro ao carregar as extensões
     */
    public List<Extensao<?>> carregarExtensoes() {
        LOGGER.debug("Carregando extensões a partir do pacote: {}", basePackage);
        return new Reflections(basePackage, new SubTypesScanner()).getSubTypesOf(Extensao.class).stream().filter(type -> !Modifier.isAbstract(type.getModifiers()))
                .map(this::newInstance).filter(Extensao::isHabilitada).collect(Collectors.toList());
    }

    private Extensao<?> newInstance(Class<?> classeExtensao) {
        try {
            LOGGER.debug("Criando nova instância da extensão {} para a classe de teste: {}", classeExtensao, classeTeste);
            return (Extensao<?>) classeExtensao.getConstructor(classeTeste.getClass()).newInstance(classeTeste);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException exception) {
            throw new TstUnitRuntimeException("Erro ao instanciar classe da extensão: " + classeExtensao, exception);
        }
    }

    public String getBasePackage() {
        return basePackage;
    }

    @SuppressWarnings("rawtypes")
    public Class getClasseTeste() {
        return classeTeste;
    }
}
