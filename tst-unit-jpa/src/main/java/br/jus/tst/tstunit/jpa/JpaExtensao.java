package br.jus.tst.tstunit.jpa;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.*;
import org.slf4j.*;

import br.jus.tst.tstunit.*;

/**
 * {@link Extensao} que habilita o JPA nos testes.
 * 
 * @author Thiago Miranda
 * @since 5 de jul de 2016
 */
public class JpaExtensao extends AbstractExtensao<HabilitarJpa> {

    private static final long serialVersionUID = -3496955917548402L;
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaExtensao.class);

    private GeradorSchema geradorSchema;

    /**
     * Cria uma nova instância da extensão para a classe de testes informada.
     * 
     * @param classeTestes
     *            a classe de testes
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    public JpaExtensao(Class<?> classeTestes) {
        super(classeTestes);
    }

    @Override
    public void beforeTestes(Object instancia) {
        LOGGER.info("Criando schema através do {}", geradorSchema);
        geradorSchema.criar();
    }

    @Override
    public void inicializar(Configuracao configuracao, RunNotifier notifier) throws TstUnitException {
        assertExtensaoHabilitada();

        HabilitarJpa habilitarJpa = classeTeste.getAnnotation(HabilitarJpa.class);
        LOGGER.info("JPA habilitado");

        String unidadePersistencia = habilitarJpa.persistenceUnitName();
        LOGGER.info("Unidade de persistência: {}", unidadePersistencia);
        TestEntityManagerFactoryProducer.setNomeUnidadePersistencia(unidadePersistencia);

        geradorSchema = criarGeradorSchema(habilitarJpa);
    }

    private GeradorSchema criarGeradorSchema(HabilitarJpa habilitarJpa) throws TstUnitException {
        Class<? extends GeradorSchema> classeGeradorSchema = habilitarJpa.geradorSchema();
        GeradorSchema instanciaGeradorSchema;

        LOGGER.debug("Criando instância do gerador de schema configurado: {}", classeGeradorSchema);
        try {
            instanciaGeradorSchema = classeGeradorSchema.newInstance();
        } catch (InstantiationException | IllegalAccessException exception) {
            throw new TstUnitException("Erro ao instanciar gerador de schema: " + classeGeradorSchema, exception);
        }

        return instanciaGeradorSchema;
    }

    @Override
    public Statement criarStatement(Statement defaultStatement, FrameworkMethod method) {
        assertExtensaoHabilitada();
        return defaultStatement;
    }
}
