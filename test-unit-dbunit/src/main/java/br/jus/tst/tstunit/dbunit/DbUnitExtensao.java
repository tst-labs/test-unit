package br.jus.tst.tstunit.dbunit;

import org.apache.commons.lang3.StringUtils;
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

    private static final long serialVersionUID = 4420119979355499371L;
    private static final Logger LOGGER = LoggerFactory.getLogger(DbUnitExtensao.class);

    private transient DbUnitRunner dbUnitRunner;

    /**
     * Cria uma nova instância da extensão para a classe de testes informada.
     * 
     * @param classeTeste
     *            a classe de testes
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    public DbUnitExtensao(Class<?> classeTeste) {
        super(classeTeste);
    }

    @Override
    public void inicializar(Configuracao configuracao, RunNotifier notifier) throws TstUnitException {
        assertExtensaoHabilitada();
        LOGGER.info("DBUnit habilitado");
        HabilitarDbUnit habilitarDbUnit = classeTeste.getAnnotation(HabilitarDbUnit.class);
        dbUnitRunner = new DbUnitRunner(classeTeste, StringUtils.stripToNull(habilitarDbUnit.nomeSchema()), configuracao);
    }

    @Override
    public Statement criarStatement(Statement defaultStatement, FrameworkMethod method) throws TstUnitException {
        assertExtensaoHabilitada();
        LOGGER.info("Ativando DBUnit");
        return dbUnitRunner.criarStatement(defaultStatement, method);
    }
}
