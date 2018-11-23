package br.jus.tst.tstunit.jpa;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.jus.tst.tstunit.TstUnitRunner;
import br.jus.tst.tstunit.cdi.HabilitarCdiAndMockito;
import br.jus.tst.tstunit.jpa.cdi.*;

/**
 * Testes de integração da {@link JpaExtensao} com CDI habilitado.
 * 
 * @author Thiago Miranda
 * @since 10 de out de 2016
 */
@RunWith(TstUnitRunner.class)
@HabilitarJpa(nomeUnidadePersistencia = "testePU", geradorSchema = GeradorSchemaCdi.class)
@HabilitarCdiAndMockito
@AdditionalClasses({ EntityManagerFactoryProducerExtension.class })
public class JpaExtensaoComCdiIT {

    @Inject
    private EntityManager entityManager;

    @Test
    @SuppressWarnings("rawtypes")
    public void deveriaCarregarEntityManager() {
        List resultado = entityManager.createQuery("SELECT e FROM " + Entidade.class.getName() + " e").getResultList();
        assertThat(resultado.size(), is(equalTo(0)));
    }
}
