package br.jus.tst.tstunit.parameters;

import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.parameterized.*;

/**
 * Implementação de {@link ParametersRunnerFactory} que fornece instâncias de {@link TestUnitParameterizedRunner}.
 * 
 * @author Thiago Miranda
 * @since 29 de ago de 2016
 */
public class TestUnitParameterizedRunnerFactory implements ParametersRunnerFactory {

    @Override
    public Runner createRunnerForTestWithParameters(TestWithParameters test) throws InitializationError {
        return new TestUnitParameterizedRunner(test);
    }
}
