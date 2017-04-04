package br.jus.tst.tstunit.jaxrs.resteasy;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.core.*;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.server.resourcefactory.SingletonResource;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jglue.cdiunit.ContextController;
import org.slf4j.Logger;

import br.jus.tst.tstunit.jaxrs.*;

/**
 * Implementação de {@link JaxRsEngine} que utiliza classes do RestEasy.
 * 
 * @author Thiago Miranda
 * @since 29 de mar de 2017
 */
@Resteasy
@ApplicationScoped
public class ResteasyEngine implements JaxRsEngine {

    protected Dispatcher dispatcher;

    @Inject
    private transient Logger logger;
    @Inject
    private transient ContextController contextController;

    @Override
    public MockRequest get(String uriTemplate) {
        logger.debug("GET {}", uriTemplate);
        return new ResteasyRequest(HttpMethod.GET, dispatcher, uriTemplate, contextController);
    }

    @Override
    public MockRequest post(String uriTemplate) {
        logger.debug("POST {}", uriTemplate);
        return new ResteasyRequest(HttpMethod.POST, dispatcher, uriTemplate, contextController);
    }

    @Override
    public MockRequest put(String uriTemplate) {
        logger.debug("PUT {}", uriTemplate);
        return new ResteasyRequest(HttpMethod.PUT, dispatcher, uriTemplate, contextController);
    }

    @Override
    public MockRequest delete(String uriTemplate) {
        logger.debug("DELETE {}", uriTemplate);
        return new ResteasyRequest(HttpMethod.DELETE, dispatcher, uriTemplate, contextController);
    }

    @Override
    public JaxRsEngine registrarRecurso(Object instancia) {
        logger.debug("Registrando instância de recurso do tipo: {}", instancia.getClass());
        dispatcher.getRegistry().addResourceFactory(new SingletonResource(instancia));
        return this;
    }

    @Override
    public JaxRsEngine registrarProvider(Class<?> providerClass) {
        logger.debug("Registrando provider: {}", providerClass);
        dispatcher.getProviderFactory().registerProvider(providerClass);
        return this;
    }

    @Override
    public <T> JaxRsEngine definirObjetoContexto(Class<T> classe, T instancia) {
        ResteasyProviderFactory.pushContext(classe, instancia);
        return this;
    }

    /**
     * Fornece acesso ao {@link Dispatcher} sendo utilizado.
     * 
     * @return a instância de {@code Dispatcher}
     */
    @Override
    public Object getImplementacaoSubjacente() {
        return dispatcher;
    }

    @PostConstruct
    protected void inicializarDispatcher() {
        ResteasyProviderFactory resteasyProviderFactory = new ResteasyProviderFactory();

        CdiInjectorFactory injectorFactory;
        try {
            injectorFactory = CdiInjectorFactory.class.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException exception) {
            logger.debug("Erro ao instanciar CdiInjectorFactory. Irá tentar de outro modo.", exception);

            try {
                injectorFactory = CdiInjectorFactory.class.getConstructor(ResteasyProviderFactory.class).newInstance(resteasyProviderFactory);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                    | SecurityException exception1) {
                throw new JaxRsException("Erro ao inicializar Dispatcher do RestEasy", exception1);
            }
        }

        resteasyProviderFactory.setInjectorFactory(injectorFactory);
        this.dispatcher = new SynchronousDispatcher(resteasyProviderFactory);

        ResteasyProviderFactory.setInstance(dispatcher.getProviderFactory());
        RegisterBuiltin.register(dispatcher.getProviderFactory());
    }
}
