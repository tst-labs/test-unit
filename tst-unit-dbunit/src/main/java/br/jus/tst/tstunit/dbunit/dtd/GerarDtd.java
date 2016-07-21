package br.jus.tst.tstunit.dbunit.dtd;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

/**
 * Indica que o arquivo DTD referente ao <em>schema</em> de banco de dados deve ser gerado ou atualizado.
 * 
 * @author Thiago Miranda
 * @since 14 de jul de 2016
 */
@Documented
@Retention(RUNTIME)
@Target({ METHOD })
public @interface GerarDtd {

    /**
     * Caminho do arquivo DTD.
     * 
     * @return o caminho do arquivo
     */
    String value();
}
