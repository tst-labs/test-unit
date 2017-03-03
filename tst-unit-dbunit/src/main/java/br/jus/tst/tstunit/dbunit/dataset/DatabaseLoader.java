package br.jus.tst.tstunit.dbunit.dataset;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.function.Supplier;

import org.apache.commons.lang3.Validate;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.*;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.*;

import br.jus.tst.tstunit.dbunit.DBUnitException;
import br.jus.tst.tstunit.dbunit.jdbc.JdbcException;

/**
 * Classe responsável por efetuar operações sobre o banco de dados para os testes.
 * 
 * @author Thiago Miranda
 * @since 4 de jul de 2016
 */
public class DatabaseLoader implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseLoader.class);
    private static final long serialVersionUID = 4244174398976628116L;

    private transient final Supplier<Connection> jdbcConnectionSupplier;
    private transient final String nomeArquivoDataSet;

    private transient FlatXmlDataSet dataSet;

    private DatabaseOperation operacaoAntesTestes;
    private DatabaseOperation operacaoAposTestes;
    private Optional<IDataTypeFactory> dataTypeFactoryOptional;
    private String schema;

    /**
     * Cria uma nova instância.
     * 
     * @param nomeArquivoDataSet
     *            nome do arquivo de DataSet do DBUnit sendo utilizado
     * @param operacaoAntesTestes
     *            operação a ser executada antes de cada teste
     * @param operacaoAposTestes
     *            operação a ser executada após cada teste
     * @param jdbcConnectionSupplier
     *            utilizado para obter conexões JDBC
     * @throws NullPointerException
     *             caso qualquer parâmetro seja {@code null}
     */
    public DatabaseLoader(String nomeArquivoDataSet, DatabaseOperation operacaoAntesTestes, DatabaseOperation operacaoAposTestes,
            Supplier<Connection> jdbcConnectionSupplier) {
        this.nomeArquivoDataSet = Objects.requireNonNull(nomeArquivoDataSet, "nomeArquivoDataSet");
        this.operacaoAntesTestes = Objects.requireNonNull(operacaoAntesTestes, "operacaoAntesTestes");
        this.operacaoAposTestes = Objects.requireNonNull(operacaoAposTestes, "operacaoAposTestes");
        this.jdbcConnectionSupplier = Objects.requireNonNull(jdbcConnectionSupplier, "jdbcConnectionSupplier");
    }

    /**
     * Carrega os dados no banco de dados.
     * 
     * @throws DBUnitException
     *             caso ocorra algum erro ao executar a operação
     */
    public void carregarBancoDados() {
        LOGGER.debug("Carga do banco de dados");

        try (Connection jdbcConnection = jdbcConnectionSupplier.get()) {
            IDatabaseConnection connection = openDbUnitConnection(jdbcConnection);

            dataTypeFactoryOptional.ifPresent(dataTypeFactory -> {
                connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
            });

            dataSet = carregarDataSet();
            LOGGER.debug("DataSet carregado: {}", dataSet);

            LOGGER.debug("Executando operação: {}", operacaoAntesTestes);
            operacaoAntesTestes.execute(connection, dataSet);
        } catch (DatabaseUnitException | SQLException | JdbcException exception) {
            throw new DBUnitException("Erro ao efetuar carga do banco de dados", exception);
        }
    }

    /**
     * Efetua a limpeza dos dados do banco.
     * 
     * @throws DBUnitException
     *             caso ocorra algum erro ao executar a operação
     */
    public void limparBancoDados() {
        LOGGER.debug("Limpeza do banco de dados");
        Validate.validState(dataSet != null, "DataSet não carregado");

        try (Connection jdbcConnection = jdbcConnectionSupplier.get()) {
            IDatabaseConnection connection = openDbUnitConnection(jdbcConnection);

            LOGGER.debug("Executando operação: {}", operacaoAposTestes);
            operacaoAposTestes.execute(connection, dataSet);
        } catch (DatabaseUnitException | SQLException | JdbcException exception) {
            throw new DBUnitException("Erro ao efetuar limpeza do banco de dados", exception);
        }
    }

    public DatabaseOperation getOperacaoAntesTestes() {
        return operacaoAntesTestes;
    }

    public DatabaseOperation getOperacaoAposTestes() {
        return operacaoAposTestes;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Optional<IDataTypeFactory> getDataTypeFactory() {
        return dataTypeFactoryOptional;
    }

    public void setDataTypeFactory(Optional<IDataTypeFactory> dataTypeFactoryOptional) {
        this.dataTypeFactoryOptional = dataTypeFactoryOptional;
    }

    private FlatXmlDataSet carregarDataSet() throws DataSetException {
        LOGGER.debug("Carregando arquivo de DataSet: {}", nomeArquivoDataSet);
        Optional<InputStream> dataSetStreamOptional = Optional.ofNullable(Thread.currentThread().getContextClassLoader().getResourceAsStream(nomeArquivoDataSet));
        return new FlatXmlDataSetBuilder().setDtdMetadata(false)
                .build(dataSetStreamOptional.orElseThrow(() -> new DBUnitException("O arquivo de DataSet não foi encontrado no classpath: " + nomeArquivoDataSet)));
    }

    private DatabaseConnection openDbUnitConnection(Connection jdbcConnection) throws DatabaseUnitException {
        LOGGER.debug("Criando conexão do DBUnit a partir da conexão JDBC: {}", jdbcConnection);
        return new DatabaseConnection(jdbcConnection, schema);
    }
}
