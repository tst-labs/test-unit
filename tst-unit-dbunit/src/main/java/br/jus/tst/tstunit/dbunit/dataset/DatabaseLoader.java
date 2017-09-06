package br.jus.tst.tstunit.dbunit.dataset;

import java.io.Serializable;
import java.sql.*;
import java.util.*;
import java.util.function.Supplier;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.*;
import org.dbunit.dataset.datatype.IDataTypeFactory;
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
    private transient final List<OperacaoDataSet> operacoes;

    private Optional<IDataTypeFactory> dataTypeFactoryOptional;
    private String schema;

    /**
     * Cria uma nova instância.
     * 
     * @param operacoes
     *            DataSets e operações a serem utilizados
     * @param jdbcConnectionSupplier
     *            utilizado para obter conexões JDBC
     * @throws NullPointerException
     *             caso qualquer parâmetro seja {@code null}
     */
    public DatabaseLoader(List<OperacaoDataSet> operacoes, Supplier<Connection> jdbcConnectionSupplier) {
        this.operacoes = new ArrayList<>(Objects.requireNonNull(operacoes, "operacoes"));
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
                LOGGER.debug("Definindo propriedade de conexão: {} = {}", DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
                connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
            });

            operacoes.forEach(operacaoAtual -> executarOperacaoPreTestes(operacaoAtual, connection));
        } catch (DatabaseUnitException | SQLException | JdbcException exception) {
            throw new DBUnitException("Erro ao efetuar carga do banco de dados", exception);
        }
    }

    private void executarOperacaoPreTestes(OperacaoDataSet operacao, IDatabaseConnection connection) {
        try {
            LOGGER.debug("Executando operação pré-testes: {}", operacao);
            operacao.executarOperacaoPreTestes(connection);
        } catch (DatabaseUnitException | SQLException exception) {
            throw new DBUnitException("Erro ao executar operação pré-testes", exception);
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

            for (int i = operacoes.size() - 1; i >= 0; i--) {
                // processando na ordem inversa
                executarOperacaoPosTestes(operacoes.get(i), connection);
            }
        } catch (DatabaseUnitException | SQLException | JdbcException exception) {
            throw new DBUnitException("Erro ao efetuar limpeza do banco de dados", exception);
        }
    }

    private void executarOperacaoPosTestes(OperacaoDataSet operacao, IDatabaseConnection connection) {
        try {
            LOGGER.debug("Executando operação pós-testes: {}", operacao);
            operacao.executarOperacaoPosTestes(connection);
        } catch (DatabaseUnitException | SQLException exception) {
            throw new DBUnitException("Erro ao executar operação pós-testes", exception);
        }
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

    private DatabaseConnection openDbUnitConnection(Connection jdbcConnection) throws DatabaseUnitException {
        LOGGER.debug("Criando conexão do DBUnit a partir da conexão JDBC: {}", jdbcConnection);
        return new DatabaseConnection(jdbcConnection, schema);
    }
}
