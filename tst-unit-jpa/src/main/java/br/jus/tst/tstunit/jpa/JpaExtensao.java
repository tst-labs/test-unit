package br.jus.tst.tstunit.jpa;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.*;
import org.slf4j.*;

import br.jus.tst.tstunit.*;

/**
 * {@link Extensao} que habilita o JPA nos testes.
 * 
 * @author Thiago Miranda
 * @sin ce 5 de jul de 2016
 */
public class JpaExtensao extends AbstractExtensao<HabilitarJpa> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JpaExtensao.class);

    private final GeradorSchema geradorSchema;

    public JpaExtensao(Class<?> classeTestes) {
        super(classeTestes);
        geradorSchema = new GeradorSchema();
    }

    @Override
    public void beforeTestes(Object instancia) {
        LOGGER.info("Criando schema");
        geradorSchema.create();
    }

    @Override
    public void inicializar(Configuracao configuracao, RunNotifier notifier) {
        LOGGER.info("JPA habilitado");
        String unidadePersistencia = classeTeste.getAnnotation(HabilitarJpa.class).persistenceUnitName();
        LOGGER.info("Unidade de persistência: {}", unidadePersistencia);
        TestEntityManagerFactoryProducer.setNomeUnidadePersistencia(unidadePersistencia);
    }

    @Override
    public Statement criarStatement(Statement defaultStatement, FrameworkMethod method) {
        return defaultStatement;
    }
}
