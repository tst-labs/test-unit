package br.jus.tst.tstunit;

import java.io.*;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.*;
import org.slf4j.*;

/**
 * Classe que fornece acesso a todas as configurações da biblioteca, conforme presentes em um arquivo {@link #getNomeArquivoPropriedades()} que esteja no
 * <em>classpath</em>.
 * 
 * @author Thiago Miranda
 * @since 4 de jul de 2016
 */
public final class Configuracao implements Serializable {

    private static final long serialVersionUID = 3577294568759890149L;
    private static final Logger LOGGER = LoggerFactory.getLogger(Configuracao.class);

    private static final String NOME_PADRAO_ARQUIVO_PROPRIEDADES = "testunit.properties";

    private static Configuracao singleton;

    private String nomeArquivoPropriedades;
    
    private transient Properties properties;

    private Configuracao() {
        nomeArquivoPropriedades = NOME_PADRAO_ARQUIVO_PROPRIEDADES;
    }

    /**
     * Obtém a instância única da classe.
     * 
     * @return a instância única
     */
    public static Configuracao getInstance() {
        if (singleton == null) {
            singleton = new Configuracao();
        }

        return singleton;
    }

    public String getNomeArquivoPropriedades() {
        return nomeArquivoPropriedades;
    }

    /**
     * Define o nome do arquivo o qual será utilizado para carregar as propriedades.
     * 
     * @param nomeArquivoPropriedades
     *            o nome do arquivo de propridades
     * @return {@code this} para chamadas encadeadas de método
     */
    public Configuracao setNomeArquivoPropriedades(String nomeArquivoPropriedades) {
        this.nomeArquivoPropriedades = nomeArquivoPropriedades;
        return this;
    }

    /**
     * Carrega as configurações a partir do arquivo.
     * 
     * @return {@code this} para chamadas encadeadas de método
     * @throws TestUnitException
     *             caso ocorra algum erro ao carregar as configurações
     */
    public Configuracao carregar() throws TestUnitException {
        LOGGER.debug("Carregando propriedades a partir do arquivo: {}", nomeArquivoPropriedades);

        Optional<InputStream> propertiesStreamOptional = Optional.ofNullable(Thread.currentThread().getContextClassLoader().getResourceAsStream(nomeArquivoPropriedades));
        InputStream propertiesStream = propertiesStreamOptional
                .orElseThrow(() -> new TestUnitException("Nenhum arquivo " + nomeArquivoPropriedades + " encontrado no classpath."));

        properties = new Properties();
        try {
            properties.load(propertiesStream);
            LOGGER.trace("Propriedades: {}", properties);
        } catch (IOException exception) {
            throw new TestUnitException("Erro ao carregar arquivo de propriedades", exception);
        } finally {
            IOUtils.closeQuietly(propertiesStream);
        }

        return this;
    }

    /**
     * Obtém o valor de uma propriedade booleana.
     * 
     * @param key
     *            chave da propriedade desejada
     * @return o valor da propriedade
     */
    public Optional<Boolean> getPropriedadeBoolean(String key) {
        String value = isCarregado() ? properties.getProperty(key) : null;
        return StringUtils.isEmpty(value) ? Optional.empty() : Optional.of(BooleanUtils.toBoolean(value));
    }

    /**
     * Obtém um subconjunto de propriedades. É utilizado um prefixo para selecionar quais propriedades serão consideradas. Caso seja informado {@code null} ou
     * uma String vazia, o retorno será as propriedades originais.
     * 
     * @param prefixo
     *            prefixo das propriedades desejadas
     * @return as propriedades com o prefixo informado, mas sem esse prefixo
     * @throws IllegalStateException
     *             caso as propriedades não tenham sido carregadas ainda
     */
    public Properties getSubPropriedades(String prefixo) {
        Validate.validState(isCarregado(), "Ainda não foram carregadas as propriedades do arquivo %s", nomeArquivoPropriedades);

        Properties subProperties;
        if (StringUtils.isNotBlank(prefixo)) {
            subProperties = new Properties();
            properties.keySet().stream().map(keyObject -> keyObject.toString()).filter(key -> key.startsWith(prefixo))
                    .forEach(key -> subProperties.setProperty(StringUtils.removeStart(key, prefixo + '.'), properties.getProperty(key)));
        } else {
            subProperties = this.properties;
        }
        return subProperties;
    }

    /**
     * Verifica se as propriedades já foram carregadas.
     * 
     * @return {@code true}/{@code false}
     */
    public boolean isCarregado() {
        return properties != null;
    }
}
