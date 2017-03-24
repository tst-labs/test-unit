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
@RunWith(TstUnitRunner.class)
@HabilitarDbUnit
public abstract class AbstractIT {

    protected JdbcConnectionSupplier criarConnectionSupplier(String nomeArquivoPropriedades) throws TstUnitException {
        return new JdbcConnectionSupplier(new Configuracao().setNomeArquivoPropriedades(nomeArquivoPropriedades).carregar().getSubPropriedades("jdbc"));
    }
    
    protected JdbcConnectionSupplier criarConnectionSupplier() throws TstUnitException {
        return new JdbcConnectionSupplier(new Configuracao().carregar().getSubPropriedades("jdbc"));
    }
}
