package br.jus.tst.tstunit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

import org.apache.commons.lang3.StringUtils;

/**
 * Determina que o nome dos métodos de teste de uma classe devem ser impressos na saída padrão assim que cada teste começar a executar.
 * 
 * @author Thiago Miranda
 * @since 11 de jul de 2016
 */
@Documented
@Inherited
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface ImprimirNomeTeste {

    /**
     * Determina se o nome dos testes deve ser impresso ou não.
     * 
     * @return {@code true}/{@code false}
     */
    boolean value() default true;

    /**
     * <p>
     * O formato utilizado nas mensagens contendo o nome do teste. Caso não seja definido, será utilizado o formato padrão. O formato aceita dois parâmetros
     * numéricos:
     * </p>
     * <ul>
     * <li><code>{0}</code>: o nome da classe de teste</li>
     * <li><code>{1}</code>: o nome do método de teste</li>
     * </ul>
     * <p>
     * Exemplo:
     * </p>
     * 
     * <pre>
     *  {@literal @}ImprimirNomeTeste(formatoMensagem = "Classe {0} - Método {1}")
     * </pre>
     * 
     * @return o formato a ser utilizado
     */
    String formatoMensagem() default StringUtils.EMPTY;
}
