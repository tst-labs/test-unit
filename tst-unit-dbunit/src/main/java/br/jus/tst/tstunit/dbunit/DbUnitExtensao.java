package br.jus.tst.tstunit.dbunit;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.*;
import org.slf4j.*;

import br.jus.tst.tstunit.*;

/**
 * {@link Extensao} que habilita o uso do DBUnit nos testes.
 * 
 * @author Thiago Miranda
 * @since 5 de jul de 2016
 */
public class DbUnitExtensao extends AbstractExtensao<HabilitarDbUnit> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbUnitExtensao.class);

    private DbUnitRunner dbUnitRunner;

    public DbUnitExtensao(Class<?> classeTeste) {
        super(classeTeste);
    }

    @Override
    public void inicializar(Configuracao configuracao, RunNotifier notifier) {
        LOGGER.info("DBUnit habilitado");
        HabilitarDbUnit habilitarDbUnit = classeTeste.getAnnotation(HabilitarDbUnit.class);
        dbUnitRunner = new DbUnitRunner(classeTeste, habilitarDbUnit.nomeSchema(), configuracao);
    }

    @Override
    public Statement criarStatement(Statement defaultStatement, FrameworkMethod method) {
        LOGGER.info("Ativando DBUnit");
        return dbUnitRunner.criarStatement(defaultStatement, method);
    }
}
