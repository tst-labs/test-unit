package br.jus.tst.tstunit.jpa;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

import javax.enterprise.inject.Default;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import br.jus.tst.tstunit.jpa.cdi.GeradorSchemaCdi;

/**
 * Habilita o JPA numa classe de teste.
 * 
 * @author Thiago Miranda
 * @since 1 de jul de 2016
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Inherited
@Documented
public @interface HabilitarJpa {

    /**
     * Anotação que define informações de uma unidade de persistência.
     * 
     * @author Thiago Miranda
     * @since 4 de out de 2016
     *
     */
    public @interface UnidadePersistencia {

        /**
         * O nome da unidade de persistência. Normalmente está definida em um arquivo {@code persistence.xml}.
         * 
         * @return o nome da unidade de persistência
         */
        String nome();

        /**
         * Qualificador utilizado para diferenciar a instância de {@link EntityManager} das demais instâncias.
         * 
         * @return o qualificador da unidade de persistência
         */
        Class<? extends Annotation> qualifierClass() default Default.class;
    }

    /**
     * O nome da unidade de persistência utilizada. Normalmente está definida em um arquivo {@code persistence.xml}. Essa propriedade não deve ser definida em
     * conjunto com {@link #unidadesPersistencia()}.
     * 
     * @return o nome da unidade de persistência
     */
    String nomeUnidadePersistencia() default StringUtils.EMPTY;

    /**
     * As unidades de persistência que são utilizadas no teste. Caso essa propriedade seja definida, o valor de {@link #nomeUnidadePersistencia()} será
     * ignorado.
     * 
     * @return informações das unidades de persistência
     */
    UnidadePersistencia[] unidadesPersistencia() default {};

    /**
     * Classe utilizada para gerar o <em>schema</em> de banco de dados.
     * 
     * @return a classe
     */
    Class<? extends GeradorSchema> geradorSchema() default GeradorSchemaCdi.class;
}
