package cz.fit.cvut.czechjava;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import cz.fit.cvut.czechjava.compiler.model.Classfile;
import cz.fit.cvut.czechjava.interpreter.CZECHJavaInterpreter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

/**
 * Runner of interpreter
 *
 * @author Jakub
 */
public class Interpreter {

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(Interpreter.class.getName());
    /**
     * Prazdne pole argumentu
     */
    private static final String[] EMPTY_ARGUMENTS = {};

    /**
     * Prepare arguments options
     *
     * @return
     */
    private static Options prepareOptions() {
        Option heapSizeOpt = new Option("h", "heap", true, "Velikost heap. (volitelne)");
        heapSizeOpt.setOptionalArg(true);
        Option framesOpt = new Option("f", "frame", true, "Pocet framu. (volitelne)");
        framesOpt.setOptionalArg(true);
        Option stackOpt = new Option("s", "stack", true, "Velikost stacku ve framu. (volitelne)");
        stackOpt.setOptionalArg(true);
        Option compiledOpt = new Option("c", "compiled", true, "Slozka se zkompilovanymi soubory.");
        compiledOpt.setRequired(true);
        Option argumentsOpt = new Option("a", "arguments", true, "Argumenty programu. (volitelne)");
        argumentsOpt.setRequired(false);

        Options options = new Options();
        options.addOption(heapSizeOpt);
        options.addOption(framesOpt);
        options.addOption(stackOpt);
        options.addOption(compiledOpt);
        options.addOption(argumentsOpt);
        
        return options;
    }

    public static void exec(String[] args) throws Exception {
        int heapSize = Globals.HEAP_SIZE_DEFAULS;
        int frameCount = Globals.FRAME_COUNT_DEFAULT;
        int stackSize = Globals.STACK_SIZE_DEFAULT;
        String directory = null;
        String[] arguments = null;

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        Options options = prepareOptions();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("heap")) {
                heapSize = Integer.parseInt(cmd.getOptionValue("heap"));
            }
            if (cmd.hasOption("frame")) {
                frameCount = Integer.parseInt(cmd.getOptionValue("frame"));
            }
            if (cmd.hasOption("stack")) {
                stackSize = Integer.parseInt(cmd.getOptionValue("stack"));
            }
            if (cmd.hasOption("arguments")) {
                arguments = cmd.getOptionValues("arguments");
            } else {
                arguments = EMPTY_ARGUMENTS;
            }
            directory = cmd.getOptionValue("compiled");
            
        } catch (NumberFormatException | ParseException ex) {
            LOGGER.fatal(ex);
            formatter.printHelp("czechjava", options);
            System.exit(0);
        }

        List<cz.fit.cvut.czechjava.compiler.model.Class> librariesList = loadLibraries();
        List<cz.fit.cvut.czechjava.compiler.model.Class> classList = loadClassfiles(directory);
        classList.addAll(librariesList);

        CZECHJavaInterpreter interpreter = new CZECHJavaInterpreter(classList, heapSize,
                frameCount, stackSize, Arrays.asList(arguments));
        CallTarget target = Truffle.getRuntime().createCallTarget(interpreter);
        target.call();
    }

    public static List<cz.fit.cvut.czechjava.compiler.model.Class> loadLibraries() throws IOException {
        return loadClassfiles(Globals.COMPILED_LIBRARIES_DIRECTORY);
    }

    public static List<cz.fit.cvut.czechjava.compiler.model.Class> loadClassfiles(String directoryName) throws IOException {
        File directory = new File(directoryName);
        List<cz.fit.cvut.czechjava.compiler.model.Class> classList = new ArrayList<>();

        if (directory.isDirectory()) {
            File[] dirFiles = directory.listFiles();
            for (File dirFile : dirFiles) {
                String extension = "";
                int i = dirFile.getName().lastIndexOf('.');
                
                if (i > 0) {
                    extension = dirFile.getName().substring(i + 1);
                }
                if (extension.equals(Globals.CLASS_TYPE_EXTENSION)) {
                    classList.add(Classfile.fromFile(dirFile));
                }
            }
        } else {
            LOGGER.error("Directory of class files doesnt set!");
            System.out.println("Please include directory of class files");
            System.exit(0);
        }

        return classList;
    }

    /**
     * Main method
     *
     * @param args arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        exec(args);
    }
}
