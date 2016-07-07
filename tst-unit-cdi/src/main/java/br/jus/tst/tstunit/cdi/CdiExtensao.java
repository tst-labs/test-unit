package br.jus.tst.tstunit.cdi;

import java.io.IOException;
import java.util.Optional;

import javax.naming.InitialContext;

import org.jboss.weld.bootstrap.api.*;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.environment.se.*;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.jglue.cdiunit.internal.*;
import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.*;
import org.slf4j.*;

import br.jus.tst.tstunit.*;

/**
 * 
 * @author Thiago Miranda
 * @since 5 de jul de 2016
 */
public class CdiExtensao extends AbstractExtensao<HabilitarCdiAndMockito> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdiExtensao.class);

    private Weld weld;
    private WeldContainer container;
    private Throwable startupException;

    public CdiExtensao(Class<?> classeTeste) {
        super(classeTeste);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getInstanciaPersonalizadaParaTestes() {
        try {
            weld = new Weld() {

                // CDI 1.1
                protected Deployment createDeployment(ResourceLoader resourceLoader, CDI11Bootstrap bootstrap) {
                    try {
                        return new Weld11TestUrlDeployment(resourceLoader, bootstrap, classeTeste);
                    } catch (IOException e) {
                        startupException = e;
                        throw new RuntimeException(e);
                    }
                }

                // CDI 1.0
                @SuppressWarnings("unused")
                protected Deployment createDeployment(ResourceLoader resourceLoader, Bootstrap bootstrap) {
                    try {
                        return new WeldTestUrlDeployment(resourceLoader, bootstrap, classeTeste);
                    } catch (IOException e) {
                        startupException = e;
                        throw new RuntimeException(e);
                    }
                };
            };

            try {
                container = weld.initialize();
            } catch (Throwable e) {
                if (startupException == null) {
                    startupException = e;
                }
                if (e instanceof ClassFormatError) {
                    throw e;
                }
            }
        } catch (Throwable e) {
            startupException = new Exception("Unable to start weld", e);
        }

        return (Optional<T>) createTest(classeTeste);
    }

    private Optional<?> createTest(Class<?> testClass) {
        return Optional.of(container.instance().select(testClass).get());
    }

    @Override
    public void inicializar(Configuracao configuracao, RunNotifier notifier) {
        LOGGER.info("CDI habilitado");
    }

    @Override
    public Statement criarStatement(Statement defaultStatement, FrameworkMethod method) throws TstUnitException {
        LOGGER.info("Ativando contexto CDI");

        return new Statement() {

            @Override
            public void evaluate() throws Throwable {

                if (startupException != null) {
                    if (method.getAnnotation(Test.class).expected() == startupException.getClass()) {
                        return;
                    }
                    throw startupException;
                }

                System.setProperty("java.naming.factory.initial", carregarNomeClasseCdiUnitContextFactory());
                InitialContext initialContext = new InitialContext();
                initialContext.bind("java:comp/BeanManager", container.getBeanManager());

                try {
                    defaultStatement.evaluate();

                } finally {
                    initialContext.close();
                    weld.shutdown();

                }

            }
        };
    }

    private String carregarNomeClasseCdiUnitContextFactory() {
        String cdiUnitContextFactory = "org.jglue.cdiunit.internal.naming.CdiUnitContextFactory";

        try {
            Class.forName(cdiUnitContextFactory);
        } catch (ClassNotFoundException exception) {
            cdiUnitContextFactory = "org.jglue.cdiunit.internal.CdiUnitContextFactory";
        }

        return cdiUnitContextFactory;
    }
}
