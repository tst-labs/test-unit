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
public class MockitoExtensao extends AbstractExtensao<HabilitarMockito> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockitoExtensao.class);

    public MockitoExtensao(Class<?> classeTeste) {
        super(classeTeste);
    }

    @Override
    public void beforeTestes(Object instancia) {
        MockitoAnnotations.initMocks(instancia);
    }

    @Override
    public void inicializar(Configuracao configuracao, RunNotifier notifier) {
        LOGGER.info("Mockito habilitado");
    }

    @Override
    public Statement criarStatement(Statement defaultStatement, FrameworkMethod method) {
        return defaultStatement;
    }
}
