package br.jus.tst.tstunit.dbunit.script;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.function.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.*;

import br.jus.tst.tstunit.dbunit.DBUnitException;

/**
 * Classe responsável por executar scripts de banco de dados.
 * 
 * @author Thiago Miranda
 * @since 7 de jul de 2016
 */
public class ExecutorScripts implements Serializable {

    private static final long serialVersionUID = 1089276331709009669L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorScripts.class);

    private static final String DIRETORIO_LOGS_PADRAO = "target";

    private transient final Supplier<Connection> jdbcConnectionSupplier;
    private transient final List<String> scriptsBefore;
    private transient final List<String> scriptsAfter;

    private transient Function<Connection, ScriptRunner> scriptRunnerSupplier;

    /**
     * Cria uma nova instância definindo os scripts a serem executados antes e após os testes.
     * 
     * @param scriptsBefore
     *            scripts a serem executados antes dos testes
     * @param scriptsAfter
     *            scripts a serem executados após os testes
     * @param jdbcConnectionSupplier
     *            utilizado para obter as conexões JDBC
     * @throws NullPointerException
     *             caso {@code jdbcConnectionSupplier} seja {@code null}
     */
    public ExecutorScripts(List<String> scriptsBefore, List<String> scriptsAfter, Supplier<Connection> jdbcConnectionSupplier) {
        this.scriptsBefore = CollectionUtils.isEmpty(scriptsBefore) ? Collections.emptyList() : new ArrayList<>(scriptsBefore);
        this.scriptsAfter = CollectionUtils.isEmpty(scriptsAfter) ? Collections.emptyList() : new ArrayList<>(scriptsAfter);
        this.jdbcConnectionSupplier = Objects.requireNonNull(jdbcConnectionSupplier, "jdbcConnectionSupplier");
        identificarScriptRunner(jdbcConnectionSupplier);
    }

    @SuppressWarnings("unchecked")
    private void identificarScriptRunner(Supplier<Connection> jdbcConnectionSupplier) {
        String provedorBancoDados;
        try (Connection connection = jdbcConnectionSupplier.get()) {
            provedorBancoDados = StringUtils.deleteWhitespace(connection.getMetaData().getDatabaseProductName());
            LOGGER.debug("Provedor de banco de dados: {}", provedorBancoDados);
        } catch (SQLException exception) {
            throw new DBUnitException("Erro ao identificar provedor de banco de dados", exception);
        }

        try {
            Class<? extends ScriptRunner> scriptRunnerClass = (Class<? extends ScriptRunner>) Class.forName(
                    String.format("%s.%sScriptRunner", ScriptRunner.class.getPackage().getName(), provedorBancoDados), false, Thread.currentThread().getContextClassLoader());
            LOGGER.info("Utilizando implementação do {} para executar Scripts SQL", provedorBancoDados);
            scriptRunnerSupplier = (connection) -> newScriptRunner(scriptRunnerClass, connection);
        } catch (ClassNotFoundException exception) {
            LOGGER.debug("Não há implementação específica para o {}", provedorBancoDados, exception);
            LOGGER.info("Utilizando implementação própria para executar Scripts SQL");
            scriptRunnerSupplier = (connection) -> new DefaultScriptRunner(connection, false, true);
            DefaultScriptRunner.setLogsDir(new File(DIRETORIO_LOGS_PADRAO));
        }
    }

    private ScriptRunner newScriptRunner(Class<? extends ScriptRunner> scriptRunnerClass, Connection connection) {
        try {
            return ConstructorUtils.invokeConstructor(scriptRunnerClass, new Object[] { connection }, new Class<?>[] { Connection.class });
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException exception) {
            throw new DBUnitException("Erro ao instanciar executor de Scripts: " + scriptRunnerClass, exception);
        }
    }

    /**
     * Executa os arquivos de script definidos como "antes".
     * 
     * @throws IOException
     *             caso ocorra algum erro ao ler o conteúdo de algum arquivo
     * @throws SQLException
     *             caso ocorra algum erro ao executar algum script
     */
    public void executarScriptsAntes() throws SQLException, IOException {
        executarScripts(scriptsBefore);
    }

    /**
     * Executa os arquivos de script definidos como "depois".
     * 
     * @throws IOException
     *             caso ocorra algum erro ao ler o conteúdo de algum arquivo
     * @throws SQLException
     *             caso ocorra algum erro ao executar algum script
     */
    public void executarScriptsDepois() throws SQLException, IOException {
        executarScripts(scriptsAfter);
    }

    private void executarScripts(List<String> arquivosScript) throws SQLException, IOException {
        for (String arquivoScript : arquivosScript) {
            LOGGER.info("Executando script: {}", arquivoScript);
            try (Connection connection = jdbcConnectionSupplier.get(); Reader fileReader = openReader(arquivoScript)) {
                scriptRunnerSupplier.apply(connection).runScript(fileReader);
                connection.commit();
            }
        }
    }

    private InputStreamReader openReader(String arquivoScript) throws FileNotFoundException {
        Optional<InputStream> inputStream = Optional.ofNullable(Thread.currentThread().getContextClassLoader().getResourceAsStream(arquivoScript));
        return new InputStreamReader(inputStream.orElseThrow(() -> new FileNotFoundException(arquivoScript)));
    }
}
