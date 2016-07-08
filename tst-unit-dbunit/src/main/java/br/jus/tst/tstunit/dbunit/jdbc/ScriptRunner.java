package br.jus.tst.tstunit.dbunit.jdbc;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.h2.tools.RunScript;
import org.slf4j.*;

/**
 * Classe responsável por executar scripts de banco de dados.
 * 
 * @author Thiago Miranda
 * @since 7 de jul de 2016
 */
public class ScriptRunner implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptRunner.class);

    private static final long serialVersionUID = 1089276331709009669L;

    private transient final Supplier<Connection> jdbcConnectionSupplier;
    private transient final String scriptBefore;
    private transient final String scriptAfter;

    /**
     * 
     * @param scriptBefore
     * @param scriptAfter
     * @param jdbcConnectionSupplier
     * @throws NullPointerException
     *             caso {@code jdbcConnectionSupplier} seja {@code null}
     */
    public ScriptRunner(String scriptBefore, String scriptAfter, Supplier<Connection> jdbcConnectionSupplier) {
        this.scriptBefore = scriptBefore;
        this.scriptAfter = scriptAfter;
        this.jdbcConnectionSupplier = Objects.requireNonNull(jdbcConnectionSupplier, "jdbcConnectionSupplier");
    }

    /**
     * Executa o arquivo de script definido como "antes".
     * 
     * @throws IOException
     *             caso ocorra algum erro ao ler o conteúdo do arquivo
     * @throws SQLException
     *             caso ocorra algum erro ao executar o script
     */
    public void executarScriptAntes() throws SQLException, IOException {
        executarScript(scriptBefore);
    }

    /**
     * Executa o arquivo de script definido como "depois".
     * 
     * @throws IOException
     *             caso ocorra algum erro ao ler o conteúdo do arquivo
     * @throws SQLException
     *             caso ocorra algum erro ao executar o script
     */
    public void executarScriptDepois() throws SQLException, IOException {
        executarScript(scriptAfter);
    }

    private void executarScript(String arquivoScript) throws SQLException, IOException {
        if (StringUtils.isNotBlank(arquivoScript)) {
            LOGGER.debug("Executando script: {}", arquivoScript);
            try (Connection connection = jdbcConnectionSupplier.get(); Reader fileReader = openReader(arquivoScript)) {
                RunScript.execute(connection, fileReader);
                connection.commit();
            }
        }
    }

    private InputStreamReader openReader(String arquivoScript) throws FileNotFoundException {
        Optional<InputStream> inputStream = Optional.ofNullable(Thread.currentThread().getContextClassLoader().getResourceAsStream(arquivoScript));
        return new InputStreamReader(inputStream.orElseThrow(() -> new FileNotFoundException(arquivoScript)));
    }
}
