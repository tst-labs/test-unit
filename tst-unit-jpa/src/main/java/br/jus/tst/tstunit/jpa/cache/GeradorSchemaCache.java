package br.jus.tst.tstunit.jpa.cache;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;

import org.slf4j.*;

import br.jus.tst.tstunit.jpa.*;
import br.jus.tst.tstunit.jpa.HabilitarJpa.UnidadePersistencia;
import br.jus.tst.tstunit.jpa.cdi.*;

/**
 * Implementação de {@link GeradorSchema} que delega a geração para o {@link EntityManagerFactory} definido na anotação {@literal @}{@link HabilitarJpa}.
 * Assume-se que o JPA esteja configurado para gerar o <em>schema</em> automaticamente.
 * 
 * @author Thiago Miranda
 * @since 6 de out de 2016
 */
public class GeradorSchemaCache implements Serializable, GeradorSchema {

    private static final long serialVersionUID = -3402053125627379702L;

    private static final Logger LOGGER = LoggerFactory.getLogger(GeradorSchemaCdi.class);
    private static final Map<UnidadePersistencia, EntityManagerCacheProducer> PRODUCER_MAP = new HashMap<>();

    @Override
    public void criar() throws JpaException {
        UnidadePersistencia[] unidadesPersistencia = EntityManagerFactoryProducerExtension.getUnidadesPersistencia();
        LOGGER.debug("Criando schema das unidades de persistência: {}", (Object[]) unidadesPersistencia);

        Arrays.stream(unidadesPersistencia).forEach(unidade -> {
            EntityManagerFactoryCacheProducer entityManagerFactoryProducer = null;
            EntityManagerCacheProducer entityManagerProducer = null;

            try {
                LOGGER.debug("Obtendo EntityManager da unidade de persistência: {}", unidade);
                entityManagerFactoryProducer = new EntityManagerFactoryCacheProducer(unidade);
                entityManagerProducer = new EntityManagerCacheProducer(entityManagerFactoryProducer);
                entityManagerProducer.criar().clear();
                PRODUCER_MAP.put(unidade, entityManagerProducer);
            } catch (PersistenceException exception) {
                throw new JpaException("Erro ao obter instância de EntityManager do contexto CDI", exception);
            }
        });

    }

    @Override
    public void destruir() throws JpaException {
        PRODUCER_MAP.forEach((unidadePersistencia, producer) -> {
            LOGGER.debug("Destruindo schema da unidade de persistência: {}", unidadePersistencia);
            producer.fecharTudo();
        });
    }
}
