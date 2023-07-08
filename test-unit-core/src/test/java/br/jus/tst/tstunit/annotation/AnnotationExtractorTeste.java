package br.jus.tst.tstunit.annotation;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import br.jus.tst.tstunit.ImprimirNomeTeste;

/**
 * Testes unitários da {@link AnnotationExtractor}.
 * 
 * @author Thiago Miranda
 * @since 31 de ago de 2016
 */
@RunWith(MockitoJUnitRunner.class)
public class AnnotationExtractorTeste {

    @ImprimirNomeTeste(value = true)
    public static class ClasseComAnotacao {

        @ImprimirNomeTeste
        public void metodoComAnotacao() {

        }

        public void metodoSemAnotacao() {

        }
    }

    public static class ClasseSemAnotacao {

        @ImprimirNomeTeste
        public void metodoComAnotacao() {

        }
    }

    private AnnotationExtractor annotationExtractor;

    @Mock
    private FrameworkMethod frameworkMethod;

    @Test
    public void deveriaCarregarAnotacoesDoMetodo() throws NoSuchMethodException, SecurityException {
        annotationExtractor = new AnnotationExtractor(ClasseSemAnotacao.class);

        when(frameworkMethod.getMethod()).thenReturn(ClasseComAnotacao.class.getMethod("metodoComAnotacao"));

        List<ImprimirNomeTeste> annotations = annotationExtractor.getAnnotationsFromMethodOrClass(frameworkMethod, ImprimirNomeTeste.class);

        assertThat(annotations.size(), is(equalTo(1)));
        verify(frameworkMethod).getMethod();
    }

    @Test
    public void deveriaCarregarAnotacoesDaClasse() throws NoSuchMethodException, SecurityException {
        annotationExtractor = new AnnotationExtractor(ClasseComAnotacao.class);

        when(frameworkMethod.getMethod()).thenReturn(ClasseComAnotacao.class.getMethod("metodoSemAnotacao"));

        List<ImprimirNomeTeste> annotations = annotationExtractor.getAnnotationsFromMethodOrClass(frameworkMethod, ImprimirNomeTeste.class);

        assertThat(annotations.size(), is(equalTo(1)));
        verify(frameworkMethod).getMethod();
    }

    @Test
    public void deveriaCarregarAnotacoesDaClasseEMetodoEmOrdem() throws NoSuchMethodException, SecurityException {
        annotationExtractor = new AnnotationExtractor(ClasseComAnotacao.class);

        when(frameworkMethod.getMethod()).thenReturn(ClasseComAnotacao.class.getMethod("metodoComAnotacao"));

        List<ImprimirNomeTeste> annotations = annotationExtractor.getAnnotationsFromMethodOrClass(frameworkMethod, ImprimirNomeTeste.class);

        assertThat(annotations.size(), is(equalTo(2)));
        verify(frameworkMethod).getMethod();
    }
}
