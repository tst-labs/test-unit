package br.jus.tst.tstunit.jpa;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.annotation.*;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.*;
import org.junit.runner.RunWith;

import br.jus.tst.tstunit.TstUnitRunner;
import br.jus.tst.tstunit.jpa.HabilitarJpa.UnidadePersistencia;
import br.jus.tst.tstunit.jpa.MultiplasUnidadesPersistenciaIT.*;
import br.jus.tst.tstunit.jpa.cache.EntityManagerCacheProducer;

/**
 * Testes de integração da {@link JpaExtensao} utilizando múltiplas unidades de persistência do JPA.
 * 
 * @author Thiago Miranda
 * @since 06 de out de 2016
 */
@RunWith(TstUnitRunner.class)
@HabilitarJpa(unidadesPersistencia = { @UnidadePersistencia(nome = "testePU", qualifierClass = TestePU.class),
        @UnidadePersistencia(nome = "teste2PU", qualifierClass = Teste2PU.class) })
public class MultiplasUnidadesPersistenciaIT {

    @Retention(RUNTIME)
    @Target(FIELD)
    public static @interface TestePU {

    }

    @Retention(RUNTIME)
    @Target(FIELD)
    public static @interface Teste2PU {

    }

    private EntityManager entityManager1;
    private EntityManager entityManager2;

    @Before
    public void setUp() {
        entityManager1 = EntityManagerCacheProducer.getEntityManager(TestePU.class);
        entityManager2 = EntityManagerCacheProducer.getEntityManager(Teste2PU.class);
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void deveriaCarregarEntityManagers() {
        List resultado = entityManager1.createQuery("SELECT e FROM " + Entidade.class.getName() + " e").getResultList();
        assertThat("Problemas na criação da entidade no banco 1", resultado.size(), is(equalTo(0)));

        resultado = entityManager2.createQuery("SELECT e FROM " + Entidade.class.getName() + " e").getResultList();
        assertThat("Problemas na criação da entidade no banco 2", resultado.size(), is(equalTo(0)));

        assertThat(entityManager1.getEntityManagerFactory().getProperties().get("hibernate.ejb.persistenceUnitName"), is(equalTo("testePU")));
        assertThat(entityManager2.getEntityManagerFactory().getProperties().get("hibernate.ejb.persistenceUnitName"), is(equalTo("teste2PU")));
    }
}
