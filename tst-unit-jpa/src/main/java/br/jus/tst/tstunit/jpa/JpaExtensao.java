package br.jus.tst.tstunit.jpa;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.*;
import org.slf4j.*;

import br.jus.tst.tstunit.*;
import br.jus.tst.tstunit.jpa.HabilitarJpa.UnidadePersistencia;
import br.jus.tst.tstunit.jpa.cdi.EntityManagerFactoryProducerExtension;
import br.jus.tst.tstunit.jpa.util.*;

/**
 * {@link Extensao} que habilita o JPA nos testes.
 * 
 * @author Thiago Miranda
 * @since 5 de jul de 2016
 */
public class JpaExtensao extends AbstractExtensao<HabilitarJpa> {

    private static final String PREFIXO_PROPRIEDADES_ORM = "jpa.orm";
    private static final String PREFIXO_PROPRIEDADES_JDBC = "jdbc";

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
        try {
            geradorSchema.criar();
        } catch (JpaException exception) {
            LOGGER.error("Erro ao criar schema", exception);
            throw exception;
        }
    }

    @Override
    public void afterTestes() {
        LOGGER.info("Derrubando schema através do {}", geradorSchema);
        try {
            geradorSchema.destruir();
        } catch (JpaException exception) {
            LOGGER.error("Erro ao derrubar schema", exception);
            throw exception;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void inicializar(Configuracao configuracao, RunNotifier notifier) throws TstUnitException {
        assertExtensaoHabilitada();

        HabilitarJpa habilitarJpa = classeTeste.getAnnotation(HabilitarJpa.class);
        LOGGER.info("JPA habilitado");

        UnidadePersistencia[] unidadesPersistencia = habilitarJpa.unidadesPersistencia();
        if (unidadesPersistencia.length == 0) {
            unidadesPersistencia = new UnidadePersistenciaCreator()
                    .criarAnotacaoUnidadePersistencia(StringUtils.defaultIfEmpty(habilitarJpa.nomeUnidadePersistencia(), habilitarJpa.persistenceUnitName()), Unico.class);
        }

        LOGGER.info("Unidades de persistência: {}", (Object[]) unidadesPersistencia);
        EntityManagerFactoryProducerExtension.setUnidadesPersistencia(unidadesPersistencia);

        Map<String, String> propriedadesAdicionais = configuracao.getSubPropriedades(PREFIXO_PROPRIEDADES_ORM).entrySet().stream()
                .collect(Collectors.toMap(entry -> (String) entry.getKey(), entry -> (String) entry.getValue()));
        propriedadesAdicionais.putAll(toOrmProperties(configuracao.getSubPropriedades(PREFIXO_PROPRIEDADES_JDBC)));

        EntityManagerFactoryProducerExtension.setPropriedadesAdicionais(propriedadesAdicionais);
        geradorSchema = new GeradorSchemaCreator().criarGeradorSchema(habilitarJpa.geradorSchema(), propriedadesAdicionais);
    }

    private Map<String, String> toOrmProperties(Properties propriedadesJdbc) {
        Map<String, String> ormProperties = new HashMap<>();
        replaceProperty(propriedadesJdbc, ormProperties, "driverClass", "hibernate.connection.driver_class")
                .replaceProperty(propriedadesJdbc, ormProperties, "url", "hibernate.connection.url")
                .replaceProperty(propriedadesJdbc, ormProperties, "user", "hibernate.connection.username")
                .replaceProperty(propriedadesJdbc, ormProperties, "password", "hibernate.connection.password");
        return ormProperties;
    }

    private JpaExtensao replaceProperty(Properties propriedadesJdbc, Map<String, String> ormProperties, String jdbcPropertyKey, String ormPropertyKey) {
        ormProperties.put(ormPropertyKey, propriedadesJdbc.getProperty(jdbcPropertyKey));
        ormProperties.remove(jdbcPropertyKey);
        return this;
    }

    @Override
    public Statement criarStatement(Statement defaultStatement, FrameworkMethod method) {
        assertExtensaoHabilitada();
        return defaultStatement;
    }
}
