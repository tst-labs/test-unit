package br.jus.tst.tstunit.dbunit;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.*;
import org.slf4j.*;

import br.jus.tst.tstunit.*;

/**
 * 
 * @author Thiago Miranda
 * @since 5 de jul de 2016
 */
public class DbUnitExtensao implements Extensao {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbUnitExtensao.class);

    private DbUnitRunner dbUnitRunner;

    @Override
    public void beforeTestes(Object instancia) {
        HabilitarDbUnit habilitarDbUnit = instancia.getClass().getAnnotation(HabilitarDbUnit.class);
        Class<? extends GeradorSchema> geradorSchema = habilitarDbUnit.geradorSchema();
        try {
            geradorSchema.newInstance().create();
        } catch (InstantiationException | IllegalAccessException exception) {
            // TODO Esqueleto de bloco catch gerado automaticamente
            exception.printStackTrace();
        }
    }
    
    @Override
    public void afterTestes(Class<?> classeTeste) {
        HabilitarDbUnit habilitarDbUnit = classeTeste.getAnnotation(HabilitarDbUnit.class);
        Class<? extends GeradorSchema> geradorSchema = habilitarDbUnit.geradorSchema();
        try {
            geradorSchema.newInstance().drop();
        } catch (InstantiationException | IllegalAccessException exception) {
            // TODO Esqueleto de bloco catch gerado automaticamente
            exception.printStackTrace();
        }
    }

    @Override
    public boolean isHabilitada(Class<?> classeTeste) {
        return classeTeste.getAnnotation(HabilitarDbUnit.class) != null;
    }

    @Override
    public void inicializar(Class<?> classeTeste, Configuracao configuracao, RunNotifier notifier) {
        LOGGER.info("DBUnit habilitado");
        HabilitarDbUnit habilitarDbUnit = classeTeste.getAnnotation(HabilitarDbUnit.class);
        dbUnitRunner = new DbUnitRunner(classeTeste, habilitarDbUnit.nomeSchema(), configuracao);
    }

    @Override
    public Statement criarStatement(Class<?> classeTeste, Statement defaultStatement, FrameworkMethod method) {
        LOGGER.info("Ativando DBUnit");
        return dbUnitRunner.criarStatement(defaultStatement, method);
    }
}
