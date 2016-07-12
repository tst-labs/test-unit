package br.jus.tst.tstunit.jpa;

import java.io.Serializable;

import javax.enterprise.inject.*;
import javax.inject.Inject;
import javax.persistence.*;

import org.slf4j.Logger;

/**
 * Provê acesso a instâncias de {@link EntityManager}.
 * 
 * @author ThiagoColbert
 * @since 29 de mai de 2016
 */
public class TestEntityManagerProducer implements Serializable {

    private static final long serialVersionUID = 3075682395966745202L;

    @Inject
    private transient Logger logger;
    @Inject
    private transient EntityManagerFactory entityManagerFactory;

    /**
     * Obtém uma instância de {@link EntityManager}.
     * 
     * @return a instância criada
     */
    @Produces
    public EntityManager criarEntityManager() {
        logger.debug("Criando novo EntityManager");
        return entityManagerFactory.createEntityManager();
    }

    /**
     * Fecha um {@link EntityManager} criado anteriormente.
     * 
     * @param entityManager
     *            instância a ser fechada
     */
    public void fecharEntityManager(@Disposes EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
