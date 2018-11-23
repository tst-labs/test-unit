package br.jus.tst.tstunit.dbunit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.*;

import org.junit.Test;

import br.jus.tst.tstunit.TestUnitException;
import br.jus.tst.tstunit.dbunit.dataset.*;
import br.jus.tst.tstunit.dbunit.dataset.UsarDataSet.Operacao;
import br.jus.tst.tstunit.dbunit.jdbc.JdbcConnectionSupplier;
import br.jus.tst.tstunit.dbunit.script.*;

/**
 * Testes de integração de cenários que utilizem a anotação {@link UsarDataSet}.
 * 
 * @author Thiago Miranda
 * @since 24 de mar de 2017
 */
@RodarScriptAntes("script-antes.sql")
@RodarScriptDepois("script-depois.sql")
@UsarDataSet("entidades.xml")
public class UsarDataSetIT extends AbstractIT {

    @Test
    public void deveriaImportarDataset() throws TestUnitException, SQLException {
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
    @UsarDataSet(value = "entidades2.xml", operacaoPreTestes = Operacao.INSERT, operacaoPosTestes = Operacao.NONE)
    public void deveriaUtilizarDatasetsDeClasseEMetodo() throws SQLException, TestUnitException {
        JdbcConnectionSupplier connectionSupplier = criarConnectionSupplier();

        try (Connection connection = connectionSupplier.get()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Entidade")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    assertThat(resultSet.next(), is(true));
                    assertThat(resultSet.getInt(1), is(equalTo(4)));
                }
            }
        }
    }

    @Test
    @UsarDataSets({ @UsarDataSet(value = "entidades2.xml", operacaoPreTestes = Operacao.INSERT, operacaoPosTestes = Operacao.NONE),
            @UsarDataSet(value = "entidades3.xml", operacaoPreTestes = Operacao.INSERT, operacaoPosTestes = Operacao.NONE) })
    public void deveriaUtilizarMultiplosDatasets() throws SQLException, TestUnitException {
        JdbcConnectionSupplier connectionSupplier = criarConnectionSupplier();

        try (Connection connection = connectionSupplier.get()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Entidade")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    assertThat(resultSet.next(), is(true));
                    assertThat(resultSet.getInt(1), is(equalTo(6)));
                }
            }
        }
    }
}
