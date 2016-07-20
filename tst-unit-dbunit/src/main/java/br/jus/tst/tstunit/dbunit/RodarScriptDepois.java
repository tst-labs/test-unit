package br.jus.tst.tstunit.dbunit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

/**
 * <p>
 * Indica que um método de teste necessita que um arquivo de script seja executado DEPOIS da execução do teste.
 * </p>
 * <p>
 * Caso a anotação seja aplicada à classe de testes, o script será rodado após de cada um dos seus métodos de teste.
 * </p>
 * <p>
 * Em cenários onde a anotação é definida tanto na classe quanto no método de testes, ambos serão executados, porém a anotação definida a nível de classe terá
 * precedência sobre as de método.
 * </p>
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
     * O nome ou caminho do arquivo de script. Podem ser definidos múltiplos arquivos, de modo que eles serão executados na ordem em que forem declarados.
     * 
     * @return o nome ou caminho do arquivo
     */
    String[] value();
}
