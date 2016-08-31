package br.jus.tst.tstunit.cdi;

import java.io.IOException;
import java.util.Optional;

import javax.naming.*;

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
 * {@link Extensao} que habilita o uso do CDI nos testes.
 * 
 * @author Thiago Miranda
 * @since 5 de jul de 2016
 */
public class CdiExtensao extends AbstractExtensao<HabilitarCdiAndMockito> {

    private static final long serialVersionUID = 2619723714870085508L;

    private static final Logger LOGGER = LoggerFactory.getLogger(CdiExtensao.class);

    private final class CdiUnitWeld extends Weld {

        // CDI 1.1
        protected Deployment createDeployment(ResourceLoader resourceLoader, CDI11Bootstrap bootstrap) {
            try {
                return new Weld11TestUrlDeployment(resourceLoader, bootstrap, classeTeste);
            } catch (IOException exception) {
                CdiExtensao.this.startupException = exception;
                throw new CdiException("Erro ao criar Deployment", exception);
            }
        }

        // CDI 1.0
        @SuppressWarnings("unused")
        protected Deployment createDeployment(ResourceLoader resourceLoader, Bootstrap bootstrap) {
            try {
                return new WeldTestUrlDeployment(resourceLoader, bootstrap, classeTeste);
            } catch (IOException exception) {
                CdiExtensao.this.startupException = exception;
                throw new CdiException("Erro ao criar Deployment", exception);
            }
        }
    }

    private final class CdiStatement extends Statement {

        private static final String BEAN_MANAGER_JNDI_NAME = "java:comp/BeanManager";

        private transient final Statement defaultStatement;
        private transient final FrameworkMethod method;

        CdiStatement(Statement defaultStatement, FrameworkMethod method) {
            this.defaultStatement = defaultStatement;
            this.method = method;
        }

        @Override
        public void evaluate() throws Throwable {
            if (CdiExtensao.this.startupException != null) {
                if (method.getAnnotation(Test.class).expected() == startupException.getClass()) {
                    return;
                }

                throw CdiExtensao.this.startupException;
            }

            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, carregarNomeClasseCdiUnitContextFactory());
            InitialContext initialContext = new InitialContext();
            initialContext.bind(BEAN_MANAGER_JNDI_NAME, container.getBeanManager());

            try {
                defaultStatement.evaluate();
            } finally {
                initialContext.close();
                weld.shutdown();
            }
        }

        private String carregarNomeClasseCdiUnitContextFactory() {
            String cdiUnitContextFactory = "org.jglue.cdiunit.internal.naming.CdiUnitContextFactory";

            try {
                Class.forName(cdiUnitContextFactory);
            } catch (ClassNotFoundException exception) {
                LOGGER.trace(exception.getLocalizedMessage(), exception);
                cdiUnitContextFactory = "org.jglue.cdiunit.internal.CdiUnitContextFactory";
            }

            return cdiUnitContextFactory;
        }
    }

    private transient Weld weld;
    private transient WeldContainer container;
    private transient Throwable startupException;

    /**
     * Cria uma nova instância da extensão para a classe de testes informada.
     * 
     * @param classeTeste
     *            a classe de testes
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    public CdiExtensao(Class<?> classeTeste) {
        super(classeTeste);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getInstanciaPersonalizadaParaTestes() {
        try {
            weld = new CdiUnitWeld();

            try {
                container = weld.initialize();
            } catch (Throwable exception) {
                if (startupException == null) {
                    startupException = exception;
                }
                if (exception instanceof ClassFormatError) {
                    throw exception;
                }
            }
        } catch (Throwable exception) {
            startupException = new Exception("Erro ao inicializar Weld", exception);
        }

        return (Optional<T>) logExceptionIfExists().createTest(classeTeste);
    }

    private CdiExtensao logExceptionIfExists() {
        if (startupException != null) {
            LOGGER.warn("Erro ao inicializar Weld. Pode ser que essa exceção seja esperada em seu teste, portanto a execução não será interrompida.", startupException);
        }
        return this;
    }

    private Optional<?> createTest(Class<?> testClass) {
        return Optional.of(container.instance().select(testClass).get());
    }

    /**
     * @throws IllegalStateException
     *             caso a extensão não esteja habilitada
     */
    @Override
    public void inicializar(Configuracao configuracao, RunNotifier notifier) {
        assertExtensaoHabilitada();
        LOGGER.info("CDI habilitado");
    }

    /**
     * @throws IllegalStateException
     *             caso a extensão não esteja habilitada
     */
    @Override
    public Statement criarStatement(Statement defaultStatement, FrameworkMethod method) throws TstUnitException {
        assertExtensaoHabilitada();
        LOGGER.info("Ativando contexto CDI");
        return new CdiStatement(defaultStatement, method);
    }
}
