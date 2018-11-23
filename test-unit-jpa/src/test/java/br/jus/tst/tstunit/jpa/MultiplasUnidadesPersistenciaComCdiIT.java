package br.jus.tst.tstunit.jpa;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.annotation.*;
import java.util.List;

import javax.inject.*;
import javax.persistence.EntityManager;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.jus.tst.tstunit.TestUnitRunner;
import br.jus.tst.tstunit.cdi.HabilitarCdiAndMockito;
import br.jus.tst.tstunit.jpa.HabilitarJpa.UnidadePersistencia;
import br.jus.tst.tstunit.jpa.MultiplasUnidadesPersistenciaComCdiIT.*;
import br.jus.tst.tstunit.jpa.cdi.*;

/**
 * Testes de integração da {@link JpaExtensao} utilizando múltiplas unidades de persistência do JPA junto com CDI.
 * 
 * @author Thiago Miranda
 * @since 03 de out de 2016
 */
@RunWith(TestUnitRunner.class)
@HabilitarJpa(unidadesPersistencia = { @UnidadePersistencia(nome = "testePU", qualifierClass = TestePU.class),
        @UnidadePersistencia(nome = "teste2PU", qualifierClass = Teste2PU.class) }, geradorSchema = GeradorSchemaCdi.class)
@HabilitarCdiAndMockito
@AdditionalClasses({ EntityManagerFactoryProducerExtension.class })
public class MultiplasUnidadesPersistenciaComCdiIT {

    @Qualifier
    @Retention(RUNTIME)
    @Target(FIELD)
    public static @interface TestePU {

    }

    @Qualifier
    @Retention(RUNTIME)
    @Target(FIELD)
    public static @interface Teste2PU {

    }

    @Inject
    @TestePU
    private EntityManager entityManager1;

    @Inject
    @Teste2PU
    private EntityManager entityManager2;

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
