package br.jus.tst.tstunit;

import java.io.*;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO Javadoc
 * 
 * @author Thiago Miranda
 * @since 4 de jul de 2016
 */
public class Configuracao implements Serializable {

    private static final String NOME_ARQUIVO_PROPRIEDADES = "tstunit.properties";

    private static final long serialVersionUID = 3577294568759890149L;

    private Properties properties;

    /**
     * TODO Javadoc
     * 
     * @throws TstUnitException
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
     * 
     * @param prefixo
     * @return
     */
    public Properties getSubPropriedades(String prefixo) {
        Properties jdbcProperties = new Properties();

        properties.keySet().stream().map(keyObject -> keyObject.toString()).filter(key -> key.startsWith(prefixo))
                .forEach(key -> jdbcProperties.setProperty(StringUtils.removeStart(key, prefixo + '.'), properties.getProperty(key)));
        return jdbcProperties;
    }
}
