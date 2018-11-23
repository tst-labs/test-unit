package br.jus.tst.tstunit.time;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.builder.*;
import org.apache.commons.lang3.time.StopWatch;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi.FColor;

import br.jus.tst.tstunit.*;

/**
 * Permite que seja medido o tempo de execução de uma operação.
 * 
 * @author Thiago Miranda
 * @since 30 de jun de 2017
 */
public final class MedidorTempoExecucao implements Serializable {

    private static final long serialVersionUID = 3431653094150490207L;

    public static final String FORMATO_MENSAGENS_PADRAO = "\n[TST UNIT - MEDIDOR] %s levou %d milisegundos\n";

    private static final MedidorTempoExecucao INSTANCIA_SINGLETON = new MedidorTempoExecucao();

    private boolean habilitado;
    private String formatoMensagens;

    private transient long duracaoAlerta;
    private transient long duracaoPerigo;
    private transient boolean consoleAnsi;

    private MedidorTempoExecucao() {
    }

    /**
     * Obtém a instância única (<em>singleton</em>).
     * 
     * @return a instância
     */
    public static MedidorTempoExecucao getInstancia() {
        return INSTANCIA_SINGLETON;
    }

    /**
     * Configura a instância de acordo com as definições informadas.
     * 
     * @param configuracao
     *            definições de configuração a serem utilizadas
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    public void configurar(Configuracao configuracao) {
        Objects.requireNonNull(configuracao, "configuracao");
        Properties propriedades = configuracao.isCarregado() ? configuracao.getSubPropriedades("core.medidortempoexecucao") : new Properties();

        habilitado = BooleanUtils.toBoolean(propriedades.getProperty("habilitado"));
        formatoMensagens = Optional.ofNullable(propriedades.getProperty("mensagens.formato")).orElse(MedidorTempoExecucao.FORMATO_MENSAGENS_PADRAO);
        duracaoAlerta = Long.parseLong(Optional.ofNullable(propriedades.getProperty("duracao.alerta")).orElse("500"));
        duracaoPerigo = Long.parseLong(Optional.ofNullable(propriedades.getProperty("duracao.perigo")).orElse("1000"));
        consoleAnsi = BooleanUtils.toBoolean(Optional.ofNullable(propriedades.getProperty("consoleansi")).orElse("true"));
    }

    /**
     * Mede o tempo de execução de uma operação representada por um {@link Runnable}.
     * 
     * @param runnable
     *            a operação a ser medida
     * @param descricao
     *            descrição da operação, a ser utilizada nos logs de execução
     * @throws NullPointerException
     *             caso seja informado {@code null} como operação
     * @throws RuntimeException
     *             caso a operação lance uma exceção
     */
    public void medir(Runnable runnable, String descricao) {
        Objects.requireNonNull(runnable, "runnable");
        if (habilitado) {
            StopWatch watch = new StopWatch();
            watch.start();
            runnable.run();
            watch.stop();
            log(descricao, watch.getTime());
        } else {
            runnable.run();
        }
    }

    /**
     * Mede o tempo de execução de uma operação representada por um {@link Callable}.
     * 
     * @param callable
     *            a operação a ser medida
     * @param descricao
     *            descrição da operação, a ser utilizada nos logs de execução
     * @return o resultado da operação
     * @throws NullPointerException
     *             caso seja informado {@code null} como operação
     * @throws TstUnitRuntimeException
     *             caso a operação lance uma exceção e o Medidor esteja habilitado
     * @throws Exception
     *             caso a operação lance uma exceção e o Medidor não esteja habilitado
     * @param <T>
     *            o tipo de objeto retornado pela operação informada
     */
    public <T> T medir(Callable<T> callable, String descricao) throws Exception { // NOSONAR
        Objects.requireNonNull(callable, "callable");

        T resultado;

        if (habilitado) {
            StopWatch watch = new StopWatch();
            watch.start();
            resultado = getResultado(callable);
            watch.stop();
            log(descricao, watch.getTime());
        } else {
            resultado = getResultado(callable);

        }

        return resultado;
    }

    private <T> T getResultado(Callable<T> callable) throws Exception { // NOSONAR
        T resultado;

        try {
            resultado = callable.call();
        } catch (Exception exception) { // NOSONAR
            throw habilitado ? new TstUnitRuntimeException("Erro ao computar resultado da chamada", exception) : exception; // NOSONAR
        }

        return resultado;
    }

    /**
     * Mede o tempo de execução de uma operação representada por uma {@link Function}.
     * 
     * @param function
     *            a operação a ser medida
     * @param descricao
     *            descrição da operação, a ser utilizada nos logs de execução
     * @return uma nova função representando a execução deste método
     * @throws NullPointerException
     *             caso seja informado {@code null} como operação
     * @throws RuntimeException
     *             caso a operação lance uma exceção
     * @param <T>
     *            tipo de dado de entrada da função informada
     * @param <R>
     *            tipo de dado de saída da função informada
     */
    public <T, R> Function<T, R> medir(Function<T, R> function, String descricao) {
        Objects.requireNonNull(function, "function");

        Function<T, R> newFunction;
        if (habilitado) {
            newFunction = (t) -> {
                StopWatch watch = new StopWatch();
                watch.start();
                R resultado = function.apply(t);
                watch.stop();
                log(descricao, watch.getTime());
                return resultado;
            };
        } else {
            newFunction = function::apply;
        }
        return newFunction;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public String getFormatoMensagens() {
        return formatoMensagens;
    }

    public void setFormatoMensagens(String formatoMensagens) {
        this.formatoMensagens = formatoMensagens;
    }

    private void log(String descricao, long tempo) {
        if (tempo > 0L) {
            String mensagem = String.format(formatoMensagens, descricao, tempo);

            if (consoleAnsi) {
                FColor foregroundColor = identificarCorPeloTempo(tempo);
                ColoredPrinter printer = new ColoredPrinter.Builder(1, false).foreground(foregroundColor).build();
                printer.println(mensagem);
                printer.clear();
            } else {
                System.out.println(mensagem);
            }
        }
    }

    private FColor identificarCorPeloTempo(long tempo) {
        return tempo >= duracaoPerigo ? FColor.RED : tempo >= duracaoAlerta ? FColor.YELLOW : FColor.GREEN;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("habilitado", habilitado).append("formatoMensagens", formatoMensagens)
                .append("duracaoAlerta", duracaoAlerta).append("duracaoPerigo", duracaoPerigo).append("consoleAnsi", consoleAnsi).toString();
    }
}
