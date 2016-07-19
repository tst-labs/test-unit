package br.jus.tst.tstunit;

import java.util.*;

import org.junit.rules.TestRule;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.*;
import org.slf4j.*;

/**
 * <p>
 * <code>{@literal @}TstUnitRunner</code> é um {@link Runner} do JUnit que facilita o uso de vários recursos em classes de testes.
 * </p>
 * 
 * <p>
 * O uso mais comum, sem habilitar nenhum recurso adicionais, é conforme o exemplo abaixo:
 * 
 * <pre>
 * <code>
 * &#064;RunWith(TstUnitRunner.class) // Roda os testes com o TST Unit
 * class MeuTeste {
 *   ... // Código do teste
 * }</code>
 * </pre>
 * </p>
 * 
 * @author Thiago Miranda
 * @since 1 de jul de 2016
 */
public class TstUnitRunner extends BlockJUnit4ClassRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TstUnitRunner.class);
    private static final String PACOTE_EXTENSOES = "br.jus.tst.tstunit";

    private transient final Class<?> classeTeste;
    private transient final Configuracao configuracao;
    private transient final List<? extends Extensao<?>> extensoes;

    /**
     * Cria uma nova instância do <em>runner</em> para rodar sobre a classe informada.
     * 
     * @param classeTeste
     *            classe de teste
     * @throws InitializationError
     *             caso ocorra algum erro ao inicializar o <em>runner</em>
     * @throws TstUnitException
     *             caso ocorra algum erro ao carregar as configurações
     */
    public TstUnitRunner(Class<?> classeTeste) throws InitializationError, TstUnitException {
        super(classeTeste);
        this.classeTeste = classeTeste;
        configuracao = new Configuracao();

        extensoes = new ExtensoesLoader(PACOTE_EXTENSOES, classeTeste).carregarExtensoes();
        LOGGER.info("Extensões habilitadas: {}", extensoes);
    }

    /**
     * Construtor utilizado nos testes desta classe.
     * 
     * @param configuracao
     * @param extensoesLoader
     * @throws InitializationError
     */
    TstUnitRunner(Configuracao configuracao, ExtensoesLoader extensoesLoader) throws InitializationError {
        super(extensoesLoader.getClasseTeste());
        this.configuracao = configuracao;
        this.classeTeste = extensoesLoader.getClasseTeste();
        extensoes = extensoesLoader.carregarExtensoes();
        LOGGER.info("Extensões habilitadas: {}", extensoes);
    }

    /**
     * @throws TstUnitRuntimeException
     *             caso ocorra algum erro ao inicializar os testes
     */
    @Override
    public void run(RunNotifier notifier) {
        LOGGER.debug("Inicializando {} extensões", extensoes.size());
        extensoes.forEach(extensao -> {
            try {
                extensao.inicializar(configuracao, notifier);
            } catch (TstUnitException exception) {
                throw new TstUnitRuntimeException("Erro ao inicializar extensão: " + extensao, exception);
            }
        });

        super.run(notifier);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Object createTest() throws Exception {
        // FIXME Supõe que exista apenas uma extensão que defina esse método
        Optional instanciaPersonalizada = extensoes.stream().map(extensao -> extensao.getInstanciaPersonalizadaParaTestes()).filter(Optional::isPresent)
                .findFirst();
        Object instanciaTeste = ((Optional<Object>) instanciaPersonalizada.orElse(Optional.of(super.createTest()))).get();
        LOGGER.debug("Instância da classe de testes utilizada: {}", instanciaTeste);

        LOGGER.debug("Executando beforeTestes() de cada extensão antes de executar os testes");
        extensoes.forEach(extensao -> extensao.beforeTestes(instanciaTeste));
        return instanciaTeste;
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        final Statement statement = super.classBlock(notifier);

        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                statement.evaluate();
                LOGGER.debug("Executando afterTestes() de cada extensão após finalizados todos os testes");
                extensoes.forEach(extensao -> extensao.afterTestes());
            }
        };
    }

    @Override
    protected Statement methodBlock(FrameworkMethod method) {
        Statement statement = super.methodBlock(method);
        for (Extensao<?> extensao : extensoes) {
            LOGGER.debug("Executando extensão: {}", extensao.getClass().getSimpleName());
            try {
                statement = extensao.criarStatement(statement, method);
            } catch (TstUnitException exception) {
                throw new TstUnitRuntimeException("Erro ao executar método: " + method, exception);
            }
        }
        return statement;
    }

    @Override
    protected List<TestRule> getTestRules(Object target) {
        List<TestRule> rules = super.getTestRules(target);

        try {
            configuracao.carregar();
        } catch (TstUnitException exception) {
            LOGGER.debug("Erro ao carregar propriedades", exception);
        }

        ImprimirNomeTeste imprimirNomeTeste = classeTeste.getAnnotation(ImprimirNomeTeste.class);
        if (imprimirNomeTeste == null && configuracao.getPropriedadeBoolean("core.printtestname.default").orElse(Boolean.TRUE)) {
            LOGGER.debug("Anotação @ImprimirNomeTeste ausente - utilizando o valor da configuração padrão, que é 'true'");
            rules.add(new PrintTestNameWatcher());
        } else if (imprimirNomeTeste != null && imprimirNomeTeste.value()) {
            LOGGER.debug("Anotação @ImprimirNomeTeste presente e habilitada");
            rules.add(new PrintTestNameWatcher(imprimirNomeTeste.formatoMensagem()));
        }

        return rules;
    }
}
