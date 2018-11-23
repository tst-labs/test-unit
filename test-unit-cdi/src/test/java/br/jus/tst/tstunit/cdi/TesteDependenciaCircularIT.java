package br.jus.tst.tstunit.cdi;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jglue.cdiunit.ProducesAlternative;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import br.jus.tst.tstunit.TestUnitRunner;

/**
 * Testes de integração da {@link CdiExtensao} que verifica o funcionamento com dependencias circulares.
 * 
 * @author Thiago Miranda
 * @since 01 de set de 2016
 */
@RunWith(TestUnitRunner.class)
@HabilitarCdiAndMockito
public class TesteDependenciaCircularIT {

    /**
     * Classe utilizada nos testes.
     * 
     * @author Thiago Miranda
     * @since 01 de set de 2016
     */
    public static class Objeto1 {

        @Inject
        private Objeto2 objeto2;

        public Objeto2 getObjeto2() {
            return objeto2;
        }
    }

    /**
     * Classe utilizada nos testes.
     * 
     * @author Thiago Miranda
     * @since 01 de set de 2016
     */
    public static class Objeto2 {
    }

    @Inject
    private Objeto1 instancia;

    @Produces
    @ProducesAlternative
    @Mock
    private Objeto2 mockObjeto2;

    @Test
    public void deveriaCarregarCdi() {
        assertThat("Não carregou o contêiner CDI", instancia, is(notNullValue()));
    }
}
