package br.jus.tst.tstunit.dbunit;

import org.junit.runner.RunWith;

import br.jus.tst.tstunit.*;
import br.jus.tst.tstunit.dbunit.jdbc.JdbcConnectionSupplier;

/**
 * Classe base para todos os testes de integração.
 * 
 * @author Thiago Miranda
 * @since 24 de mar de 2017
 */
@RunWith(TestUnitRunner.class)
@HabilitarDbUnit
public abstract class AbstractIT {

    protected JdbcConnectionSupplier criarConnectionSupplier(String nomeArquivoPropriedades) throws TestUnitException {
        return new JdbcConnectionSupplier(Configuracao.getInstance().setNomeArquivoPropriedades(nomeArquivoPropriedades).carregar().getSubPropriedades("jdbc"));
    }

    protected JdbcConnectionSupplier criarConnectionSupplier() throws TestUnitException {
        return new JdbcConnectionSupplier(Configuracao.getInstance().carregar().getSubPropriedades("jdbc"));
    }
}
