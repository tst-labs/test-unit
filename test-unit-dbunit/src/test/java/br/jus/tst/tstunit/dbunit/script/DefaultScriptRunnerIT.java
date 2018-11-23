package br.jus.tst.tstunit.dbunit.script;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.*;
import java.sql.*;

import org.junit.*;

import br.jus.tst.tstunit.TestUnitException;
import br.jus.tst.tstunit.dbunit.AbstractIT;
import br.jus.tst.tstunit.dbunit.jdbc.JdbcConnectionSupplier;

/**
 * Testes de integração da {@link DefaultScriptRunner}.
 * 
 * @author Thiago Miranda
 * @since 6 de jul de 2017
 */
public class DefaultScriptRunnerIT extends AbstractIT {

    private ScriptRunner defaultScriptRunner;

    @BeforeClass
    public static void setUpClass() {
        DefaultScriptRunner.setLogsDir(new File("target"));
    }

    @Test
    public void deveriaExecutarScriptDdl() throws SQLException, TestUnitException, IOException {
        JdbcConnectionSupplier connectionSupplier = criarConnectionSupplier();
        String sql = "CREATE TABLE TesteScript (id INTEGER NOT NULL, descricao VARCHAR(100) NOT NULL, PRIMARY KEY(id));";

        try (Connection connection = connectionSupplier.get(); StringReader reader = new StringReader(sql)) {
            defaultScriptRunner = new DefaultScriptRunner(connection, false, false);
            defaultScriptRunner.runScript(reader);
        }

        try (Connection connection = connectionSupplier.get()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM TesteScript")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    assertThat(resultSet.next(), is(false));
                }
            }
        }
    }
}
