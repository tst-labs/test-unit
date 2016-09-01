package br.jus.tst.tstunit.annotation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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

    }

    public static class ClasseSemAnotacao {

    }

    private AnnotationExtractor annotationExtractor;

    @Mock
    private FrameworkMethod frameworkMethod;

    @Test
    public void deveriaCarregarAnotacoesDoMetodo() {
        annotationExtractor = new AnnotationExtractor(ClasseSemAnotacao.class);
        ImprimirNomeTeste imprimirNomeTeste = mock(ImprimirNomeTeste.class);

        when(frameworkMethod.getAnnotation(ImprimirNomeTeste.class)).thenReturn(imprimirNomeTeste);

        List<ImprimirNomeTeste> annotations = annotationExtractor.getAnnotationsFromMethodOrClass(frameworkMethod, ImprimirNomeTeste.class);

        assertThat(annotations.size(), is(equalTo(1)));
        verify(frameworkMethod).getAnnotation(ImprimirNomeTeste.class);
    }

    @Test
    public void deveriaCarregarAnotacoesDaClasse() {
        annotationExtractor = new AnnotationExtractor(ClasseComAnotacao.class);

        when(frameworkMethod.getAnnotation(ImprimirNomeTeste.class)).thenReturn(null);

        List<ImprimirNomeTeste> annotations = annotationExtractor.getAnnotationsFromMethodOrClass(frameworkMethod, ImprimirNomeTeste.class);

        assertThat(annotations.size(), is(equalTo(1)));
        verify(frameworkMethod).getAnnotation(ImprimirNomeTeste.class);
    }

    @Test
    public void deveriaCarregarAnotacoesDaClasseEMetodoEmOrdem() {
        annotationExtractor = new AnnotationExtractor(ClasseComAnotacao.class);
        ImprimirNomeTeste imprimirNomeTeste = mock(ImprimirNomeTeste.class);

        when(frameworkMethod.getAnnotation(ImprimirNomeTeste.class)).thenReturn(imprimirNomeTeste);

        List<ImprimirNomeTeste> annotations = annotationExtractor.getAnnotationsFromMethodOrClass(frameworkMethod, ImprimirNomeTeste.class);

        assertThat(annotations.size(), is(equalTo(2)));
        verify(frameworkMethod).getAnnotation(ImprimirNomeTeste.class);
    }
}
