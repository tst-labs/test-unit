package br.jus.tst.tstunit.dbunit;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.function.Supplier;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.*;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.xml.*;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.*;

import br.jus.tst.tstunit.TstUnitException;

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

    public void carregarBancoDados() throws TstUnitException {
        LOGGER.debug("Carga do banco de dados");

        try (Connection jdbcConnection = jdbcConnectionSupplier.get()) {
            IDatabaseConnection connection = openDbUnitConnection(jdbcConnection);
            connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory()); // TODO H2 hard-coded
            dataSet = carregarDataSet();
            DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
        } catch (DatabaseUnitException | SQLException exception) {
            throw new TstUnitException("Erro ao efetuar carga do banco de dados", exception);
        }
    }

    public void limparBancoDados() throws TstUnitException {
        LOGGER.debug("Limpeza do banco de dados");

        try (Connection jdbcConnection = jdbcConnectionSupplier.get()) {
            IDatabaseConnection connection = openDbUnitConnection(jdbcConnection);
            DatabaseOperation.DELETE_ALL.execute(connection, dataSet);
        } catch (DatabaseUnitException | SQLException exception) {
            throw new TstUnitException("Erro ao efetuar limpeza do banco de dados", exception);
        }
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    private FlatXmlDataSet carregarDataSet() throws DataSetException, TstUnitException {
        LOGGER.debug("Carregando arquivo de DataSet: {}", nomeArquivoDataSet);
        Optional<InputStream> dataSetStreamOptional = Optional
                .ofNullable(Thread.currentThread().getContextClassLoader().getResourceAsStream(nomeArquivoDataSet));
        return new FlatXmlDataSetBuilder().setDtdMetadata(false).build(
                dataSetStreamOptional.orElseThrow(() -> new TstUnitException("O arquivo de DataSet não foi encontrado no classpath: " + nomeArquivoDataSet)));
    }

    private DatabaseConnection openDbUnitConnection(Connection jdbcConnection) throws DatabaseUnitException {
        LOGGER.debug("Criando conexão do DBUnit a partir da conexão JDBC: {}", jdbcConnection);
        return new DatabaseConnection(jdbcConnection, schema);
    }
}
