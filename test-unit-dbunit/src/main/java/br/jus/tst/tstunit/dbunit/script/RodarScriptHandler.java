package br.jus.tst.tstunit.dbunit.script;

import java.io.File;
import java.sql.Connection;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.runners.model.FrameworkMethod;
import org.slf4j.*;

import br.jus.tst.tstunit.annotation.*;

/**
 * Classe que auxilia a criação de instâncias de {@link ExecutorScripts} a partir de anotações {@link RodarScriptAntes} e {@link RodarScriptDepois}.
 * 
 * @author Thiago Miranda
 * @since 21 de jul de 2016
 */
public class RodarScriptHandler implements AnnotationHandler<ExecutorScripts> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RodarScriptHandler.class);

    private final String scriptsDirectory;
    private final Supplier<Connection> jdbcConnectionSupplier;
    private final AnnotationExtractor annotationExtractor;

    /**
     * Cria uma nova instância.
     * 
     * @param scriptsDirectory
     *            o diretório onde se encontram os <em>scripts</em>
     * @param jdbcConnectionSupplier
     *            utilizado para obter conexões JDBC
     * @param annotationExtractor
     *            utilizado para identificar anotações na classe e nos métodos de teste
     * @throws NullPointerException
     *             caso qualquer parâmetro seja {@code null}
     */
    public RodarScriptHandler(String scriptsDirectory, Supplier<Connection> jdbcConnectionSupplier, AnnotationExtractor annotationExtractor) {
        this.scriptsDirectory = Objects.requireNonNull(scriptsDirectory, "scriptsDirectory");
        this.jdbcConnectionSupplier = Objects.requireNonNull(jdbcConnectionSupplier, "jdbcConnectionSupplier");
        this.annotationExtractor = Objects.requireNonNull(annotationExtractor, "annotationExtractor");
    }

    @Override
    public Optional<ExecutorScripts> processar(FrameworkMethod method) {
        List<String> scriptsBefore = annotationExtractor.getAnnotationsFromMethodOrClass(method, RodarScriptAntes.class).stream()
                .flatMap(anotacao -> Arrays.stream(anotacao.value())).map(caminhoArquivo -> buildCaminhoArquivo(scriptsDirectory, caminhoArquivo))
                .collect(Collectors.toList());
        LOGGER.debug("Scripts a serem executados antes dos testes: {}", scriptsBefore);

        List<String> scriptsAfter = annotationExtractor.getAnnotationsFromMethodOrClass(method, RodarScriptDepois.class).stream()
                .flatMap(anotacao -> Arrays.stream(anotacao.value())).map(caminhoArquivo -> buildCaminhoArquivo(scriptsDirectory, caminhoArquivo))
                .collect(Collectors.toList());
        LOGGER.debug("Scripts a serem executados após os testes: {}", scriptsAfter);

        return Optional.of(new ExecutorScripts(scriptsBefore, scriptsAfter, jdbcConnectionSupplier));
    }

    private String buildCaminhoArquivo(String directory, String nomeArquivo) {
        return directory + File.separatorChar + nomeArquivo;
    }
}
