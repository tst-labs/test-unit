package br.jus.tst.tstunit.jpa.cdi;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.*;
import javax.persistence.*;

import org.hibernate.annotations.common.annotationfactory.*;
import org.slf4j.*;

import br.jus.tst.tstunit.jpa.HabilitarJpa.UnidadePersistencia;

/**
 * Classe que provê acesso a instâncias de {@link EntityManagerFactory} utilizadas nos testes.
 * 
 * @author ThiagoColbert
 * @since 29 de mai de 2016
 */
public class TestEntityManagerFactoryProducer implements Bean<EntityManagerFactory>, Serializable {

    private static final long serialVersionUID = 902185891912929393L;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestEntityManagerFactoryProducer.class);

    private final UnidadePersistencia unidadePersistencia;

    /**
     * Cria uma nova instância de produtor da unidade de persistência informada.
     * 
     * @param unidadePersistencia
     *            informações da unidade de persistência
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    public TestEntityManagerFactoryProducer(UnidadePersistencia unidadePersistencia) {
        this.unidadePersistencia = Objects.requireNonNull(unidadePersistencia, "unidadePersistencia");
    }

    @Override
    public EntityManagerFactory create(CreationalContext<EntityManagerFactory> creationalContext) {
        LOGGER.info("Inicializando contexto de persistência: {}", unidadePersistencia);
        return Persistence.createEntityManagerFactory(unidadePersistencia.nome());
    }

    @Override
    public void destroy(EntityManagerFactory instance, CreationalContext<EntityManagerFactory> creationalContext) {
        if (instance.isOpen()) {
            LOGGER.info("Encerrando contexto de persistência: {}", instance);
            instance.close();
        }
    }

    @Override
    public Set<Type> getTypes() {
        return new HashSet<>(Arrays.asList(EntityManagerFactory.class, Object.class));
    }

    @Override
    public Set<Annotation> getQualifiers() {
        Annotation annotation = new AnnotationProxy(new AnnotationDescriptor(unidadePersistencia.qualifierClass()));
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
}
