package br.jus.tst.tstunit.dbunit.script;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

/**
 * <p>
 * Indica que um método de teste necessita que um arquivo de script seja executado antes da execução do teste.
 * </p>
 * <p>
 * Caso a anotação seja aplicada à classe de testes, o script será rodado antes de cada um dos seus métodos de teste.
 * </p>
 * <p>
 * Em cenários onde a anotação é definida tanto na classe quanto no método de testes, ambos serão executados, porém a anotação definida a nível de classe terá
 * precedência sobre as de método.
 * </p>
 * 
 * @author Thiago Miranda
 * @since 7 de jul de 2016
 * @see RodarScriptDepois
 */
@Documented
@Inherited
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface RodarScriptAntes {

    /**
     * <p>
     * O nome ou caminho do arquivo de script. Podem ser definidos múltiplos arquivos, de modo que eles serão executados na ordem em que forem declarados.
     * </p>
     * 
     * <p>
     * Exemplos:
     * </p>
     * 
     * <pre>
     * {@literal @}RodarScriptAntes("meu-script.sql")
     * 
     * {@literal @}RodarScriptAntes({ "meu-script-1.sql", "meu-script-2.sql" })
     * </pre>
     * 
     * @return o nome ou caminho do arquivo (ou arquivos)
     */
    String[] value();
}
