package br.jus.tst.tstunit.jpa.cache;

import java.lang.annotation.Annotation;
import java.util.*;

import javax.persistence.*;

import org.apache.commons.lang3.Validate;

import br.jus.tst.tstunit.jpa.*;

/**
 * Implementação de {@link EntityManagerProducer} que guarda as instâncias em cache.
 * 
 * @author Thiago Miranda
 * @since 6 de out de 2016
 */
public class EntityManagerCacheProducer implements EntityManagerProducer {

    private static final Map<Class<? extends Annotation>, EntityManager> CACHE = new HashMap<>();

    private final EntityManagerFactoryCacheProducer entityManagerFactoryProducer;
    private final Class<? extends Annotation> qualifierClass;

    /**
     * Cria uma nova instância do produtor.
     * 
     * @param entityManagerFactoryProducer
     *            utilizado para obter instâncias de {@link EntityManagerFactory}
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    public EntityManagerCacheProducer(EntityManagerFactoryCacheProducer entityManagerFactoryProducer) {
        this.entityManagerFactoryProducer = Objects.requireNonNull(entityManagerFactoryProducer, "entityManagerFactoryProducer");
        qualifierClass = entityManagerFactoryProducer.getUnidadePersistencia().qualifierClass();
    }

    /**
     * Obtém a instância de {@link EntityManager} com o qualificador informado.
     * 
     * @param qualifierClass
     *            o qualificador da unidade de persistência
     * @return a instância associada (pode ser {@code null}
     */
    public static EntityManager getEntityManager(Class<? extends Annotation> qualifierClass) {
        return CACHE.get(qualifierClass);
    }

    /**
     * Obtém a única instância de {@link EntityManager} existente no contexto atual.
     * 
     * @return a instância associada ao contexto atual (pode não existir)
     * @throws IllegalStateException
     *             caso exista mais de uma instância associada ao contexto atual
     * @see #getEntityManager(Class)
     */
    public static Optional<EntityManager> getUniqueEntityManager() {
        Validate.validState(CACHE.size() == 1, "Há mais de 1 unidade de persistência definida");
        return Optional.ofNullable(CACHE.get(Unico.class));
    }

    @Override
    public EntityManager criar() throws JpaException {
        EntityManager entityManagerFactory;

        if (CACHE.containsKey(qualifierClass)) {
            entityManagerFactory = CACHE.get(qualifierClass);
        } else {
            entityManagerFactory = EntityManagerProducer.super.criar();
            CACHE.put(qualifierClass, entityManagerFactory);
        }

        return entityManagerFactory;
    }

    @Override
    public void destruir(EntityManager instancia) {
        CACHE.remove(qualifierClass);
        EntityManagerProducer.super.destruir(instancia);
    }

    /**
     * Fecha as instâncias de {@link EntityManager} e {@link EntityManagerFactory} associadas à unidade de persistência atual.
     */
    public void fecharTudo() {
        destruir(criar());
        entityManagerFactoryProducer.fechar();
    }

    @Override
    public EntityManagerFactoryProducer geEntityManagerFactoryProducer() {
        return entityManagerFactoryProducer;
    }
}
