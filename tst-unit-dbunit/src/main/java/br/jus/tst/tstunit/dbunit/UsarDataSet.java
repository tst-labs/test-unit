package br.jus.tst.tstunit.dbunit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

/**
 * Indica que uma classe ou método de teste necessita do uso de um arquivo DataSet do DBUnit.
 * 
 * @author Thiago Miranda
 * @since 4 de jul de 2016
 */
@Documented
@Inherited
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface UsarDataSet {

    /**
     * O nome do arquivo necessário.
     * 
     * @return o nome do arquivo
     */
    String value();
}
