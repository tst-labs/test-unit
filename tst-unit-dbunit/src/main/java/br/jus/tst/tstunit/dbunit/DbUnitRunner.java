package br.jus.tst.tstunit.dbunit;

import java.io.*;
import java.util.*;

import org.apache.commons.lang3.*;
import org.junit.runners.model.*;
import org.slf4j.*;

import br.jus.tst.tstunit.*;

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

    private final class DbUnitStatement extends Statement {

        private final DbUnitDatabaseLoader databaseLoader;
        private final Statement defaultStatement;
        private final ScriptRunner scriptRunner;

        /**
         * Cria um novo statement.
         * 
         * @param databaseLoader
         *            utilizado para efetuar operações sobre os dados do banco - opcional
         * @param scriptRunner
         *            utilizado para rodar scripts antes e após a execução do statement
         * @param defaultStatement
         *            o statement padrão (pai)
         */
        public DbUnitStatement(DbUnitDatabaseLoader databaseLoader, ScriptRunner scriptRunner, Statement defaultStatement) {
            this.databaseLoader = databaseLoader;
            this.scriptRunner = Objects.requireNonNull(scriptRunner, "scriptRunner");
            this.defaultStatement = Objects.requireNonNull(defaultStatement, "defaultStatement");
        }

        @Override
        public void evaluate() throws Throwable {
            scriptRunner.executarScriptAntes();
            if (databaseLoader != null) {
                databaseLoader.carregarBancoDados();
            }

            try {
                defaultStatement.evaluate();
            } finally {
                if (databaseLoader != null) {
                    databaseLoader.limparBancoDados();
                }

                scriptRunner.executarScriptDepois();
            }
        }
    }

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
     */
    public DbUnitRunner(Class<?> classeTeste, String nomeSchema, Configuracao configuracao) {
        super();
        this.classeTeste = Objects.requireNonNull(classeTeste, "classeTeste");
        this.nomeSchema = nomeSchema;
        this.configuracao = Objects.requireNonNull(configuracao, "configuracao");
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

        ScriptRunner scriptRunner = criarScriptRunner(method, jdbcConnectionSupplier);
        DbUnitDatabaseLoader databaseLoader = criarDatabaseLoader(method, jdbcConnectionSupplier);

        return new DbUnitStatement(databaseLoader, scriptRunner, statement);
    }

    private DbUnitDatabaseLoader criarDatabaseLoader(FrameworkMethod method, JdbcConnectionSupplier jdbcConnectionSupplier) {
        DbUnitDatabaseLoader databaseLoader;
        UsarDataSet usarDataSet = getUsarDatasetAnnotation(method);

        if (usarDataSet != null) {
            String datasetsDir = StringUtils.defaultIfBlank(getDiretorioDatasetsConfigurado(), DIRETORIO_DATASETS_PADRAO);

            databaseLoader = new DbUnitDatabaseLoader(buildCaminhoArquivo(datasetsDir, usarDataSet.value()), jdbcConnectionSupplier);
            databaseLoader.setSchema(nomeSchema);

        } else {
            LOGGER.warn("Nenhuma anotação @UsarDataSet definida no teste nem na classe: {}", method.getName());
            databaseLoader = null;
        }

        return databaseLoader;
    }

    private ScriptRunner criarScriptRunner(FrameworkMethod method, JdbcConnectionSupplier jdbcConnectionSupplier) {
        String scriptsDir = StringUtils.defaultIfBlank(getDiretorioScriptsConfigurado(), DIRETORIO_SCRIPTS_PADRAO);

        String scriptBefore;
        RodarScriptAntes rodarScriptAntes = getRodarScriptAntesAnnotation(method);
        if (rodarScriptAntes != null) {
            scriptBefore = buildCaminhoArquivo(scriptsDir, rodarScriptAntes.value());
        } else {
            scriptBefore = null;
        }

        String scriptAfter;
        RodarScriptDepois rodarScriptDepois = getRodarScriptDepoisAnnotation(method);
        if (rodarScriptDepois != null) {
            scriptAfter = buildCaminhoArquivo(scriptsDir, rodarScriptDepois.value());
        } else {
            scriptAfter = null;
        }

        return new ScriptRunner(scriptBefore, scriptAfter, jdbcConnectionSupplier);
    }

    private RodarScriptAntes getRodarScriptAntesAnnotation(FrameworkMethod method) {
        return ObjectUtils.defaultIfNull(method.getAnnotation(RodarScriptAntes.class), classeTeste.getAnnotation(RodarScriptAntes.class));
    }

    private RodarScriptDepois getRodarScriptDepoisAnnotation(FrameworkMethod method) {
        return ObjectUtils.defaultIfNull(method.getAnnotation(RodarScriptDepois.class), classeTeste.getAnnotation(RodarScriptDepois.class));
    }

    private UsarDataSet getUsarDatasetAnnotation(FrameworkMethod method) {
        return ObjectUtils.defaultIfNull(method.getAnnotation(UsarDataSet.class), classeTeste.getAnnotation(UsarDataSet.class));
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