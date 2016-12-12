package br.jus.tst.tstunit.jpa.cdi;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.*;
import javax.persistence.EntityManagerFactory;

import org.slf4j.*;

import br.jus.tst.tstunit.jpa.*;
import br.jus.tst.tstunit.jpa.HabilitarJpa.UnidadePersistencia;
import br.jus.tst.tstunit.jpa.annotation.*;

/**
 * Implementação de {@link EntityManagerFactoryProducer} que obtém as instâncias através do CDI.
 * 
 * @author ThiagoColbert
 * @since 29 de mai de 2016
 */
public class EntityManagerFactoryCdiProducer implements Bean<EntityManagerFactory>, EntityManagerFactoryProducer, PassivationCapable, Serializable {

    private static final long serialVersionUID = 902185891912929393L;

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityManagerFactoryCdiProducer.class);

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
    public EntityManagerFactoryCdiProducer(UnidadePersistencia unidadePersistencia, Map<String, String> propriedadesAdicionais) {
        this.unidadePersistencia = Objects.requireNonNull(unidadePersistencia, "unidadePersistencia");
        this.propriedadesAdicionais = new HashMap<>(Objects.requireNonNull(propriedadesAdicionais, "propriedadesAdicionais"));
    }

    @Override
    public EntityManagerFactory create(CreationalContext<EntityManagerFactory> creationalContext) {
        LOGGER.info("Inicializando contexto de persistência: {}", unidadePersistencia);
        return criar();
    }

    @Override
    public void destroy(EntityManagerFactory instance, CreationalContext<EntityManagerFactory> creationalContext) {
        LOGGER.info("Encerrando contexto de persistência: {}", instance);
        destruir(instance);
    }

    @Override
    public Set<Type> getTypes() {
        return new HashSet<>(Arrays.asList(EntityManagerFactory.class, Object.class));
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Set<Annotation> getQualifiers() {
        Class<? extends Annotation> qualifierClass = unidadePersistencia.qualifierClass();
        if (qualifierClass == Unico.class) {
            // substitui o qualifier para funcionar dentro do CDI
            qualifierClass = Default.class;
        }

        Annotation annotation = new AnnotationProxy(new AnnotationDescriptor(qualifierClass));
        LOGGER.debug("Qualifier da unidade de persistência: {}", annotation);

        return new HashSet<>(Arrays.asList(annotation));
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ApplicationScoped.class;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public Class<?> getBeanClass() {
        return EntityManagerFactory.class;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public UnidadePersistencia getUnidadePersistencia() {
        return unidadePersistencia;
    }

    @Override
    public Map<String, String> getPropriedadesAdicionais() {
        return Collections.unmodifiableMap(propriedadesAdicionais);
    }

    @Override
    public String getId() {
        return this.toString();
    }
}
