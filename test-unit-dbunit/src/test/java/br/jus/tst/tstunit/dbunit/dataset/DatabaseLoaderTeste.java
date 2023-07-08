package br.jus.tst.tstunit.dbunit.dataset;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.*;
import java.util.Arrays;
import java.util.function.Supplier;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;

import br.jus.tst.tstunit.TestUnitRunner;
import br.jus.tst.tstunit.mockito.HabilitarMockito;

/**
 * Testes unitários da {@link DatabaseLoader}.
 * 
 * @author Thiago Miranda
 * @since 18 de out de 2017
 */
@RunWith(TestUnitRunner.class)
@HabilitarMockito
public class DatabaseLoaderTeste {

    @Mock
    private Supplier<Connection> jdbcConnectionSupplier;
    @Mock
    private IDataSet dataSet;

    @Mock
    private DatabaseOperation operacaoPreTestes1;
    @Mock
    private DatabaseOperation operacaoPreTestes2;
    @Mock
    private DatabaseOperation operacaoPosTestes1;
    @Mock
    private DatabaseOperation operacaoPosTestes2;

    private DatabaseLoader databaseLoader;

    private OperacaoDataSet operacao1;
    private OperacaoDataSet operacao2;

    @Before
    public void setUp() {
        operacao1 = OperacaoDataSet.nova().comDataSet(dataSet).operacaoPreTestes(operacaoPreTestes1).operacaoPosTestes(operacaoPosTestes1).build();
        operacao2 = OperacaoDataSet.nova().comDataSet(dataSet).operacaoPreTestes(operacaoPreTestes2).operacaoPosTestes(operacaoPosTestes2).build();

        databaseLoader = new DatabaseLoader(Arrays.asList(operacao1, operacao2), jdbcConnectionSupplier);
    }

    @Test
    public void deveriaCarregarBancoDados() throws DatabaseUnitException, SQLException {
        Connection connection = mock(Connection.class);

        when(jdbcConnectionSupplier.get()).thenReturn(connection);

        databaseLoader.carregarBancoDados();

        InOrder inOrder = inOrder(operacaoPreTestes1, operacaoPreTestes2);
        inOrder.verify(operacaoPreTestes1).execute(any(DatabaseConnection.class), eq(dataSet));
        inOrder.verify(operacaoPreTestes2).execute(any(DatabaseConnection.class), eq(dataSet));

        verify(jdbcConnectionSupplier).get();

        verifyNoMoreInteractions(jdbcConnectionSupplier, operacaoPreTestes1, operacaoPreTestes2);
        verifyNoInteractions(operacaoPosTestes1, operacaoPosTestes2);
    }

    @Test
    public void deveriaLimparBancoDados() throws DatabaseUnitException, SQLException {
        Connection connection = mock(Connection.class);

        when(jdbcConnectionSupplier.get()).thenReturn(connection);

        databaseLoader.limparBancoDados();

        InOrder inOrder = inOrder(operacaoPosTestes2, operacaoPosTestes1);
        inOrder.verify(operacaoPosTestes2).execute(any(DatabaseConnection.class), eq(dataSet));
        inOrder.verify(operacaoPosTestes1).execute(any(DatabaseConnection.class), eq(dataSet));

        verify(jdbcConnectionSupplier).get();

        verifyNoMoreInteractions(jdbcConnectionSupplier, operacaoPosTestes1, operacaoPosTestes2);
        verifyNoInteractions(operacaoPreTestes1, operacaoPreTestes2);
    }
}
