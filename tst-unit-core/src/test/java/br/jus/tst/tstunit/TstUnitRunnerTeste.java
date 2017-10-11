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
 * Testes unitários da {@link TstUnitRunner}.
 * 
 * @author Thiago Miranda
 * @since 8 de jul de 2016
 */
public class TstUnitRunnerTeste {

    public static class Teste {

        @Test
        public void teste() {
            assertTrue(true);
        }
    }

    @Test
    public void testRunNotifier() throws InitializationError, TstUnitException {
        Configuracao configuracao = mock(Configuracao.class);
        ExtensoesLoader extensoesLoader = mock(ExtensoesLoader.class);
        RunNotifier notifier = mock(RunNotifier.class);
        Extensao<?> extensao = mock(Extensao.class);

        when(extensoesLoader.getClasseTeste()).thenReturn(Teste.class);
        when(extensoesLoader.carregarExtensoes()).thenReturn(Arrays.asList(extensao));

        TstUnitRunner runner = new TstUnitRunner(configuracao, extensoesLoader);
        runner.run(notifier);

        verify(extensao).inicializar(configuracao, notifier);
    }
}
