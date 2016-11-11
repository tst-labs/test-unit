package br.jus.tst.tstunit.jpa.cdi;

import java.util.*;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.*;

import br.jus.tst.tstunit.jpa.HabilitarJpa.UnidadePersistencia;

/**
 * {@link Extension} que carrega as instâncias de {@link EntityManagerFactoryCdiProducer}.
 * 
 * @author Thiago Miranda
 * @since 4 de out de 2016
 * 
 * @see <a href="http://jdevelopment.nl/dynamic-cdi-producers/">Dynamic CDI producers</a>
 */
public class EntityManagerFactoryProducerExtension implements Extension {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityManagerFactoryProducerExtension.class);

    private static UnidadePersistencia[] unidadesPersistencia;
    private static Map<String, String> propriedadesAdicionais;

    public static UnidadePersistencia[] getUnidadesPersistencia() {
        return ArrayUtils.clone(unidadesPersistencia);
    }

    public static void setUnidadesPersistencia(UnidadePersistencia[] unidadesPersistencia) {
        LOGGER.debug("Unidades de persistência: {}", unidadesPersistencia);
        EntityManagerFactoryProducerExtension.unidadesPersistencia = ArrayUtils.clone(unidadesPersistencia);
    }

    public static Map<String, String> getPropriedadesAdicionais() {
        return Collections.unmodifiableMap(propriedadesAdicionais);
    }

    public static void setPropriedadesAdicionais(Map<String, String> propriedadesAdicionais) {
        LOGGER.debug("Propriedades ORM adicionais: {}", propriedadesAdicionais);
        EntityManagerFactoryProducerExtension.propriedadesAdicionais = new HashMap<>(propriedadesAdicionais);
    }

    /**
     * Método executado após a etapa de <em>bean discovery</em> do CDI ser finalizada.
     * 
     * @param afterBeanDiscovery
     *            o evento de <em>bean discovery</em>
     */
    public void afterBean(final @Observes AfterBeanDiscovery afterBeanDiscovery) {
        LOGGER.debug("Processando unidades de persistência: {}", (Object[]) unidadesPersistencia);

        for (UnidadePersistencia unidade : unidadesPersistencia) {
            EntityManagerFactoryCdiProducer entityManagerFactoryCdiProducer = new EntityManagerFactoryCdiProducer(unidade, propriedadesAdicionais);
            afterBeanDiscovery.addBean(entityManagerFactoryCdiProducer);
            afterBeanDiscovery.addBean(new EntityManagerCdiProducer(entityManagerFactoryCdiProducer));
        }
    }
}
