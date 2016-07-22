package br.jus.tst.tstunit.dbunit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.*;
import java.sql.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import br.jus.tst.tstunit.*;
import br.jus.tst.tstunit.dbunit.dataset.UsarDataSet;
import br.jus.tst.tstunit.dbunit.dtd.GerarDtd;
import br.jus.tst.tstunit.dbunit.jdbc.JdbcConnectionSupplier;
import br.jus.tst.tstunit.dbunit.script.*;

/**
 * Testes de integração da {@link DbUnitExtensao}.
 * 
 * @author Thiago Miranda
 * @since 14 de jul de 2016
 */
@RunWith(TstUnitRunner.class)
@HabilitarDbUnit
public class DbUnitExtensaoIT {

    @Test
    @RodarScriptAntes("script-antes.sql")
    @RodarScriptDepois("script-depois.sql")
    public void deveriaRodarScriptAntesDepois() throws TstUnitException, SQLException {
        JdbcConnectionSupplier connectionSupplier = criarConnectionSupplier();

        try (Connection connection = connectionSupplier.get()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Entidade")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    assertThat(resultSet.next(), is(true));
                    assertThat(resultSet.getInt(1), is(equalTo(1)));
                }
            }
        }
    }

    @Test
    @RodarScriptAntes({ "script-antes.sql", "script-antes2.sql" })
    public void deveriaRodarMultiplosScriptsAntesEmOrdem() throws TstUnitException, SQLException {
        JdbcConnectionSupplier connectionSupplier = criarConnectionSupplier();

        try (Connection connection = connectionSupplier.get()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Entidade")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    assertThat(resultSet.next(), is(true));
                    assertThat(resultSet.getInt(1), is(equalTo(2)));
                }
            }
        }
    }

    @Test
    @GerarDtd("src/test/resources/datasets/testes.dtd")
    @RodarScriptAntes("script-antes.sql")
    @RodarScriptDepois("script-depois.sql")
    public void deveriaGerarDtd() throws URISyntaxException {
        URL dtdUrl = Thread.currentThread().getContextClassLoader().getResource("datasets/testes.dtd");
        assertThat(dtdUrl, is(notNullValue()));

        File arquivoDtd = new File(dtdUrl.toURI());
        assertThat(arquivoDtd.exists(), is(true));
    }

    @Test
    @RodarScriptAntes("script-antes.sql")
    @RodarScriptDepois("script-depois.sql")
    @UsarDataSet("entidades.xml")
    public void deveriaImportarDataset() throws TstUnitException, SQLException {
        JdbcConnectionSupplier connectionSupplier = criarConnectionSupplier();

        try (Connection connection = connectionSupplier.get()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Entidade")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    assertThat(resultSet.next(), is(true));
                    assertThat(resultSet.getInt(1), is(equalTo(2)));
                }
            }
        }
    }

    private JdbcConnectionSupplier criarConnectionSupplier() throws TstUnitException {
        return new JdbcConnectionSupplier(new Configuracao().carregar().getSubPropriedades("jdbc"));
    }
}
