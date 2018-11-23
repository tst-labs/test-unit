package br.jus.tst.tstunit.dbunit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.*;

import org.junit.Test;

import br.jus.tst.tstunit.TestUnitException;
import br.jus.tst.tstunit.dbunit.jdbc.JdbcConnectionSupplier;
import br.jus.tst.tstunit.dbunit.script.*;

/**
 * Testes de integração de cenários que utilizem as anotações {@link RodarScriptAntes} e {@link RodarScriptDepois}.
 * 
 * @author Thiago Miranda
 * @since 6 de jul de 2017
 */
public class RodarScriptIT extends AbstractIT {

    @Test
    @RodarScriptAntes("script-funcao.sql")
    public void deveriaRodarScriptDdlFuncoesAntes() throws SQLException, TestUnitException {
        JdbcConnectionSupplier connectionSupplier = criarConnectionSupplier();
        
        try (Connection connection = connectionSupplier.get()) {
            try (CallableStatement statement = connection.prepareCall("{ ? = CALL SOMAR(1,2) }")) {
                statement.registerOutParameter(1, Types.INTEGER);
                statement.execute();
                int resultado = statement.getInt(1);
                assertThat(resultado, is(equalTo(3)));
            }
        }
    }
}
