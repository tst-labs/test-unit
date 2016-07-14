package br.jus.tst.tstunit.dbunit;

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
     * Nome do arquivo DTD. Por padrão, o arquivo será gerado no mesmo diretório que os <em>datasets</em>.
     * 
     * @return o caminho do arquivo
     */
    String value();
}
