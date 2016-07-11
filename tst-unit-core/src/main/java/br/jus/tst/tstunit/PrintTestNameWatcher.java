package br.jus.tst.tstunit;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * {@link TestRule} que imprime o nome de cada caso de teste em um stream de saída.
 *
 * @author Thiago Colbert
 * @since 08/12/2014
 */
public final class PrintTestNameWatcher implements TestRule {

    /**
     * Formato padrão para mensagens exibindo o nome do teste.
     */
    public static final String FORMATO_MENSAGEM_PADRAO = ">>>>>>>>>> Executando: {0}.{1} <<<<<<<<<<";

    private final PrintStream stream;
    private final String formatoMensagem;

    /**
     * 
     * @param formatoMensagem
     * @param stream
     * @throws NullPointerException
     *             caso {@code stream} seja {@code null}
     */
    public PrintTestNameWatcher(String formatoMensagem, PrintStream stream) {
        Objects.requireNonNull(stream, "stream");
        this.formatoMensagem = StringUtils.defaultIfBlank(formatoMensagem, FORMATO_MENSAGEM_PADRAO);
        this.stream = stream;
    }

    public PrintTestNameWatcher(String formatoMensagem) {
        this(formatoMensagem, System.out);
    }

    public PrintTestNameWatcher() {
        this(FORMATO_MENSAGEM_PADRAO);
    }

    @Override
    public Statement apply(Statement statement, Description description) {
        stream.println();
        stream.println(MessageFormat.format(formatoMensagem, description.getClassName(), description.getMethodName()));
        stream.println();
        return statement;
    }
}
