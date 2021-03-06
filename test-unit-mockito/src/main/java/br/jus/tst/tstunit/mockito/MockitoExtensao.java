package br.jus.tst.tstunit.mockito;

import org.junit.runner.notification.RunNotifier;
import org.mockito.MockitoAnnotations;
import org.slf4j.*;

import br.jus.tst.tstunit.*;
import br.jus.tst.tstunit.time.MedidorTempoExecucao;

/**
 * {@link Extensao} que habilita o uso do Mockito nos testes.
 * 
 * @author Thiago Miranda
 * @since 5 de jul de 2016
 */
public class MockitoExtensao extends AbstractExtensao<HabilitarMockito> {

    private static final long serialVersionUID = 8537368004439287501L;
    private static final Logger LOGGER = LoggerFactory.getLogger(MockitoExtensao.class);

    /**
     * Cria uma nova instância da extensão para a classe de testes informada.
     * 
     * @param classeTeste
     *            a classe de testes
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    public MockitoExtensao(Class<?> classeTeste) {
        super(classeTeste);
    }

    @Override
    public void beforeTestes(Object instancia) {
        LOGGER.info("Inicializando os mocks");
        MedidorTempoExecucao.getInstancia().medir(() -> MockitoAnnotations.initMocks(instancia), "Inicialização de Mocks");
    }

    @Override
    public void inicializar(Configuracao configuracao, RunNotifier notifier) {
        LOGGER.info("Mockito habilitado");
    }
}
