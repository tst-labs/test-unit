package br.jus.tst.tstunit.jpa.cdi;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.*;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.CDI;
import javax.persistence.*;

import org.slf4j.*;

import br.jus.tst.tstunit.jpa.*;
import br.jus.tst.tstunit.jpa.HabilitarJpa.UnidadePersistencia;
import br.jus.tst.tstunit.jpa.annotation.*;

/**
 * Implementação de {@link GeradorSchema} que delega a geração para o {@link EntityManagerFactory} obtido através do contêiner CDI. Assume-se que o JPA esteja
 * configurado para gerar o <em>schema</em> automaticamente.
 * 
 * @author Thiago Miranda
 * @since 6 de jul de 2016
 */
public class GeradorSchemaCdi implements Serializable, GeradorSchema {

    private static final long serialVersionUID = -7371478997025466447L;

    private static final Logger LOGGER = LoggerFactory.getLogger(GeradorSchemaCdi.class);

    private final Map<String, String> propriedadesAdicionais;

    /**
     * Cria uma nova instância com as propriedades adicionais a serem repassadas para o framework ORM.
     * 
     * @param propriedadesAdicionais
     *            a serem repassadas ao framework ORM (pode estar vazio)
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    public GeradorSchemaCdi(Map<String, String> propriedadesAdicionais) {
        this.propriedadesAdicionais = new HashMap<>(Objects.requireNonNull(propriedadesAdicionais, "propriedadesAdicionais"));
    }

    /**
     * @throws JpaException
     *             caso ocorra algum erro ao executar a operação
     */
    @Override
    public void criar() {
        try {
            UnidadePersistencia[] unidadesPersistencia = EntityManagerFactoryProducerExtension.getUnidadesPersistencia();
            LOGGER.debug("Unidades de persistência: {}", (Object[]) unidadesPersistencia);

            Arrays.stream(unidadesPersistencia).forEach(unidade -> {
                LOGGER.debug("Obtendo EntityManager da unidade de persistência: {}", unidade);
                recuperarEntityManagerDoCdi(unidade).clear();
            });

        } catch (PersistenceException exception) {
            throw new JpaException("Erro ao obter instância de EntityManager do contexto CDI", exception);
        }
    }

    private EntityManager recuperarEntityManagerDoCdi(UnidadePersistencia unidade) {
        return CDI.current().select(EntityManager.class, getQualifierAnnotation(unidade)).get();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Annotation getQualifierAnnotation(UnidadePersistencia unidade) {
        Class<? extends Annotation> qualifierClass = unidade.qualifierClass();

        if (qualifierClass == Unico.class) {
            // substitui o qualifier para funcionar dentro do CDI
            qualifierClass = Default.class;
        }

        return new AnnotationProxy(new AnnotationDescriptor(qualifierClass));
    }

    @Override
    public void destruir() {
    }
    
    @Override
    public Map<String, String> getPropriedadesAdicionais() {
        return Collections.unmodifiableMap(propriedadesAdicionais);
    }
}
