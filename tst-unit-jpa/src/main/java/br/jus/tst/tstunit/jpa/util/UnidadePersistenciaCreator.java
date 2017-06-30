package br.jus.tst.tstunit.jpa.util;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import org.apache.commons.lang3.builder.*;

import br.jus.tst.tstunit.jpa.HabilitarJpa.UnidadePersistencia;

/**
 * Classe que permite a criação de {@link UnidadePersistencia}.
 * 
 * @author Thiago Miranda
 * @since 11 de nov de 2016
 */
public class UnidadePersistenciaCreator implements Serializable {

    private static final long serialVersionUID = 4058213719115710830L;

    /**
     * Cria uma nova unidade de persistência com o nome informado.
     * 
     * @param nomeUnidadePersistencia
     *            o nome da unidade de persistência
     * @param qualifierClass
     *            classe do qualificador, utilizado para diferenciar as unidades de persistência entre si
     * @return a unidade de persistência criada
     */
    public UnidadePersistencia[] criarAnotacaoUnidadePersistencia(String nomeUnidadePersistencia, Class<? extends Annotation> qualifierClass) {
        return new UnidadePersistencia[] { new UnidadePersistencia() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return UnidadePersistencia.class;
            }

            @Override
            public Class<? extends Annotation> qualifierClass() {
                return qualifierClass;
            }

            @Override
            public String nome() {
                return nomeUnidadePersistencia;
            }

            @Override
            public String toString() {
                return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("annotationType", annotationType()).append("qualifierClass", qualifierClass())
                        .append("nome", nome()).toString();
            }
        } };
    }
}
