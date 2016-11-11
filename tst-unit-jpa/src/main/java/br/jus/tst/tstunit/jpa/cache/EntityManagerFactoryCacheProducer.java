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
    private final Map<String, String> propriedadesAdicionais;

    /**
     * Cria uma nova instância de produtor da unidade de persistência informada.
     * 
     * @param unidadePersistencia
     *            informações da unidade de persistência
     * @param propriedadesAdicionais
     *            a serem repassadas ao fraemwork ORM (pode estar vazio)
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    public EntityManagerFactoryCacheProducer(UnidadePersistencia unidadePersistencia, Map<String, String> propriedadesAdicionais) {
        this.unidadePersistencia = Objects.requireNonNull(unidadePersistencia, "unidadePersistencia");
        this.propriedadesAdicionais = new HashMap<>(Objects.requireNonNull(propriedadesAdicionais, "propriedadesAdicionais"));
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

    @Override
    public Map<String, String> getPropriedadesAdicionais() {
        return Collections.unmodifiableMap(propriedadesAdicionais);
    }
}
