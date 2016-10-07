package br.jus.tst.tstunit.dbunit;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import br.jus.tst.tstunit.*;
import br.jus.tst.tstunit.dbunit.dataset.UsarDataSet;
import br.jus.tst.tstunit.dbunit.jdbc.JdbcConnectionSupplier;
import br.jus.tst.tstunit.dbunit.script.*;

/**
 * Testes de integração com o HSQLDB.
 * 
 * @author Thiago Miranda
 * @since 7 de out de 2016
 */
/*
 * OBS.: Para rodar este teste individualmente, é necessário passar como parâmetro: -DnomeArquivoPropriedades=tstunit-hsqldb.properties
 */
@RunWith(TstUnitRunner.class)
@HabilitarDbUnit
public class HsqldbIT {

    private static final String NOME_ARQUIVO_PROPRIEDADES = "tstunit-hsqldb.properties";

    @Test
    public void deveriaUtilizarArquivoCorreto() throws TstUnitException, SQLException {
        JdbcConnectionSupplier connectionSupplier = criarConnectionSupplier();

        try (Connection connection = connectionSupplier.get()) {
            assertThat(connection.getMetaData().getDriverName().toUpperCase(), containsString("HSQL"));
        }
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
        return new JdbcConnectionSupplier(new Configuracao().setNomeArquivoPropriedades(NOME_ARQUIVO_PROPRIEDADES).carregar().getSubPropriedades("jdbc"));
    }
}
