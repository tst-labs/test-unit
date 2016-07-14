package br.jus.tst.tstunit.jpa;

import java.io.Serializable;
import java.util.Optional;

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
     * Cria o <em>schema</em> de banco de dados.
     */
    @Override
    public void criar() {
        Optional<EntityManager> entityManagerOptional = Optional.empty();
        try {
            Instance<EntityManager> entityManagerInstance = CDI.current().select(EntityManager.class);
            entityManagerOptional = entityManagerInstance.isUnsatisfied() ? Optional.empty() : Optional.of(entityManagerInstance.get());
        } finally {
            entityManagerOptional.orElseThrow(() -> new RuntimeException("Nenhum produtor de EntityManager encontrado no classpath")).close();
        }
    }
}
