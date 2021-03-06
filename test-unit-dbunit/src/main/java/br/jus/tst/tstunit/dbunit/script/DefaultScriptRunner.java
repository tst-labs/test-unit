package br.jus.tst.tstunit.dbunit.script;

/*
 * Slightly modified version of the com.ibatis.common.jdbc.ScriptRunner class
 * from the iBATIS Apache project. Only removed dependency on Resource class
 * and a constructor
 * GPSHansl, 06.08.2015: regex for delimiter, rearrange comment/delimiter detection, remove some ide warnings.
 */

/*
 *  Copyright 2004 Clinton Begin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.regex.*;

import org.slf4j.*;

/**
 * Implementação simples de {@link ScriptRunner}, independente de banco de dados utilizado.
 *
 * @author BenoitDuffez
 * @author Thiago Miranda
 * 
 * @see <a href="https://github.com/BenoitDuffez/DefaultScriptRunner">Original</a>
 */
public class DefaultScriptRunner implements ScriptRunner {

    /**
     * Regex to detect delimiter. ignores spaces, allows delimiter in comment, allows an equals-sign
     */
    public static final Pattern DELIMITER_PATTERN;

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultScriptRunner.class);
    private static final String DEFAULT_DELIMITER;
    private static final String MESSAGE_SEPARATOR = "\n-------\n";

    private static File logsDir;

    static {
        DELIMITER_PATTERN = Pattern.compile("^\\s*(--)?\\s*delimiter\\s*=?\\s*([^\\s]+)+\\s*.*$", Pattern.CASE_INSENSITIVE);
        DEFAULT_DELIMITER = ";";

        logsDir = new File(".");
    }

    private final Connection connection;

    private final boolean stopOnError;
    private final boolean autoCommit;

    private PrintWriter logWriter;
    private PrintWriter errorLogWriter;

    private String delimiter = DEFAULT_DELIMITER;
    private boolean fullLineDelimiter;

    /**
     * Cria uma nova instância do executor de scripts.
     * 
     * @param connection
     *            a conexão JDBC a ser utilizada
     * @param autoCommit
     *            define se o modo <a href="https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html#disable_auto_commit">auto-commit</a> da
     *            conexão está habilitado ou não
     * @param stopOnError
     *            define se o script deve parar quando ocorrer algum erro SQL
     */
    public DefaultScriptRunner(Connection connection, boolean autoCommit, boolean stopOnError) {
        this.connection = connection;
        this.autoCommit = autoCommit;
        this.stopOnError = stopOnError;
        File logFile = new File(logsDir, "create_db.log");
        File errorLogFile = new File(logsDir, "create_db_error.log");
        try {
            if (logFile.exists()) {
                logWriter = new PrintWriter(new FileWriter(logFile, true));
            } else {
                logWriter = new PrintWriter(new FileWriter(logFile, false));
            }
        } catch (IOException e) {
            LOGGER.error("Unable to access or create the db_create log", e);
        }
        try {
            if (logFile.exists()) {
                errorLogWriter = new PrintWriter(new FileWriter(errorLogFile, true));
            } else {
                errorLogWriter = new PrintWriter(new FileWriter(errorLogFile, false));
            }
        } catch (IOException e) {
            LOGGER.error("Unable to access or create the  db_create error log", e);
        }
        String timeStamp = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss").format(new java.util.Date());
        println(MESSAGE_SEPARATOR + timeStamp + MESSAGE_SEPARATOR);
        printlnError(MESSAGE_SEPARATOR + timeStamp + MESSAGE_SEPARATOR);
    }

    public static File getLogsDir() {
        return logsDir;
    }

    public static void setLogsDir(File logsDir) {
        LOGGER.debug("Diretório de logs: {}", logsDir);
        DefaultScriptRunner.logsDir = logsDir;
    }

    public void setDelimiter(String delimiter, boolean fullLineDelimiter) {
        this.delimiter = delimiter;
        this.fullLineDelimiter = fullLineDelimiter;
    }

    /**
     * Setter for logWriter property
     *
     * @param logWriter
     *            - the new value of the logWriter property
     */
    public void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    /**
     * Setter for errorLogWriter property
     *
     * @param errorLogWriter
     *            - the new value of the errorLogWriter property
     */
    public void setErrorLogWriter(PrintWriter errorLogWriter) {
        this.errorLogWriter = errorLogWriter;
    }

    @Override
    public void runScript(Reader reader) throws IOException, SQLException {
        boolean originalAutoCommit = connection.getAutoCommit();
        try {
            if (originalAutoCommit != autoCommit) {
                connection.setAutoCommit(autoCommit);
            }

            runScript(connection, reader);
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    private void runScript(Connection conn, Reader reader) throws IOException, SQLException {
        StringBuffer command = null;
        try {
            LineNumberReader lineReader = new LineNumberReader(reader);
            String line;
            while ((line = lineReader.readLine()) != null) {
                if (command == null) {
                    command = new StringBuffer();
                }
                String trimmedLine = line.trim();
                final Matcher delimMatch = DELIMITER_PATTERN.matcher(trimmedLine);
                if (trimmedLine.length() < 1 || trimmedLine.startsWith("//")) {
                    // Do nothing
                } else if (delimMatch.matches()) {
                    setDelimiter(delimMatch.group(2), false);
                } else if (trimmedLine.startsWith("--")) {
                    println(trimmedLine);
                } else if (trimmedLine.length() < 1 || trimmedLine.startsWith("--")) {
                    // Do nothing
                } else if (!fullLineDelimiter && trimmedLine.endsWith(getDelimiter()) || fullLineDelimiter && trimmedLine.equals(getDelimiter())) {
                    command.append(line.substring(0, line.lastIndexOf(getDelimiter())));
                    command.append(" ");
                    execCommand(conn, command, lineReader);
                    command = null;
                } else {
                    command.append(line);
                    command.append("\n");
                }
            }
            if (command != null) {
                execCommand(conn, command, lineReader);
            }
            if (!autoCommit) {
                conn.commit();
            }
        } catch (IOException e) {
            throw new IOException(String.format("Error executing '%s': %s", command, e.getMessage()), e);
        } finally {
            conn.rollback();
            flush();
        }
    }

    private void execCommand(Connection conn, StringBuffer command, LineNumberReader lineReader) throws SQLException {
        Statement statement = conn.createStatement();

        println(command);

        boolean hasResults = false;
        try {
            hasResults = statement.execute(command.toString());
        } catch (SQLException e) {
            final String errText = String.format("Error executing '%s' (line %d): %s", command, lineReader.getLineNumber(), e.getMessage());
            printlnError(errText);
            LOGGER.error(errText);
            if (stopOnError) {
                throw new SQLException(errText, e);
            }
        }

        if (autoCommit && !conn.getAutoCommit()) {
            conn.commit();
        }

        ResultSet rs = statement.getResultSet();
        if (hasResults) {
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            for (int i = 1; i <= cols; i++) {
                String name = md.getColumnLabel(i);
                print(name + "\t");
            }
            println("");
            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    String value = rs.getString(i);
                    print(value + "\t");
                }
                println("");
            }
        }

        try {
            statement.close();
        } catch (SQLException e) {
            // Ignore to workaround a bug in Jakarta DBCP
            LOGGER.debug("Error closing the Statement", e);
        }
    }

    private String getDelimiter() {
        return delimiter;
    }

    private void print(Object o) {
        if (logWriter != null) {
            logWriter.print(o);
        }
    }

    private void println(Object o) {
        if (logWriter != null) {
            logWriter.println(o);
        }
    }

    private void printlnError(Object o) {
        if (errorLogWriter != null) {
            errorLogWriter.println(o);
        }
    }

    private void flush() {
        if (logWriter != null) {
            logWriter.flush();
        }
        if (errorLogWriter != null) {
            errorLogWriter.flush();
        }
    }
}
