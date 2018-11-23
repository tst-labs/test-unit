package br.jus.tst.tstunit.parameters;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.model.*;
import org.junit.runners.parameterized.TestWithParameters;

import br.jus.tst.tstunit.*;

/**
 * Implementação adaptada de {@link TstUnitRunner} que permite a execução de testes parametrizados.
 * 
 * @author Thiago Miranda
 * @since 29 de ago de 2016
 */
public class TstUnitParameterizedRunner extends TstUnitRunner {

    private final List<Object> parametros;
    private final String nome;

    /**
     * Cria uma nova instância para o teste parametrizado informado.
     * 
     * @param test
     *            o teste parametrizado
     * @throws InitializationError
     *             caso ocorra algum erro ao inicializar o <em>runner</em>
     */
    public TstUnitParameterizedRunner(TestWithParameters test) throws InitializationError {
        super(test.getTestClass().getJavaClass());
        parametros = test.getParameters();
        nome = test.getName();
    }

    /*
     * Trecho de código adaptado de org.junit.runners.parameterized.BlockJUnit4ClassRunnerWithParameters
     */
    @Override
    protected Object createTest() throws Exception {
        List<FrameworkField> annotatedFieldsByParameter = getAnnotatedFieldsByParameter();
        if (annotatedFieldsByParameter.size() != parametros.size()) {
            throw new TstUnitException("Wrong number of parameters and @Parameter fields." + " @Parameter fields counted: " + annotatedFieldsByParameter.size()
                    + ", available parameters: " + parametros.size() + ".");
        }

        Object testInstance = super.createTest();

        for (FrameworkField each : annotatedFieldsByParameter) {
            Field field = each.getField();
            Parameter annotation = field.getAnnotation(Parameter.class);
            int index = annotation.value();

            Object parametro = parametros.get(index);
            try {
                field.set(testInstance, parametro);
            } catch (IllegalArgumentException exception) {
                throw new TstUnitException(getTestClass().getName() + ": Trying to set " + field.getName() + " with the value " + parametro + " that is not the right type ("
                        + parametro.getClass().getSimpleName() + " instead of " + field.getType().getSimpleName() + ").", exception);
            }
        }

        return testInstance;
    }

    private List<FrameworkField> getAnnotatedFieldsByParameter() {
        return getTestClass().getAnnotatedFields(Parameter.class);
    }

    public String getNome() {
        return nome;
    }
}
