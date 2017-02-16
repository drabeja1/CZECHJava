package cz.fit.cvut.czehjava.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Jakub
 */
public class CzechJavaTests {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * Test funkcnosti kompilace a spusteni 
     * 
     * @throws IOException
     * @throws InterruptedException 
     */
    @Test
    public void testCzechJavaBubble() throws IOException, InterruptedException {
        StringBuilder expected = new StringBuilder();
        expected.append("5").append(LINE_SEPARATOR)
                .append("4").append(LINE_SEPARATOR)
                .append("3").append(LINE_SEPARATOR)
                .append("2").append(LINE_SEPARATOR)
                .append("1").append(LINE_SEPARATOR)
                .append("Serazeno:").append(LINE_SEPARATOR)
                .append("1").append(LINE_SEPARATOR)
                .append("2").append(LINE_SEPARATOR)
                .append("3").append(LINE_SEPARATOR)
                .append("4").append(LINE_SEPARATOR)
                .append("5").append(LINE_SEPARATOR);

        Process pCompile = Runtime.getRuntime().exec("java -jar target/czechjavac.jar -s examples/Bubble/ -t compiled/");
        pCompile.waitFor();

        Process pRun = Runtime.getRuntime().exec("java -jar target/czechjava.jar -c compiled/");
        pRun.waitFor();

        String output = readOutput(pRun.getInputStream());
        Assert.assertEquals(expected.toString(), output);
    }

    /**
     * Test na vyhozeni HeapOverflowExcetion
     * 
     * @throws IOException
     * @throws InterruptedException 
     */
    @Test
    public void testCzechJavaHeapOverflow() throws IOException, InterruptedException {
        Process pCompile = Runtime.getRuntime().exec("java -jar target/czechjavac.jar -s examples/HeapOverflow/ -t compiled/");
        pCompile.waitFor();

        Process pRun = Runtime.getRuntime().exec("java -jar target/czechjava.jar -c compiled/");
        pRun.waitFor();

        String output = readOutput(pRun.getErrorStream());
        Assert.assertTrue(output.contains("java.lang.RuntimeException: cz.fit.cvut.czechjava.interpreter.exceptions.HeapOverflowException"));
    }
    
    /**
     * Test na vyhozeni vyjimky pri preteceni cisla
     * 
     * @throws IOException 
     * @throws java.lang.InterruptedException 
     */
    @Test
    public void testCzecjJavaArithmeticException() throws IOException, InterruptedException {
        Process pCompile = Runtime.getRuntime().exec("java -jar target/czechjavac.jar -s examples/Factorial/ -t compiled/");
        pCompile.waitFor();

        Process pRun = Runtime.getRuntime().exec("java -jar target/czechjava.jar -c compiled/ -a 15");
        pRun.waitFor();
        String output = readOutput(pRun.getErrorStream());
        Assert.assertTrue(output.contains("ArithmeticException"));
    }

    private String readOutput(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            while ((line = br.readLine()) != null) {
                sb.append(line).append(LINE_SEPARATOR);
            }
        }
        return sb.toString();
    }
}
