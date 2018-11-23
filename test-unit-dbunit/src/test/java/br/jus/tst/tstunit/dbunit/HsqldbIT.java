package br.jus.tst.tstunit.dbunit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.sql.*;

import org.junit.Test;

import br.jus.tst.tstunit.TestUnitException;
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
 * OBS.: Para rodar este teste individualmente, é necessário passar como parâmetro: -DnomeArquivoPropriedades=testunit-hsqldb.properties
 * 
 * Rodando pelo Maven (mvn test), isso já é feito automaticamente.
 */
public class HsqldbIT extends AbstractIT {

    private static final String NOME_ARQUIVO_PROPRIEDADES = "testunit-hsqldb.properties";

    @Test
    public void deveriaUtilizarArquivoCorreto() throws TestUnitException, SQLException {
        JdbcConnectionSupplier connectionSupplier = criarConnectionSupplier(NOME_ARQUIVO_PROPRIEDADES);

        try (Connection connection = connectionSupplier.get()) {
            assertThat(connection.getMetaData().getDriverName().toUpperCase(), containsString("HSQL"));
        }

        assertThat(System.getProperty("nomeArquivoPropriedades"), is(equalTo(NOME_ARQUIVO_PROPRIEDADES)));
    }

    @Test
    @RodarScriptAntes("script-antes.sql")
    @RodarScriptDepois("script-depois.sql")
    @UsarDataSet("entidades.xml")
    public void deveriaImportarDataset() throws TestUnitException, SQLException {
        JdbcConnectionSupplier connectionSupplier = criarConnectionSupplier(NOME_ARQUIVO_PROPRIEDADES);

        try (Connection connection = connectionSupplier.get()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Entidade")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    assertThat(resultSet.next(), is(true));
                    assertThat(resultSet.getInt(1), is(equalTo(2)));
                }
            }
        }
    }
}
