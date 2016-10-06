package br.jus.tst.tstunit.jpa.cache;

import java.util.*;

import javax.persistence.EntityManagerFactory;

import br.jus.tst.tstunit.jpa.*;
import br.jus.tst.tstunit.jpa.HabilitarJpa.UnidadePersistencia;

/**
 * Implementação de {@link EntityManagerFactoryProducer} que guarda as instâncias em cache.
 * 
 * @author Thiago Miranda
 * @since 6 de out de 2016
 */
public class EntityManagerFactoryCacheProducer implements EntityManagerFactoryProducer {

    private static final Map<UnidadePersistencia, EntityManagerFactory> CACHE = new HashMap<>();

    private final UnidadePersistencia unidadePersistencia;

    /**
     * Cria uma nova instância de produtor da unidade de persistência informada.
     * 
     * @param unidadePersistencia
     *            informações da unidade de persistência
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    public EntityManagerFactoryCacheProducer(UnidadePersistencia unidadePersistencia) {
        this.unidadePersistencia = Objects.requireNonNull(unidadePersistencia, "unidadePersistencia");
    }

    @Override
    public EntityManagerFactory criar() throws JpaException {
        if (CACHE.containsKey(unidadePersistencia)) {
            return CACHE.get(unidadePersistencia);
        } else {
            EntityManagerFactory entityManagerFactory = EntityManagerFactoryProducer.super.criar();
            CACHE.put(unidadePersistencia, entityManagerFactory);
            return entityManagerFactory;
        }
    }

    @Override
    public void destruir(EntityManagerFactory instancia) {
        CACHE.remove(unidadePersistencia);
        EntityManagerFactoryProducer.super.destruir(instancia);
    }
    
    /**
     * Fecha a única instância de {@link EntityManagerFactory} associada à unidade de persistência definida.
     */
    public void fechar() {
        destruir(criar());
    }

    @Override
    public UnidadePersistencia getUnidadePersistencia() {
        return unidadePersistencia;
    }
}
