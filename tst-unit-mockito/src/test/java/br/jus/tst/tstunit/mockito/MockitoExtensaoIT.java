package br.jus.tst.tstunit.mockito;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import br.jus.tst.tstunit.TstUnitRunner;

/**
 * Testes de integração da {@link MockitoExtensao}.
 * 
 * @author Thiago Miranda
 * @since 14 de jul de 2016
 */
@RunWith(TstUnitRunner.class)
@HabilitarMockito
public class MockitoExtensaoIT {

    @Mock
    private Object mock;

    @Test
    public void deveriaCarregarMocks() {
        assertThat("Não carregou os mocks", mock, is(notNullValue(Object.class)));
    }
}
