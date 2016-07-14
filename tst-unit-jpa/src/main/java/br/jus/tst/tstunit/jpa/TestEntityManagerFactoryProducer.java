package br.jus.tst.tstunit.jpa;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.*;
import javax.persistence.*;

import org.slf4j.*;

/**
 * Classe que provê acesso a instâncias de {@link EntityManagerFactory} utilizadas nos testes.
 * 
 * @author ThiagoColbert
 * @since 29 de mai de 2016
 */
public class TestEntityManagerFactoryProducer implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestEntityManagerFactoryProducer.class);

    private static final long serialVersionUID = 902185891912929393L;

    private static String nomeUnidadePersistencia;

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
        LOGGER.info("Inicializando contexto de persistência: {}", nomeUnidadePersistencia);
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
            LOGGER.info("Encerrando contexto de persistência: {}", entityManagerFactory);
            entityManagerFactory.close();
        }
    }
}
