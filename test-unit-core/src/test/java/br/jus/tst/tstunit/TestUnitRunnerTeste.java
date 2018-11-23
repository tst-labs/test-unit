package br.jus.tst.tstunit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.*;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

/**
 * Testes unitários da {@link TestUnitRunner}.
 * 
 * @author Thiago Miranda
 * @since 8 de jul de 2016
 */
public class TestUnitRunnerTeste {

    public static class Teste {

        @Test
        public void teste() {
            assertTrue(true);
        }
    }

    @Test
    public void testRunNotifier() throws InitializationError, TestUnitException {
        Configuracao configuracao = Configuracao.getInstance();
        ExtensoesLoader extensoesLoader = mock(ExtensoesLoader.class);
        RunNotifier notifier = mock(RunNotifier.class);
        Extensao<?> extensao = mock(Extensao.class);

        when(extensoesLoader.getClasseTeste()).thenReturn(Teste.class);
        when(extensoesLoader.carregarExtensoes()).thenReturn(Arrays.asList(extensao));

        TestUnitRunner runner = new TestUnitRunner(configuracao, extensoesLoader);
        runner.run(notifier);

        verify(extensao).inicializar(configuracao, notifier);
    }
}
