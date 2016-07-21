package br.jus.tst.tstunit.dbunit.dtd;

import java.io.*;
import java.sql.Connection;
import java.util.*;
import java.util.function.Supplier;

import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.junit.runners.model.FrameworkMethod;

import br.jus.tst.tstunit.dbunit.annotation.AnnotationHandler;

/**
 * Classe que auxilia a criação de instâncias de {@link GeradorDtd} a partir de uma anotação {@link GerarDtd}.
 * 
 * @author Thiago Miranda
 * @since 21 de jul de 2016
 */
public class GerarDtdHandler implements AnnotationHandler<GeradorDtd>, Serializable {

    private static final long serialVersionUID = 4493224015962106560L;

    private final Supplier<Connection> jdbcConnectionSupplier;

    private IDataTypeFactory dataTypeFactory;

    /**
     * Cria uma nova instância.
     * 
     * @param jdbcConnectionSupplier
     *            utilizado para obter conexões JDBC
     * @throws NullPointerException
     *             caso qualquer parâmetro seja {@code null}
     */
    public GerarDtdHandler(Supplier<Connection> jdbcConnectionSupplier) {
        this.jdbcConnectionSupplier = Objects.requireNonNull(jdbcConnectionSupplier, "jdbcConnectionSupplier");
        this.dataTypeFactory = null;
    }

    /**
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    @Override
    public Optional<GeradorDtd> processar(FrameworkMethod method) {
        Optional<GeradorDtd> geradorDtdOptional;

        GerarDtd gerarDtd = Objects.requireNonNull(method, "method").getAnnotation(GerarDtd.class);
        if (gerarDtd != null) {
            GeradorDtd geradorDtd = new GeradorDtd(jdbcConnectionSupplier, new File(gerarDtd.value()));
            geradorDtd.setDataTypeFactory(dataTypeFactory);
            geradorDtdOptional = Optional.of(geradorDtd);
        } else {
            geradorDtdOptional = Optional.empty();
        }

        return geradorDtdOptional;
    }

    public Supplier<Connection> getJdbcConnectionSupplier() {
        return jdbcConnectionSupplier;
    }

    public IDataTypeFactory getDataTypeFactory() {
        return dataTypeFactory;
    }

    public void setDataTypeFactory(IDataTypeFactory dataTypeFactory) {
        this.dataTypeFactory = dataTypeFactory;
    }
}
