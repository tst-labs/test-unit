package br.jus.tst.tstunit.jpa;

import javax.persistence.EntityManagerFactory;

import br.jus.tst.tstunit.jpa.cdi.*;

/**
 * Gerador de <em>schema</em> utilizado nos testes.
 * 
 * @author Thiago Miranda
 * @since 14 de jul de 2016
 */
public class GeradorSchemaPu implements GeradorSchema {

    private static EntityManagerFactory entityManagerFactory;

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    @Override
    public void criar() {
        entityManagerFactory = new TestEntityManagerFactoryProducer(TestEntityManagerFactoryProducerExtension.getUnidadesPersistencia()[0]).create(null);
    }

    @Override
    public void destruir() {
        entityManagerFactory.close();
    }
}