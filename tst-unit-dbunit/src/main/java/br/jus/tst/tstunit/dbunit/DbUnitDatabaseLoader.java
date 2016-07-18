package br.jus.tst.tstunit.dbunit;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.function.Supplier;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.*;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.*;

/**
 * Classe responsável por efetuar operações sobre o banco de dados para os testes.
 * 
 * @author Thiago Miranda
 * @since 4 de jul de 2016
 */
public class DbUnitDatabaseLoader implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbUnitDatabaseLoader.class);

    private static final long serialVersionUID = 4244174398976628116L;

    private transient final Supplier<Connection> jdbcConnectionSupplier;
    private transient final String nomeArquivoDataSet;

    private transient FlatXmlDataSet dataSet;

    private IDataTypeFactory dataTypeFactory;
    private String schema;

    /**
     * Cria uma nova instância.
     * 
     * @param nomeArquivoDataSet
     *            nome do arquivo de DataSet do DBUnit sendo utilizado
     * @param jdbcConnectionSupplier
     *            utilizado para obter conexões JDBC
     * @throws NullPointerException
     *             caso qualquer parâmetro seja {@code null}
     */
    public DbUnitDatabaseLoader(String nomeArquivoDataSet, Supplier<Connection> jdbcConnectionSupplier) {
        this.nomeArquivoDataSet = Objects.requireNonNull(nomeArquivoDataSet, "nomeArquivoDataSet");
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

            if (dataTypeFactory != null) {
                connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
            }

            dataSet = carregarDataSet();
            DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
        } catch (DatabaseUnitException | SQLException exception) {
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

        try (Connection jdbcConnection = jdbcConnectionSupplier.get()) {
            IDatabaseConnection connection = openDbUnitConnection(jdbcConnection);
            DatabaseOperation.DELETE_ALL.execute(connection, dataSet);
        } catch (DatabaseUnitException | SQLException exception) {
            throw new DBUnitException("Erro ao efetuar limpeza do banco de dados", exception);
        }
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public IDataTypeFactory getDataTypeFactory() {
        return dataTypeFactory;
    }

    public void setDataTypeFactory(IDataTypeFactory dataTypeFactory) {
        this.dataTypeFactory = dataTypeFactory;
    }

    private FlatXmlDataSet carregarDataSet() throws DataSetException {
        LOGGER.debug("Carregando arquivo de DataSet: {}", nomeArquivoDataSet);
        Optional<InputStream> dataSetStreamOptional = Optional
                .ofNullable(Thread.currentThread().getContextClassLoader().getResourceAsStream(nomeArquivoDataSet));
        return new FlatXmlDataSetBuilder().setDtdMetadata(false).build(
                dataSetStreamOptional.orElseThrow(() -> new DBUnitException("O arquivo de DataSet não foi encontrado no classpath: " + nomeArquivoDataSet)));
    }

    private DatabaseConnection openDbUnitConnection(Connection jdbcConnection) throws DatabaseUnitException {
        LOGGER.debug("Criando conexão do DBUnit a partir da conexão JDBC: {}", jdbcConnection);
        return new DatabaseConnection(jdbcConnection, schema);
    }
}
