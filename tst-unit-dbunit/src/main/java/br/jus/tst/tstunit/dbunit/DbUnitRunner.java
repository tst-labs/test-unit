package br.jus.tst.tstunit.dbunit;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.junit.runners.model.*;
import org.slf4j.*;

import br.jus.tst.tstunit.*;
import br.jus.tst.tstunit.dbunit.jdbc.*;

/**
 * Classe responsável por rodar o DBUnit em métodos de teste.
 * 
 * @author Thiago Miranda
 * @since 4 de jul de 2016
 */
public class DbUnitRunner implements Serializable {

    private static final long serialVersionUID = -1862055645135908142L;
    private static final Logger LOGGER = LoggerFactory.getLogger(DbUnitRunner.class);

    private static final String DIRETORIO_DATASETS_PADRAO = "datasets";
    private static final String DIRETORIO_SCRIPTS_PADRAO = "scripts";

    private transient final Class<?> classeTeste;
    private transient final Configuracao configuracao;
    private transient final String nomeSchema;

    /**
     * Cria uma nova instância definindo todos os parâmetros.
     * 
     * @param classeTeste
     *            a classe dos testes
     * @param nomeSchema
     *            nome do schema de banco de dados
     * @param configuracao
     *            configurações a serem utilizadas
     * @throws NullPointerException
     *             caso {@code classeTeste} ou {@code configuracao} seja {@code null}
     * @throws TstUnitException
     *             caso ocorra algum erro ao carregar as configurações
     */
    public DbUnitRunner(Class<?> classeTeste, String nomeSchema, Configuracao configuracao) throws TstUnitException {
        this.classeTeste = Objects.requireNonNull(classeTeste, "classeTeste");
        this.nomeSchema = StringUtils.stripToNull(nomeSchema);
        this.configuracao = Objects.requireNonNull(configuracao, "configuracao");
        this.configuracao.carregar();
    }

    /**
     * Cria um {@link Statement} que funcione com o DBUnit.
     * 
     * @param statement
     *            o statement original
     * @param method
     *            o método de teste
     * @return o statement criado
     * @throws NullPointerException
     *             caso qualquer parâmetro seja {@code null}
     * @throws TstUnitException
     *             caso ocorra algum erro durante a operação
     */
    public Statement criarStatement(Statement statement, FrameworkMethod method) throws TstUnitException {
        Objects.requireNonNull(statement, "statement");
        Objects.requireNonNull(method, "method");

        Properties propriedadesJdbc = getConnfiguracoesJdbc();
        JdbcConnectionSupplier jdbcConnectionSupplier = new JdbcConnectionSupplier(propriedadesJdbc);

        return DbUnitStatement.aPartirDo(statement).usandoDatabaseLoader(criarDatabaseLoader(method, jdbcConnectionSupplier))
                .usandoScriptRunner(criarScriptRunner(method, jdbcConnectionSupplier)).usandoGeradorDtd(criarGeradorDtd(method, jdbcConnectionSupplier))
                .build();
    }

    private GeradorDtd criarGeradorDtd(FrameworkMethod method, JdbcConnectionSupplier jdbcConnectionSupplier) throws TstUnitException {
        GeradorDtd geradorDtd;

        GerarDtd gerarDtd = method.getAnnotation(GerarDtd.class);
        if (gerarDtd != null) {
            geradorDtd = new GeradorDtd(jdbcConnectionSupplier, new File(gerarDtd.value()));
            geradorDtd.setDataTypeFactory(getDataTypeFactory());
        } else {
            geradorDtd = null;
        }

        return geradorDtd;
    }

