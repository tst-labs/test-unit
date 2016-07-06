package br.jus.tst.tstunit.mockito;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.*;
import org.mockito.MockitoAnnotations;
import org.slf4j.*;

import br.jus.tst.tstunit.*;

/**
 * 
 * @author Thiago Miranda
 * @since 5 de jul de 2016
 */
public class MockitoExtensao implements Extensao {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockitoExtensao.class);

    @Override
    public void beforeTestes(Object instancia) {
        MockitoAnnotations.initMocks(instancia);
    }

    @Override
    public boolean isHabilitada(Class<?> classeTeste) {
        return classeTeste.getAnnotation(HabilitarMockito.class) != null;
    }

    @Override
    public void inicializar(Class<?> classeTeste, Configuracao configuracao, RunNotifier notifier) {
        LOGGER.info("Mockito habilitado");
    }

    @Override
    public Statement criarStatement(Class<?> classeTeste, Statement defaultStatement, FrameworkMethod method) {
        return defaultStatement;
    }
}
