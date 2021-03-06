package br.jus.tst.tstunit.dbunit.script;

import java.io.*;
import java.sql.*;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.h2.message.DbException;
import org.h2.tools.RunScript;
import org.slf4j.*;

/**
 * Implementação de {@link ScriptRunner} que delega a execução para o {@link RunScript} do H2.
 * 
 * @author Thiago Miranda
 * @since 6 de jul de 2017
 */
public class H2ScriptRunner implements ScriptRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(H2ScriptRunner.class);

    private transient final Connection connection;

    /**
     * Cria uma nova instância utilizando determinada conexão JDBC.
     * 
     * @param connection
     *            conexão JDBC a ser utilizada
     * @throws NullPointerException
     *             caso seja informado {@code null}
     * @throws IllegalArgumentException
     *             caso a conexão não seja válida
     */
    public H2ScriptRunner(Connection connection) {
        this.connection = Objects.requireNonNull(connection, "connection");

        try {
            Validate.isTrue(!connection.isClosed(), "Conexão inválida");
        } catch (SQLException exception) {
            throw new IllegalArgumentException("Conexão inválida", exception);
        }
    }

    @Override
    public void runScript(Reader reader) throws IOException, SQLException {
        try {
            LOGGER.debug("Executando {} na conexão JDBC {}", reader, connection);
            RunScript.execute(connection, reader);
        } catch (DbException exception) {
            throw new SQLException(exception);
        }
    }
}
