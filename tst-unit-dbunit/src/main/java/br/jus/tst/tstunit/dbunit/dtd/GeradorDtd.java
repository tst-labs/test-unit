package br.jus.tst.tstunit.dbunit.dtd;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.function.Supplier;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.*;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.xml.FlatDtdWriter;
import org.slf4j.*;

import br.jus.tst.tstunit.dbunit.DBUnitException;
import br.jus.tst.tstunit.dbunit.jdbc.JdbcException;

/**
 * Classe responsável por gerar o arquivo DTD referente a um <em>schema</em> de banco de dados.
 * 
 * @author ThiagoColbert
 * @since 29 de mai de 2016
 */
public class GeradorDtd implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeradorDtd.class);

    private static final long serialVersionUID = -4939611691160487785L;

    private transient final Supplier<Connection> jdbcConnectionSupplier;
    private transient final File arquivoDtd;

    private Optional<IDataTypeFactory> dataTypeFactoryOptional;

    /**
     * Cria um novo gerador de DTD.
     * 
     * @param jdbcConnectionSupplier
     *            utilizado para obter conexões JDBC
     * @param arquivoDtd
     *            o arquivo a ser gerado ou atualizado
     * @throws NullPointerException
     *             caso qualquer parâmetro seja {@code null}
     */
    public GeradorDtd(Supplier<Connection> jdbcConnectionSupplier, File arquivoDtd) {
        this.jdbcConnectionSupplier = Objects.requireNonNull(jdbcConnectionSupplier, "jdbcConnectionSupplier");
        this.arquivoDtd = Objects.requireNonNull(arquivoDtd, "arquivoDtd");
    }

    /**
     * Gera o arquivo DTD.
     * 
     * @throws JdbcException
     *             caso ocorra algum erro ao executar a operação
     */
    public void gerar() {
        LOGGER.info("Gerando arquivo DTD a partir do schema do banco");

        try (Connection jdbcConnection = jdbcConnectionSupplier.get()) {
            IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

            dataTypeFactoryOptional.ifPresent(dataTypeFactory -> {
                connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
            });

            IDataSet dataSet = connection.createDataSet();
            Writer out = new OutputStreamWriter(new FileOutputStream(arquivoDtd));
            FlatDtdWriter datasetWriter = new FlatDtdWriter(out);
            datasetWriter.setContentModel(FlatDtdWriter.CHOICE);
            datasetWriter.write(dataSet);
            LOGGER.info("Arquivo DTD gerado: {}", arquivoDtd);
        } catch (DatabaseUnitException | FileNotFoundException | SQLException exception) {
            throw new DBUnitException("Erro ao gerar arquivo DTD do schema do banco", exception);
        }
    }

    public Optional<IDataTypeFactory> getDataTypeFactory() {
        return dataTypeFactoryOptional;
    }

    public void setDataTypeFactory(Optional<IDataTypeFactory> dataTypeFactoryOptional) {
        this.dataTypeFactoryOptional = dataTypeFactoryOptional;
    }
}
