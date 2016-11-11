package br.jus.tst.tstunit.jpa.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.slf4j.*;

import br.jus.tst.tstunit.TstUnitException;
import br.jus.tst.tstunit.jpa.*;

/**
 * Classe que permite a criação de instâncias de {@link GeradorSchema}.
 * 
 * @author Thiago Miranda
 * @since 11 de nov de 2016
 */
public class GeradorSchemaCreator implements Serializable {

    private static final long serialVersionUID = 4685033585780473988L;

    private static final Logger LOGGER = LoggerFactory.getLogger(JpaExtensao.class);

    /**
     * Cria uma nova instância de {@link GeradorSchema}.
     * 
     * @param classe
     *            a classe a ser instanciada
     * @param propriedadesAdicionais
     *            propriedades a serem repassadas para a instância criada (pode estar vazio, mas não {@code null})
     * @return a instância criada
     * @throws NullPointerException
     *             caso qualquer parâmetro seja {@code null}
     * @throws TstUnitException
     *             caso ocorra algum erro ao criar a instância
     */
    public GeradorSchema criarGeradorSchema(Class<? extends GeradorSchema> classe, Map<String, String> propriedadesAdicionais) throws TstUnitException {
        GeradorSchema instanciaGeradorSchema;

        LOGGER.debug("Criando instância do gerador de schema configurado: {}", classe);
        try {
            instanciaGeradorSchema = Objects.requireNonNull(classe, "classe").getConstructor(Map.class)
                    .newInstance(Objects.requireNonNull(propriedadesAdicionais, "propriedadesAdicionais"));
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException exception) {
            throw new TstUnitException("Erro ao instanciar gerador de schema: " + classe, exception);
        }

        return instanciaGeradorSchema;
    }
}