    private DbUnitDatabaseLoader criarDatabaseLoader(FrameworkMethod method, JdbcConnectionSupplier jdbcConnectionSupplier) throws TstUnitException {
        DbUnitDatabaseLoader databaseLoader;

        Optional<UsarDataSet> usarDataSet = Arrays.stream(getAnnotationsFromMethodOrClass(method, UsarDataSet.class)).filter(Objects::nonNull).findFirst();

        if (usarDataSet.isPresent()) {
            String datasetsDir = getDiretorioDatasets();

            databaseLoader = new DbUnitDatabaseLoader(buildCaminhoArquivo(datasetsDir, usarDataSet.get().value()), jdbcConnectionSupplier);
            databaseLoader.setSchema(nomeSchema);
            databaseLoader.setDataTypeFactory(getDataTypeFactory());

        } else {
            LOGGER.warn("Nenhuma anotação @UsarDataSet definida no teste nem na classe: {}", method.getName());
            databaseLoader = null;
        }

        return databaseLoader;
    }

    private String getDiretorioDatasets() {
        return StringUtils.defaultIfBlank(getDiretorioDatasetsConfigurado(), DIRETORIO_DATASETS_PADRAO);
    }

    private ScriptRunner criarScriptRunner(FrameworkMethod method, JdbcConnectionSupplier jdbcConnectionSupplier) {
        String scriptsDir = StringUtils.defaultIfBlank(getDiretorioScriptsConfigurado(), DIRETORIO_SCRIPTS_PADRAO);

        List<String> scriptsBefore;
        RodarScriptAntes[] rodarScriptAntes = getAnnotationsFromMethodOrClass(method, RodarScriptAntes.class);
        if (rodarScriptAntes != null) {
            scriptsBefore = Arrays.stream(rodarScriptAntes).filter(Objects::nonNull).flatMap(anotacao -> Arrays.stream(anotacao.value()))
                    .map(caminhoArquivo -> buildCaminhoArquivo(scriptsDir, caminhoArquivo)).collect(Collectors.toList());
        } else {
            scriptsBefore = Collections.emptyList();
        }
        LOGGER.debug("Scripts a serem executados antes dos testes: {}", scriptsBefore);

        List<String> scriptsAfter;
        RodarScriptDepois[] rodarScriptDepois = getAnnotationsFromMethodOrClass(method, RodarScriptDepois.class);
        if (rodarScriptDepois != null) {
            scriptsAfter = Arrays.stream(rodarScriptDepois).filter(Objects::nonNull).flatMap(anotacao -> Arrays.stream(anotacao.value()))
                    .map(caminhoArquivo -> buildCaminhoArquivo(scriptsDir, caminhoArquivo)).collect(Collectors.toList());
        } else {
            scriptsAfter = Collections.emptyList();
        }
        LOGGER.debug("Scripts a serem executados após os testes: {}", scriptsAfter);

        return new ScriptRunner(scriptsBefore, scriptsAfter, jdbcConnectionSupplier);
    }

    @SuppressWarnings("unchecked")
    private <T extends Annotation> T[] getAnnotationsFromMethodOrClass(FrameworkMethod method, Class<T> annotationType) {
        final int tamanho = 2;
        T[] annotations = (T[]) Array.newInstance(annotationType, tamanho);
        annotations[0] = classeTeste.getAnnotation(annotationType);
        annotations[1] = method.getAnnotation(annotationType);
        return annotations;
    }

    private String buildCaminhoArquivo(String directory, String nomeArquivo) {
        return directory + File.separatorChar + nomeArquivo;
    }

    private String getDiretorioDatasetsConfigurado() {
        return (String) getConfiguracoesDbUnit().get("datasets.dir");
    }

    private String getDiretorioScriptsConfigurado() {
        return (String) getConfiguracoesDbUnit().get("scripts.dir");
    }

    private IDataTypeFactory getDataTypeFactory() throws TstUnitException {
        String dataTypeFactoryClass = (String) getConfiguracoesDbUnit().get("dataTypeFactoryClass");
        try {
            return (IDataTypeFactory) Class.forName(dataTypeFactoryClass).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException exception) {
            throw new TstUnitException("Erro ao instanciar classe de DataTypeFactory configurada: " + dataTypeFactoryClass, exception);
        }
    }

    private Properties getConnfiguracoesJdbc() {
        return configuracao.getSubPropriedades("jdbc");
    }

    private Properties getConfiguracoesDbUnit() {
        return configuracao.getSubPropriedades("dbunit");
    }

    public Class<?> getClasseTeste() {
        return classeTeste;
    }
}