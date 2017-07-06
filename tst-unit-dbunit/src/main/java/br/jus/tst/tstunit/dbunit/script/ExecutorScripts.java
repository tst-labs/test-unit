package br.jus.tst.tstunit.dbunit.script;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.function.*;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.*;

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

        if (H2ScriptRunner.isHabilitado()) {
            LOGGER.info("Utilizando implementação do H2 para executar Scripts SQL");
            scriptRunnerSupplier = (connection) -> new H2ScriptRunner(connection);
        } else {
            LOGGER.info("Utilizando implementação própria para executar Scripts SQL");
            scriptRunnerSupplier = (connection) -> new DefaultScriptRunner(connection, false, true);
            DefaultScriptRunner.setLogsDir(new File(DIRETORIO_LOGS_PADRAO));
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
