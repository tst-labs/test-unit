package br.jus.tst.tstunit;

import org.junit.*;

/**
 * Testes unitários da {@link Configuracao}.
 * 
 * @author Thiago Miranda
 * @since 26 de jan de 2017
 */
public class ConfiguracaoTeste {

    private Configuracao configuracao;

    @Before
    public void setUp() {
        configuracao = Configuracao.getInstance();
    }

    @Test(expected = TestUnitException.class)
    public void deveriaLancarExcecaoCasoArquivoNaoExista() throws TestUnitException {
        configuracao.setNomeArquivoPropriedades("teste.properties");
        configuracao.carregar();
    }
}
