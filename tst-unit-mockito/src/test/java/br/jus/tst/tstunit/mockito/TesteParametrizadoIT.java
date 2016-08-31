package br.jus.tst.tstunit.mockito;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;
import org.mockito.Mock;

import br.jus.tst.tstunit.parameters.TstUnitParameterizedRunnerFactory;

/**
 * Testes de integração para a funcionalidade de testes parametrizados.
 * 
 * @author Thiago Miranda
 * @since 31 de ago de 2016
 */
@RunWith(Parameterized.class)
@UseParametersRunnerFactory(TstUnitParameterizedRunnerFactory.class)
@HabilitarMockito
public class TesteParametrizadoIT {

    @Parameters
    public static Collection<Object[]> parametros() {
        return Arrays.asList(new Object[] { 1, "1" }, new Object[] { 2, "2" });
    }

    @Parameter(0)
    public int numero;
    @Parameter(1)
    public String numeroString;

    @Mock
    private Object objeto;

    @Test
    public void teste() {
        assertThat(String.valueOf(numero), is(equalTo(numeroString)));
        assertThat("Mock não instanciado automaticamente", objeto, is(notNullValue()));
    }
}
