package br.jus.tst.tstunit.jpa.cdi;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.*;
import javax.persistence.*;

import org.slf4j.*;

/**
 * Provê acesso a instâncias de {@link EntityManager}.
 * 
 * @author ThiagoColbert
 * @since 29 de mai de 2016
 */
public class TestEntityManagerProducer implements Bean<EntityManager>, Serializable {

    private static final long serialVersionUID = 3075682395966745202L;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestEntityManagerProducer.class);

    private TestEntityManagerFactoryProducer entityManagerFactoryProducer;

    /**
     * Cria uma nova instância com o produtor de {@link EntityManagerFactory} informado.
     * 
     * @param entityManagerFactoryProducer
     *            utilizado para obter instâncias de {@link EntityManagerFactory}
     */
    public TestEntityManagerProducer(TestEntityManagerFactoryProducer entityManagerFactoryProducer) {
        this.entityManagerFactoryProducer = Objects.requireNonNull(entityManagerFactoryProducer, "entityManagerFactoryProducer");
    }

    @Override
    public EntityManager create(CreationalContext<EntityManager> creationalContext) {
        LOGGER.info("Criando novo EntityManager");
        return entityManagerFactoryProducer.create(null).createEntityManager();
    }

    @Override
    public void destroy(EntityManager instance, CreationalContext<EntityManager> creationalContext) {
        if (instance.isOpen()) {
            LOGGER.info("Fechando EntityManager: {}", instance);
            instance.close();
        }
    }

    @Override
    public Set<Type> getTypes() {
        return new HashSet<>(Arrays.asList(EntityManager.class, Object.class));
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return entityManagerFactoryProducer.getQualifiers();
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
        return EntityManager.class;
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
