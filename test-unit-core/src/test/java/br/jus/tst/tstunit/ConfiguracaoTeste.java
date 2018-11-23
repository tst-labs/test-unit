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

    @Test(expected = TstUnitException.class)
    public void deveriaLancarExcecaoCasoArquivoNaoExista() throws TstUnitException {
        configuracao.setNomeArquivoPropriedades("teste.properties");
        configuracao.carregar();
    }
}
