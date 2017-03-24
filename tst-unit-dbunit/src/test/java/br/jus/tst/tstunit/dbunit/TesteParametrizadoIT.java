package br.jus.tst.tstunit.dbunit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.*;
import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;

import br.jus.tst.tstunit.*;
import br.jus.tst.tstunit.dbunit.dataset.UsarDataSet;
import br.jus.tst.tstunit.dbunit.jdbc.JdbcConnectionSupplier;
import br.jus.tst.tstunit.dbunit.script.*;
import br.jus.tst.tstunit.parameters.TstUnitParameterizedRunnerFactory;

/**
 * Testes de integração para a funcionalidade de testes parametrizados.
 * 
 * @author Thiago Miranda
 * @since 30 de ago de 2016
 */
@RunWith(Parameterized.class)
@UseParametersRunnerFactory(TstUnitParameterizedRunnerFactory.class)
public class TesteParametrizadoIT extends AbstractIT {

    @Parameters
    public static Collection<Object[]> parametros() {
        return Arrays.asList(new Object[] { 1, "1" }, new Object[] { 2, "2" });
    }

    @Parameter(0)
    public int numero;
    @Parameter(1)
    public String numeroString;

    @Test
    @RodarScriptAntes("script-antes.sql")
    @RodarScriptDepois("script-depois.sql")
    @UsarDataSet("entidades.xml")
    public void teste() throws TstUnitException, SQLException {
        assertThat(String.valueOf(numero), is(equalTo(numeroString)));

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
}
