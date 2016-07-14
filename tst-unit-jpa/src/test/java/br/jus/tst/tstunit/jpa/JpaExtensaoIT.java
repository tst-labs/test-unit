package br.jus.tst.tstunit.jpa;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.persistence.*;

import org.junit.*;
import org.junit.runner.RunWith;

import br.jus.tst.tstunit.TstUnitRunner;
import br.jus.tst.tstunit.jpa.JpaExtensaoIT.GeradorSchemaPu;

/**
 * Testes de integração da {@link JpaExtensao}.
 * 
 * @author Thiago Miranda
 * @since 14 de jul de 2016
 */
@RunWith(TstUnitRunner.class)
@HabilitarJpa(persistenceUnitName = "testePU", geradorSchema = GeradorSchemaPu.class)
public class JpaExtensaoIT {

    /**
     * Gerador de <em>schema</em> utilizado nos testes.
     * 
     * @author Thiago Miranda
     * @since 14 de jul de 2016
     */
    public static class GeradorSchemaPu implements GeradorSchema {

        @Override
        public void criar() {
            TestEntityManagerFactoryProducer entityManagerFactoryProducer = new TestEntityManagerFactoryProducer();
            entityManagerFactory = entityManagerFactoryProducer.criarEntityManagerFactory();
        }
    }

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

    private static EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    @AfterClass
    public static void tearDownClass() {
        entityManagerFactory.close();
    }

    @Before
    public void setUp() {
        entityManager = entityManagerFactory.createEntityManager();
    }

    @After
    public void tearDown() {
        entityManager.close();
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void test() {
        List resultado = entityManager.createQuery("SELECT e FROM " + Entidade.class.getName() + " e").getResultList();
        assertThat(resultado.size(), is(equalTo(0)));
    }
}
