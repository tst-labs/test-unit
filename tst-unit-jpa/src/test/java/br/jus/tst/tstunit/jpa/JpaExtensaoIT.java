package br.jus.tst.tstunit.jpa;

import static br.jus.tst.tstunit.jpa.GeradorSchemaPu.getEntityManagerFactory;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.persistence.*;

import org.junit.*;
import org.junit.runner.RunWith;

import br.jus.tst.tstunit.TstUnitRunner;

/**
 * Testes de integração da {@link JpaExtensao}.
 * 
 * @author Thiago Miranda
 * @since 14 de jul de 2016
 */
@RunWith(TstUnitRunner.class)
@HabilitarJpa(nomeUnidadePersistencia = "testePU", geradorSchema = GeradorSchemaPu.class)
public class JpaExtensaoIT {

    /**
     * Entidade de testes.
     * 
     * @author Thiago Miranda
     * @since 14 de jul de 2016
     */
    @Entity
    @Table
    public static class Entidade {

        @Id
        private int id;
    }

    private EntityManager entityManager;

    @Before
    public void setUp() {
        entityManager = getEntityManagerFactory().createEntityManager();
    }

    @After
    public void tearDown() {
        entityManager.close();
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void deveriaCarregarEntityManager() {
        List resultado = entityManager.createQuery("SELECT e FROM " + Entidade.class.getName() + " e").getResultList();
        assertThat(resultado.size(), is(equalTo(0)));
    }
}
