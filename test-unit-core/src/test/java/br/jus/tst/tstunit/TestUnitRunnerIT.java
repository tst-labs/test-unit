package br.jus.tst.tstunit;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Testes de integração da {@link TestUnitRunner}.
 * 
 * @author Thiago Miranda
 * @since 14 de jul de 2016
 */
@RunWith(TestUnitRunner.class)
public class TestUnitRunnerIT {

    @Test
    public void test() {
        assertTrue("Runner não executado", true);
    }
}
