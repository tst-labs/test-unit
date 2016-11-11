package br.jus.tst.tstunit.jpa;

import java.util.Map;

import javax.persistence.*;

import br.jus.tst.tstunit.jpa.HabilitarJpa.UnidadePersistencia;

/**
 * Classe que provê acesso a instâncias de {@link EntityManagerFactory} utilizadas nos testes.
 * 
 * @author ThiagoColbert
 * @since 06 de out de 2016
 */
public interface EntityManagerFactoryProducer {

    /**
     * Obtém uma nova instância de {@link EntityManagerFactory}.
     * 
     * @return a instância criada
     * @throws JpaException
     *             caso ocorra algum erro ao criar a instância
     */
    default EntityManagerFactory criar() throws JpaException {
        try {
            return Persistence.createEntityManagerFactory(getUnidadePersistencia().nome(), getPropriedadesAdicionais());
        } catch (PersistenceException exception) {
            throw new JpaException("Erro ao inicializar contexto de persistência", exception);
        }
    }

    /**
     * Obtém propriedades adicionais a serem repassadas diretamente para o framework ORM.
     * 
     * @return as propriedades (pode estar vazio)
     */
    Map<String, String> getPropriedadesAdicionais();

    /**
     * Destrói uma instância de {@link EntityManagerFactory} criada anteriormente.
     * 
     * @param instancia
     *            a instância a ser destruída
     */
    default void destruir(EntityManagerFactory instancia) {
        if (instancia != null && instancia.isOpen()) {
            instancia.close();
        }
    }

    /**
     * Obtém a instância de {@link UnidadePersistencia} contendo as informações a serem utilizadas na geração de {@link EntityManagerFactory}.
     * 
     * @return as informações da unidade de persistência
     */
    UnidadePersistencia getUnidadePersistencia();
}
