package br.jus.tst.tstunit.jpa;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.*;
import javax.inject.Inject;
import javax.persistence.*;

import org.slf4j.Logger;

/**
 * Classe que provê acesso a instâncias de {@link EntityManagerFactory} utilizadas nos testes.
 * 
 * @author ThiagoColbert
 * @since 29 de mai de 2016
 */
public class TestEntityManagerFactoryProducer implements Serializable {

    private static final long serialVersionUID = 902185891912929393L;

    private static String nomeUnidadePersistencia;

    @Inject
    private transient Logger logger;

    public static String getNomeUnidadePersistencia() {
        return nomeUnidadePersistencia;
    }

    public static void setNomeUnidadePersistencia(String nomeUnidadePersistencia) {
        TestEntityManagerFactoryProducer.nomeUnidadePersistencia = nomeUnidadePersistencia;
    }

    /**
     * Obtém uma instância de {@link EntityManagerFactory}.
     * 
     * @return a instância criada
     */
    @Produces
    @ApplicationScoped
    public EntityManagerFactory criarEntityManagerFactory() {
        logger.info("Inicializando contexto de persistência");
        return Persistence.createEntityManagerFactory(nomeUnidadePersistencia);
    }

    /**
     * Fecha uma instância de {@link EntityManagerFactory} criada anteriormente.
     * 
     * @param entityManagerFactory
     *            a instância a ser fechada
     */
    public void fecharEntityManagerFactory(@Disposes EntityManagerFactory entityManagerFactory) {
        if (entityManagerFactory.isOpen()) {
            logger.info("Encerrando contexto de persistência: {}", entityManagerFactory);
            entityManagerFactory.close();
        }
    }
}
