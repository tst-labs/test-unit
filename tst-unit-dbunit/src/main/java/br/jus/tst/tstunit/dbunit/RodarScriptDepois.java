package br.jus.tst.tstunit.dbunit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

/**
 * Indica que um método de teste necessita que um arquivo de script seja executado DEPOIS da execução do teste. Caso a anotação seja aplicada à classe de
 * testes, o script será rodado depois de cada um dos seus métodos de teste.
 * 
 * @author Thiago Miranda
 * @since 7 de jul de 2016
 */
@Documented
@Inherited
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface RodarScriptDepois {

    /**
     * O nome ou caminho do arquivo de script.
     * 
     * @return o nome ou caminho do arquivo
     */
    String value();
}
