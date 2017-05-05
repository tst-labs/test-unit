package br.jus.tst.tstunit.dbunit.dataset;

import java.io.*;
import java.sql.Connection;
import java.util.*;
import java.util.function.Supplier;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.xml.*;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.*;

import br.jus.tst.tstunit.annotation.*;
import br.jus.tst.tstunit.dbunit.DBUnitException;
import br.jus.tst.tstunit.dbunit.operation.DbUnitOperations;

/**
 * Classe que auxilia a criação de instâncias de {@link DatabaseLoader} a partir de uma anotação {@link UsarDataSet}.
 * 
 * @author Thiago Miranda
 * @since 21 de jul de 2016
 */
public class UsarDataSetHandler implements AnnotationHandler<DatabaseLoader>, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsarDataSetHandler.class);

    private static final long serialVersionUID = -476332749779353903L;

    private final String datasetsDirectory;
    private final Supplier<Connection> jdbcConnectionSupplier;
    private final AnnotationExtractor annotationExtractor;

    private String operacaoAntesTestesDefault;
    private String operacaoAposTestesDefault;
    private String nomeSchema;
    private Optional<IDataTypeFactory> dataTypeFactoryOptional;

    /**
     * Cria uma nova instância.
     * 
     * @param datasetsDirectory
     *            o diretório onde se encontram os <em>datasets</em>
     * @param operacaoAntesTestesDefault
     *            operação padrão a ser executada antes de cada teste
     * @param operacaoAposTestesDefault
     *            operação padrão a ser executada após cada teste
     * @param jdbcConnectionSupplier
     *            utilizado para obter conexões JDBC
     * @param annotationExtractor
     *            utilizado para identificar anotações na classe e nos métodos de teste
     * @throws NullPointerException
     *             caso qualquer parâmetro seja {@code null}
     */
    public UsarDataSetHandler(String datasetsDirectory, String operacaoAntesTestesDefault, String operacaoAposTestesDefault, Supplier<Connection> jdbcConnectionSupplier,
            AnnotationExtractor annotationExtractor) {
        this.datasetsDirectory = Objects.requireNonNull(datasetsDirectory, "datasetsDirectory");
        this.operacaoAntesTestesDefault = Objects.requireNonNull(operacaoAntesTestesDefault, "operacaoAntesTestesDefault");
        this.operacaoAposTestesDefault = Objects.requireNonNull(operacaoAposTestesDefault, "operacaoAposTestesDefault");
        this.jdbcConnectionSupplier = Objects.requireNonNull(jdbcConnectionSupplier, "jdbcConnectionSupplier");
        this.annotationExtractor = Objects.requireNonNull(annotationExtractor, "annotationExtractor");
    }

    /**
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    @Override
    public Optional<DatabaseLoader> processar(FrameworkMethod method) {
        List<OperacaoDataSet> operacaoDataSets = new ArrayList<>();

        annotationExtractor.getAnnotationsFromMethodOrClass(Objects.requireNonNull(method, "method"), UsarDataSet.class).stream().forEach(usarDataSet -> {
            String nomeArquivo = buildCaminhoArquivo(datasetsDirectory, usarDataSet.value());
            operacaoDataSets.add(OperacaoDataSet.nova().comDataSet(carregarDataSet(nomeArquivo))
                    .operacaoPreTestes(DbUnitOperations.carregarOperacao(usarDataSet.operacaoPreTestes().getNome(), operacaoAntesTestesDefault))
                    .operacaoPosTestes(DbUnitOperations.carregarOperacao(usarDataSet.operacaoPosTestes().getNome(), operacaoAposTestesDefault)).build());
        });

        Optional<DatabaseLoader> databaseLoaderOptional;
        if (!operacaoDataSets.isEmpty()) {
            DatabaseLoader databaseLoader = new DatabaseLoader(operacaoDataSets, jdbcConnectionSupplier);
            databaseLoader.setSchema(nomeSchema);
            databaseLoader.setDataTypeFactory(dataTypeFactoryOptional);
            databaseLoaderOptional = Optional.of(databaseLoader);
        } else {
            databaseLoaderOptional = Optional.empty();
        }

        return databaseLoaderOptional;
    }

    private String buildCaminhoArquivo(String directory, String nomeArquivo) {
        return directory + File.separatorChar + nomeArquivo;
    }

    private FlatXmlDataSet carregarDataSet(String nomeArquivo) {
        LOGGER.debug("Carregando arquivo de DataSet: {}", nomeArquivo);
        Optional<InputStream> dataSetStreamOptional = Optional.ofNullable(Thread.currentThread().getContextClassLoader().getResourceAsStream(nomeArquivo));
        try {
            return new FlatXmlDataSetBuilder().setDtdMetadata(false)
                    .build(dataSetStreamOptional.orElseThrow(() -> new DBUnitException("O arquivo de DataSet não foi encontrado no classpath: " + nomeArquivo)));
        } catch (DataSetException exception) {
            throw new DBUnitException("Erro ao carregar arquivo de DataSet: " + nomeArquivo, exception);
        }
    }

    public String getDatasetsDirectory() {
        return datasetsDirectory;
    }

    public Supplier<Connection> getJdbcConnectionSupplier() {
        return jdbcConnectionSupplier;
    }

    public AnnotationExtractor getAnnotationExtractor() {
        return annotationExtractor;
    }

    public String getOperacaoAntesTestesDefault() {
        return operacaoAntesTestesDefault;
    }

    public String getOperacaoAposTestesDefault() {
        return operacaoAposTestesDefault;
    }

    public String getNomeSchema() {
        return nomeSchema;
    }

    public void setNomeSchema(String nomeSchema) {
        this.nomeSchema = nomeSchema;
    }

    public Optional<IDataTypeFactory> getDataTypeFactory() {
        return dataTypeFactoryOptional;
    }

    public void setDataTypeFactory(Optional<IDataTypeFactory> dataTypeFactoryOptional) {
        this.dataTypeFactoryOptional = dataTypeFactoryOptional;
    }
}
