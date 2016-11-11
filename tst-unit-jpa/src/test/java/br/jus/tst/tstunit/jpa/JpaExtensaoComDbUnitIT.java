package br.jus.tst.tstunit.jpa;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.*;
import org.junit.runner.RunWith;

import br.jus.tst.tstunit.TstUnitRunner;
import br.jus.tst.tstunit.dbunit.HabilitarDbUnit;
import br.jus.tst.tstunit.dbunit.dataset.UsarDataSet;
import br.jus.tst.tstunit.jpa.cache.EntityManagerCacheProducer;

/**
 * Testes de integração da {@link JpaExtensao} com DB Unit habilitado.
 * 
 * @author Thiago Miranda
 * @since 14 de jul de 2016
 */
@RunWith(TstUnitRunner.class)
@HabilitarJpa(nomeUnidadePersistencia = "testePUIntegradoDbUnit")
@HabilitarDbUnit(nomeSchema = "IT2")
@UsarDataSet("entidades.xml")
public class JpaExtensaoComDbUnitIT {

    private EntityManager entityManager;

    @Before
    public void setUp() {
        entityManager = EntityManagerCacheProducer.getUniqueEntityManager();
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void deveriaCarregarEntityManager() {
        List resultado = entityManager.createQuery("SELECT e FROM " + Entidade.class.getName() + " e").getResultList();
        assertThat(resultado.size(), is(equalTo(2)));
    }
}
