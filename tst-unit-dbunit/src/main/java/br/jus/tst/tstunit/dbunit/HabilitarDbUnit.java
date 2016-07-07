package br.jus.tst.tstunit.dbunit;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

import org.apache.commons.lang3.StringUtils;

/**
 * Habilita o DBUnit numa classe de teste.
 * 
 * @author Thiago Miranda
 * @since 1 de jul de 2016
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Inherited
@Documented
public @interface HabilitarDbUnit {

    /**
     * Nome do <em>schema</em> de banco de dados a ser utilizado pelo DBUnit.
     * 
     * @return nome do <em>schema</em> de banco de dados
     */
    String nomeSchema() default StringUtils.EMPTY;
}
