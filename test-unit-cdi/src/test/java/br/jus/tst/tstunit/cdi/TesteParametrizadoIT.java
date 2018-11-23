package br.jus.tst.tstunit.cdi;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.*;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;

import br.jus.tst.tstunit.parameters.TestUnitParameterizedRunnerFactory;

/**
 * Testes de integração para a funcionalidade de testes parametrizados.
 * 
 * @author Thiago Miranda
 * @since 31 de ago de 2016
 */
@RunWith(Parameterized.class)
@UseParametersRunnerFactory(TestUnitParameterizedRunnerFactory.class)
@HabilitarCdiAndMockito
public class TesteParametrizadoIT {

    /**
     * Classe utilizada nos testes.
     * 
     * @author Thiago Miranda
     * @since 14 de jul de 2016
     */
    public static class Objeto {

    }

    @Parameters
    public static Collection<Object[]> parametros() {
        return Arrays.asList(new Object[] { 1, "1" }, new Object[] { 2, "2" });
    }

    @Parameter(0)
    public int numero;
    @Parameter(1)
    public String numeroString;

    @Inject
    private Objeto instancia;

    @Test
    public void teste() {
        assertThat(String.valueOf(numero), is(equalTo(numeroString)));
        assertThat("Não injetou dependências", instancia, is(notNullValue(Object.class)));
    }
}
