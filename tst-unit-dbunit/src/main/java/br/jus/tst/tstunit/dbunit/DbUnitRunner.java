package br.jus.tst.tstunit.dbunit;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.runners.model.*;
import org.slf4j.*;

import br.jus.tst.tstunit.*;
import br.jus.tst.tstunit.dbunit.annotation.AnnotationExtractor;
import br.jus.tst.tstunit.dbunit.dataset.UsarDataSetHandler;
import br.jus.tst.tstunit.dbunit.dtd.GerarDtdHandler;
import br.jus.tst.tstunit.dbunit.jdbc.JdbcConnectionSupplier;
import br.jus.tst.tstunit.dbunit.script.RodarScriptHandler;

/**
 * Classe responsável por rodar o DBUnit em métodos de teste.
 * 
 * @author Thiago Miranda
 * @since 4 de jul de 2016
 */
public class DbUnitRunner implements Serializable {

    private static final long serialVersionUID = -1862055645135908142L;
    private static final Logger LOGGER = LoggerFactory.getLogger(DbUnitRunner.class);

    private static final String DIRETORIO_DATASETS_PADRAO = "datasets";
    private static final String DIRETORIO_SCRIPTS_PADRAO = "scripts";

    private static final String OPERACAO_BEFORE_TESTS_PADRAO = "CLEAN_INSERT";
    private static final String OPERACAO_AFTER_TESTS_PADRAO = "DELETE_ALL";

    private transient final Class<?> classeTeste;
    private transient final Configuracao configuracao;

    private transient final JdbcConnectionSupplier jdbcConnectionSupplier;
    private transient final AnnotationExtractor annotationExtractor;

    private transient final GerarDtdHandler gerarDtdHandler;
    private transient final RodarScriptHandler rodarScriptHandler;
    private transient final UsarDataSetHandler usarDataSetHandler;

    /**
     * Cria uma nova instância definindo todos os parâmetros.
     * 
     * @param classeTeste
     *            a classe dos testes
     * @param nomeSchema
     *            nome do schema de banco de dados (opcional)
     * @param configuracao
     *            configurações a serem utilizadas
     * @throws NullPointerException
     *             caso {@code classeTeste} ou {@code configuracao} seja {@code null}
     * @throws DBUnitException
     *             caso alguma configuração seja inválida
     * @throws TstUnitException
     *             caso ocorra algum erro ao carregar as configurações
     */
    public DbUnitRunner(Class<?> classeTeste, String nomeSchema, Configuracao configuracao) throws TstUnitException {
        this.classeTeste = Objects.requireNonNull(classeTeste, "classeTeste");
        this.configuracao = Objects.requireNonNull(configuracao, "configuracao");
        this.configuracao.carregar();

        jdbcConnectionSupplier = new JdbcConnectionSupplier(getConfiguracoesJdbc());

        gerarDtdHandler = new GerarDtdHandler(jdbcConnectionSupplier);
        gerarDtdHandler.setDataTypeFactory(getDataTypeFactory());

        annotationExtractor = new AnnotationExtractor(classeTeste);

        rodarScriptHandler = new RodarScriptHandler(StringUtils.defaultIfBlank(getDiretorioScriptsConfigurado(), DIRETORIO_SCRIPTS_PADRAO),
                jdbcConnectionSupplier, annotationExtractor);

        usarDataSetHandler = new UsarDataSetHandler(getDiretorioDatasets(), carregarOperacao("beforeTests.operation", OPERACAO_BEFORE_TESTS_PADRAO),
                carregarOperacao("afterTests.operation", OPERACAO_AFTER_TESTS_PADRAO), jdbcConnectionSupplier, annotationExtractor);
        usarDataSetHandler.setDataTypeFactory(getDataTypeFactory());
        usarDataSetHandler.setNomeSchema(nomeSchema);
    }

    private DatabaseOperation carregarOperacao(String nomePropriedade, String valorPadrao) {
        String operacaoString = Optional.ofNullable(getConfiguracoesDbUnit().getProperty(nomePropriedade)).orElse(valorPadrao);

        DatabaseOperation operacao;
        try {
            operacao = (DatabaseOperation) DatabaseOperation.class.getField(operacaoString).get(null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException exception) {
            throw new DBUnitException("Operação inválida: " + operacaoString, exception);
        }
        return operacao;
    }

    /**
     * Cria um {@link Statement} que funcione com o DBUnit.
     * 
     * @param statement
     *            o statement original
     * @param method
     *            o método de teste
     * @return o statement criado
     * @throws NullPointerException
     *             caso qualquer parâmetro seja {@code null}
     * @throws TstUnitException
     *             caso ocorra algum erro durante a operação
     */
    public Statement criarStatement(Statement statement, FrameworkMethod method) throws TstUnitException {
        Objects.requireNonNull(statement, "statement");
        Objects.requireNonNull(method, "method");

        LOGGER.debug("Criando Statement para o método {}", method);
        return DbUnitStatement.aPartirDo(statement).usandoDatabaseLoader(usarDataSetHandler.processar(method))
                .usandoScriptRunner(rodarScriptHandler.processar(method)).usandoGeradorDtd(gerarDtdHandler.processar(method)).build();
    }

    private String getDiretorioDatasets() {
        return StringUtils.defaultIfBlank(getDiretorioDatasetsConfigurado(), DIRETORIO_DATASETS_PADRAO);
    }

    private String getDiretorioDatasetsConfigurado() {
        return (String) getConfiguracoesDbUnit().get("datasets.dir");
    }

    private String getDiretorioScriptsConfigurado() {
        return (String) getConfiguracoesDbUnit().get("scripts.dir");
    }

    private IDataTypeFactory getDataTypeFactory() {
        String dataTypeFactoryClass = (String) getConfiguracoesDbUnit().get("dataTypeFactoryClass");
        try {
            return (IDataTypeFactory) Class.forName(dataTypeFactoryClass).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException exception) {
            throw new DBUnitException("Erro ao instanciar classe de DataTypeFactory configurada: " + dataTypeFactoryClass, exception);
        }
    }

    private Properties getConfiguracoesJdbc() {
        return configuracao.getSubPropriedades("jdbc");
    }

    private Properties getConfiguracoesDbUnit() {
        return configuracao.getSubPropriedades("dbunit");
    }

    public Class<?> getClasseTeste() {
        return classeTeste;
    }
}