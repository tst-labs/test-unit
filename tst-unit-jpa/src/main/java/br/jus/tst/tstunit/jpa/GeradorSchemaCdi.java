package br.jus.tst.tstunit.jpa;

import java.io.Serializable;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.persistence.*;

/**
 * Implementação de {@link GeradorSchema} que delega a geração para o {@link EntityManagerFactory} obtido através do contêiner CDI. Assume-se que o JPA esteja
 * configurado para gerar o <em>schema</em> automaticamente.
 * 
 * @author Thiago Miranda
 * @since 6 de jul de 2016
 */
public class GeradorSchemaCdi implements Serializable, GeradorSchema {

    private static final long serialVersionUID = -7371478997025466447L;

    /**
     * @throws JpaException
     *             caso ocorra algum erro ao executar a operação
     */
    @Override
    public void criar() {
        try {
            CDI.current().select(EntityManager.class).get();
        } catch (PersistenceException exception) {
            throw new JpaException("Erro ao obter instância de EntityManager do contexto CDI", exception);
        }
    }
}
