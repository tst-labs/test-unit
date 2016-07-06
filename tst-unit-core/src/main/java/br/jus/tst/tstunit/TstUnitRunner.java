package br.jus.tst.tstunit;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.*;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.*;
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

    private transient final Class<?> classeTeste;
    private transient final Configuracao configuracao;

    private List<? extends Extensao> extensoes;

    /**
     * Cria uma nova instância do <em>runner</em> para rodar sobre a classe informada.
     * 
     * @param classeTeste
     *            classe de teste
     * @throws InitializationError
     *             caso ocorra algum erro ao inicializar o <em>runner</em>
     * @throws TstUnitException
     */
    public TstUnitRunner(Class<?> classeTeste) throws InitializationError, TstUnitException {
        super(classeTeste);
        this.classeTeste = classeTeste;
        configuracao = new Configuracao();

        Reflections reflections = new Reflections(new ConfigurationBuilder().filterInputsBy(new FilterBuilder().includePackage("br.jus.tst.tstunit"))
                .setUrls(ClasspathHelper.forPackage("br.jus.tst.tstunit")).setScanners(new SubTypesScanner()));

        Set<Class<? extends Extensao>> classesExtensoes = reflections.getSubTypesOf(Extensao.class);
        extensoes = classesExtensoes.stream().map(this::newInstance).filter(extensao -> extensao.isHabilitada(classeTeste)).collect(Collectors.toList());
    }

    private <T extends Extensao> T newInstance(Class<T> classeExtensao) {
        try {
            return classeExtensao.newInstance();
        } catch (InstantiationException | IllegalAccessException exception) {
            throw new RuntimeException("Erro ao instanciar classe da extensão: " + classeExtensao, exception);
        }
    }

    @Override
    public void run(RunNotifier notifier) {
        extensoes.forEach(extensao -> extensao.inicializar(classeTeste, configuracao, notifier));
        super.run(notifier);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Object createTest() throws Exception {
        // FIXME Supõe que exista apenas uma extensão que defina esse método
        Optional instanciaPersonalizada = extensoes.stream().map(extensao -> extensao.getInstanciaPersonalizadaParaTestes(classeTeste))
                .filter(Optional::isPresent).findFirst();
        Object instanciaTeste = ((Optional<Object>) instanciaPersonalizada.orElse(Optional.of(super.createTest()))).get();
        extensoes.forEach(extensao -> extensao.beforeTestes(instanciaTeste));
        return instanciaTeste;
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        Statement statement = super.classBlock(notifier);

        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                statement.evaluate();
                extensoes.forEach(extensao -> extensao.afterTestes(classeTeste));
            }
        };
    }
    
    @Override
    protected Statement methodBlock(FrameworkMethod method) {
        Statement statement = super.methodBlock(method);
        for (Extensao extensao : extensoes) {
            try {
                statement = extensao.criarStatement(classeTeste, statement, method);
            } catch (TstUnitException exception) {
                throw new RuntimeException("Erro ao executar método: " + method, exception);
            }
        }
        return statement;
    }
}
