package br.jus.tst.tstunit;

import java.io.*;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Classe que fornece acesso a todas as configurações da biblioteca, conforme presentes em um arquivo {@value #NOME_ARQUIVO_PROPRIEDADES} que esteja no
 * <em>classpath</em>.
 * 
 * @author Thiago Miranda
 * @since 4 de jul de 2016
 */
public class Configuracao implements Serializable {

    private static final String NOME_ARQUIVO_PROPRIEDADES = "tstunit.properties";

    private static final long serialVersionUID = 3577294568759890149L;

    private transient final Properties properties;

    /**
     * Cria uma nova instância das configurações.
     * 
     * @throws TstUnitException
     *             caso ocorra algum erro ao carregar as configurações
     */
    public Configuracao() throws TstUnitException {
        Optional<InputStream> propertiesStreamOptional = Optional
                .ofNullable(Thread.currentThread().getContextClassLoader().getResourceAsStream(NOME_ARQUIVO_PROPRIEDADES));
        InputStream propertiesStream = propertiesStreamOptional
                .orElseThrow(() -> new TstUnitException("Nenhum arquivo " + NOME_ARQUIVO_PROPRIEDADES + " encontrado no classpath."));

        properties = new Properties();
        try {
            properties.load(propertiesStream);
        } catch (IOException exception) {
            throw new TstUnitException("Erro ao carregar arquivo de propriedades", exception);
        } finally {
            IOUtils.closeQuietly(propertiesStream);
        }
    }

    /**
     * Obtém um subconjunto de propriedades. É utilizado um prefixo para selecionar quais propriedades serão consideradas. Caso seja informado {@code null} ou
     * uma String vazia, o retorno será as propriedades originais.
     * 
     * @param prefixo
     *            prefixo das propriedades desejadas
     * @return as propriedades com o prefixo informado, mas sem esse prefixo
     */
    public Properties getSubPropriedades(String prefixo) {
        if (StringUtils.isNotBlank(prefixo)) {
            Properties jdbcProperties = new Properties();
            properties.keySet().stream().map(keyObject -> keyObject.toString()).filter(key -> key.startsWith(prefixo))
                    .forEach(key -> jdbcProperties.setProperty(StringUtils.removeStart(key, prefixo + '.'), properties.getProperty(key)));
            return jdbcProperties;
        } else {
            return this.properties;
        }
    }
}
