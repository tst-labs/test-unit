package br.jus.tst.tstunit;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.*;
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

    private transient final String basePackage;
    private transient final Class<?> classeTeste;

    /**
     * Cria uma nova instância que utilizará como base o pacote informado para escanear extensões.
     * 
     * @param basePackage
     *            pacote-base para a varredura por extensões
     * @param classeTeste
     *            classe com os testes a serem executados
     */
    public ExtensoesLoader(String basePackage, Class<?> classeTeste) {
        this.basePackage = basePackage;
        this.classeTeste = classeTeste;
    }

    @SuppressWarnings("rawtypes")
    public List<Extensao<?>> carregarExtensoes() {
        LOGGER.debug("Carregando extensões a partir do pacote: {}", basePackage);
        Reflections reflections = new Reflections(new ConfigurationBuilder().filterInputsBy(new FilterBuilder().includePackage(basePackage))
                .setUrls(ClasspathHelper.forPackage(basePackage)).setScanners(new SubTypesScanner()));
        Set<Class<? extends AbstractExtensao>> classesExtensoes = reflections.getSubTypesOf(AbstractExtensao.class);
        return classesExtensoes.stream().map(this::newInstance).filter(extensao -> extensao.isHabilitada()).collect(Collectors.toList());
    }

    private Extensao<?> newInstance(Class<?> classeExtensao) {
        try {
            LOGGER.debug("Criando nova instância da extensão {} para a classe de teste: {}", classeExtensao, classeTeste);
            return (Extensao<?>) classeExtensao.getConstructor(classeTeste.getClass()).newInstance(classeTeste);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException exception) {
            throw new RuntimeException("Erro ao instanciar classe da extensão: " + classeExtensao, exception);
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
