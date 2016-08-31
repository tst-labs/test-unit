package br.jus.tst.tstunit.cdi;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.jboss.weld.exceptions.DeploymentException;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.jus.tst.tstunit.TstUnitRunner;

/**
 * Testes de integração da {@link CdiExtensao}.
 * 
 * @author Thiago Miranda
 * @since 31 de ago de 2016
 */
@RunWith(TstUnitRunner.class)
@HabilitarCdiAndMockito
public class TesteComErroInicializacaoIT {

    /**
     * Classe utilizada nos testes.
     * 
     * @author Thiago Miranda
     * @since 31 de ago de 2016
     */
    public static class Objeto {

        @Inject
        private String dependenciaInexistente;

        public String getDependenciaInexistente() {
            return dependenciaInexistente;
        }
    }

    @Inject
    private Objeto instancia;

    @Test(expected = DeploymentException.class)
    public void deveriaCarregarCdi() {
        assertThat("Não carregou o contêiner CDI", instancia, is(notNullValue()));
    }
}
