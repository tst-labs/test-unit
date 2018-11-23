package br.jus.tst.tstunit.jpa;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.*;

import javax.persistence.EntityManager;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;

import br.jus.tst.tstunit.jpa.cache.EntityManagerCacheProducer;
import br.jus.tst.tstunit.parameters.TstUnitParameterizedRunnerFactory;

/**
 * Testes de integração para a funcionalidade de testes parametrizados.
 * 
 * @author Thiago Miranda
 * @since 31 de ago de 2016
 */
@RunWith(Parameterized.class)
@UseParametersRunnerFactory(TstUnitParameterizedRunnerFactory.class)
@HabilitarJpa(nomeUnidadePersistencia = "testePU")
public class TesteParametrizadoIT {

    @Parameters
    public static Collection<Object[]> parametros() {
        return Arrays.asList(new Object[] { 1, "1" }, new Object[] { 2, "2" });
    }

    @Parameter(0)
    public int numero;
    @Parameter(1)
    public String numeroString;

    private EntityManager entityManager;

    @Before
    public void setUp() {
        entityManager = EntityManagerCacheProducer.getUniqueEntityManager().get();
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void teste() {
        assertThat(String.valueOf(numero), is(equalTo(numeroString)));
        List resultado = entityManager.createQuery("SELECT e FROM " + Entidade.class.getName() + " e").getResultList();
        assertThat(resultado.size(), is(equalTo(0)));
    }
}
