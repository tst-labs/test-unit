package br.jus.tst.tstunit.jpa;

import javax.persistence.*;

/**
 * Classe que provê acesso a instâncias de {@link EntityManager} utilizadas nos testes.
 * 
 * @author ThiagoColbert
 * @since 06 de out de 2016
 */
public interface EntityManagerProducer {

    /**
     * Obtém uma nova instância de {@link EntityManager}.
     * 
     * @return a instância criada
     * @throws JpaException
     *             caso ocorra algum erro ao criar a instância
     */
    default EntityManager criar() {
        try {
            return geEntityManagerFactoryProducer().criar().createEntityManager();
        } catch (PersistenceException exception) {
            throw new JpaException("Erro ao criar EntityManager", exception);
        }
    }
    
    /**
     * Destrói uma instância de {@link EntityManager} criada anteriormente.
     * 
     * @param instancia
     *            a instância a ser destruída
     */
    default void destruir(EntityManager instancia) {
        if (instancia != null && instancia.isOpen()) {
            instancia.close();
        }
    }

    /**
     * Obtém o produtor utilizado para obter instâncias de {@link EntityManagerFactory}.
     * 
     * @return o produtor
     */
    EntityManagerFactoryProducer geEntityManagerFactoryProducer();
}
