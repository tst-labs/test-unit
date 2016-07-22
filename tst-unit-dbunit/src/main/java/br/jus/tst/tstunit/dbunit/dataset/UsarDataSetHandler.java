package br.jus.tst.tstunit.dbunit.dataset;

import java.io.*;
import java.sql.Connection;
import java.util.*;
import java.util.function.Supplier;

import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.runners.model.FrameworkMethod;

import br.jus.tst.tstunit.dbunit.annotation.*;

/**
 * Classe que auxilia a criação de instâncias de {@link DatabaseLoader} a partir de uma anotação {@link UsarDataSet}.
 * 
 * @author Thiago Miranda
 * @since 21 de jul de 2016
 */
public class UsarDataSetHandler implements AnnotationHandler<DatabaseLoader>, Serializable {

    private static final long serialVersionUID = -476332749779353903L;

    private final String datasetsDirectory;
    private final Supplier<Connection> jdbcConnectionSupplier;
    private final AnnotationExtractor annotationExtractor;

    private DatabaseOperation operacaoAntesTestes;
    private DatabaseOperation operacaoAposTestes;
    private String nomeSchema;
    private IDataTypeFactory dataTypeFactory;

    /**
     * Cria uma nova instância.
     * 
     * @param datasetsDirectory
     *            o diretório onde se encontram os <em>datasets</em>
     * @param operacaoAntesTestes
     *            operação a ser executada antes de cada teste
     * @param operacaoAposTestes
     *            operação a ser executada após cada teste
     * @param jdbcConnectionSupplier
     *            utilizado para obter conexões JDBC
     * @param annotationExtractor
     *            utilizado para identificar anotações na classe e nos métodos de teste
     * @throws NullPointerException
     *             caso qualquer parâmetro seja {@code null}
     */
    public UsarDataSetHandler(String datasetsDirectory, DatabaseOperation operacaoAntesTestes, DatabaseOperation operacaoAposTestes,
            Supplier<Connection> jdbcConnectionSupplier, AnnotationExtractor annotationExtractor) {
        this.datasetsDirectory = Objects.requireNonNull(datasetsDirectory, "datasetsDirectory");
        this.operacaoAntesTestes = Objects.requireNonNull(operacaoAntesTestes, "operacaoAntesTestes");
        this.operacaoAposTestes = Objects.requireNonNull(operacaoAposTestes, "operacaoAposTestes");
        this.jdbcConnectionSupplier = Objects.requireNonNull(jdbcConnectionSupplier, "jdbcConnectionSupplier");
        this.annotationExtractor = Objects.requireNonNull(annotationExtractor, "annotationExtractor");
    }

    /**
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    @Override
    public Optional<DatabaseLoader> processar(FrameworkMethod method) {
        Optional<UsarDataSet> usarDataSet = annotationExtractor.getAnnotationsFromMethodOrClass(Objects.requireNonNull(method, "method"), UsarDataSet.class)
                .stream().findFirst();

        Optional<DatabaseLoader> databaseLoaderOptional;
        if (usarDataSet.isPresent()) {
            DatabaseLoader databaseLoader = new DatabaseLoader(buildCaminhoArquivo(datasetsDirectory, usarDataSet.get().value()), operacaoAntesTestes,
                    operacaoAposTestes, jdbcConnectionSupplier);
            databaseLoader.setSchema(nomeSchema);
            databaseLoader.setDataTypeFactory(dataTypeFactory);
            databaseLoaderOptional = Optional.of(databaseLoader);
        } else {
            databaseLoaderOptional = Optional.empty();
        }

        return databaseLoaderOptional;
    }

    private String buildCaminhoArquivo(String directory, String nomeArquivo) {
        return directory + File.separatorChar + nomeArquivo;
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

    public DatabaseOperation getOperacaoAntesTestes() {
        return operacaoAntesTestes;
    }

    public DatabaseOperation getOperacaoAposTestes() {
        return operacaoAposTestes;
    }

    public String getNomeSchema() {
        return nomeSchema;
    }

    public void setNomeSchema(String nomeSchema) {
        this.nomeSchema = nomeSchema;
    }

    public IDataTypeFactory getDataTypeFactory() {
        return dataTypeFactory;
    }

    public void setDataTypeFactory(IDataTypeFactory dataTypeFactory) {
        this.dataTypeFactory = dataTypeFactory;
    }
}
