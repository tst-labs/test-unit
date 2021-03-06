package br.jus.tst.tstunit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;

import br.jus.tst.tstunit.parameters.TestUnitParameterizedRunnerFactory;

/**
 * Testes de integração para a funcionalidade de testes parametrizados.
 * 
 * @author Thiago Miranda
 * @since 30 de ago de 2016
 */
@RunWith(Parameterized.class)
@UseParametersRunnerFactory(TestUnitParameterizedRunnerFactory.class)
public class TesteParametrizadoIT {

    @Parameters
    public static Collection<Object[]> parametros() {
        return Arrays.asList(new Object[] { 1, "1" }, new Object[] { 2, "2" });
    }

    @Parameter(0)
    public int numero;
    @Parameter(1)
    public String numeroString;

    @Test
    public void teste() {
        assertThat(String.valueOf(numero), is(equalTo(numeroString)));
    }
}
