package br.jus.tst.tstunit.dbunit.dataset;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

import org.apache.commons.lang3.StringUtils;
import org.dbunit.operation.DatabaseOperation;

/**
 * <p>
 * Indica que uma classe ou método de teste necessita do uso de um arquivo <em>DataSet</em> do <em>DBUnit</em>.
 * </p>
 * <p>
 * OBS.: Caso a anotação seja definida tanto numa classe de testes quanto em seus métodos, a ordem de execução será sempre 1º classe -&gt; 2º método.
 * </p>
 * 
 * @author Thiago Miranda
 * @since 4 de jul de 2016
 */
@Documented
@Inherited
@Repeatable(UsarDataSets.class)
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface UsarDataSet {

    /**
     * Enumeração das operações de banco de dados disponíveis.
     * 
     * @author Thiago Miranda
     * @since 5 de mai de 2017
     */
    public enum Operacao {

        /**
         * Utilizado para indicar que deve ser usada a operação padrão configurada globalmente.
         */
        DEFAULT {

            @Override
            public String getNome() {
                return StringUtils.EMPTY;
            }
        },

        /**
         * @see DatabaseOperation#NONE
         */
        NONE,

        /**
         * @see DatabaseOperation#UPDATE
         */
        UPDATE,

        /**
         * @see DatabaseOperation#INSERT
         */
        INSERT,

        /**
         * @see DatabaseOperation#REFRESH
         */
        REFRESH,

        /**
         * @see DatabaseOperation#DELETE
         */
        DELETE,

        /**
         * @see DatabaseOperation#DELETE_ALL
         */
        DELETE_ALL,

        /**
         * @see DatabaseOperation#TRUNCATE_TABLE
         */
        TRUNCATE_TABLE,

        /**
         * @see DatabaseOperation#CLEAN_INSERT
         */
        CLEAN_INSERT;

        /**
         * Obtém o nome da operação, o qual pode ser utilizado para detectar a respectiva {@link DatabaseOperation}.
         * 
         * @return o nome da operação
         */
        public String getNome() {
            return name();
        }
    }

    /**
     * <p>
     * O nome do arquivo de dado.
     * </p>
     * <p>
     * Exemplo:
     * </p>
     * 
     * <pre>
     * {@literal @}UsarDataSet("meu-dataset1.xml")
     * </pre>
     * 
     * @return o nome do arquivo
     */
    String value();

    /**
     * <p>
     * Sobrescreve a operação a ser executada antes do teste. O valor padrão indica que deve ser utilizada a operação configurada globalmente para todos os
     * testes.
     * </p>
     * <p>
     * Exemplo:
     * </p>
     * 
     * <pre>
     * {@literal @}UsarDataSet(value = "meu-dataset.xml", operacaoPreTestes = Operacao.CLEAN_INSERT)
     * </pre>
     * 
     * @return a operação a ser executada antes do teste
     * @see DatabaseOperation
     */
    Operacao operacaoPreTestes() default Operacao.DEFAULT;

    /**
     * <p>
     * Sobrescreve a operação a ser executada após o teste. O valor padrão indica que deve ser utilizada a operação configurada globalmente para todos os
     * testes.
     * </p>
     * <p>
     * Exemplo:
     * </p>
     * 
     * <pre>
     * {@literal @}UsarDataSet(value = "meu-dataset.xml", operacaoPosTestes = Operacao.DELETE_ALL)
     * </pre>
     * 
     * @return a operação a ser executada após o teste
     * @see DatabaseOperation
     */
    Operacao operacaoPosTestes() default Operacao.DEFAULT;
}
