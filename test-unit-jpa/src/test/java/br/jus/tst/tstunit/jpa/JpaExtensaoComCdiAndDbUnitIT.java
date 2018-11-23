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

import br.jus.tst.tstunit.TestUnitRunner;
import br.jus.tst.tstunit.cdi.HabilitarCdiAndMockito;
import br.jus.tst.tstunit.dbunit.HabilitarDbUnit;
import br.jus.tst.tstunit.dbunit.dataset.UsarDataSet;
import br.jus.tst.tstunit.jpa.cdi.*;

/**
 * Testes de integração da {@link JpaExtensao} com CDI e DB Unit habilitados.
 * 
 * @author Thiago Miranda
 * @since 14 de jul de 2016
 */
@RunWith(TestUnitRunner.class)
@HabilitarJpa(nomeUnidadePersistencia = "testePUIntegradoDbUnit", geradorSchema = GeradorSchemaCdi.class)
@HabilitarCdiAndMockito
@AdditionalClasses({ EntityManagerFactoryProducerExtension.class })
@HabilitarDbUnit(nomeSchema = "IT2")
@UsarDataSet("entidades.xml")
public class JpaExtensaoComCdiAndDbUnitIT {

    @Inject
    private EntityManager entityManager;

    @Test
    @SuppressWarnings("rawtypes")
    public void deveriaCarregarEntityManager() {
        List resultado = entityManager.createQuery("SELECT e FROM " + Entidade.class.getName() + " e").getResultList();
        assertThat(resultado.size(), is(equalTo(2)));
    }
}
