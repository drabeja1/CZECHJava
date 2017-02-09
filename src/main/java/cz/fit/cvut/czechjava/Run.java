package cz.fit.cvut.czechjava;

import cz.fit.cvut.czechjava.compiler.Classfile;
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
 * Zakladni trida pro spusteni interpreteru
 * 
 * @author Jakub
 */
public class Run {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(Run.class.getName());
    
    private static Options prepareOptions() {
        Option heapSizeOpt = new Option("h", "heap", true, "Velikost heap. (volitelne)");
        heapSizeOpt.setOptionalArg(true);
        Option framesOpt = new Option("f", "frame", true, "Pocet framu. (volitelne)");
        framesOpt.setOptionalArg(true);
        Option stackOpt = new Option("s", "stack", true, "Velikost stacku ve framu. (volitelne)");
        stackOpt.setOptionalArg(true);
        Option compiledOpt = new Option("c", "compiled", true, "Slozka se zkompilovanymi soubory.");
        compiledOpt.setRequired(true);
        Option argumentsOpt = new Option("a", "arguments", true, "Argumenty programu.");
        argumentsOpt.setRequired(true);

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
            directory = cmd.getOptionValue("compiled");
            arguments = cmd.getOptionValues("arguments");
        } catch (NumberFormatException | ParseException ex) {
            LOGGER.fatal(ex);
            formatter.printHelp("czechjava", options);
            System.exit(1);
        }

        List<cz.fit.cvut.czechjava.compiler.Class> librariesList = loadLibraries();
        List<cz.fit.cvut.czechjava.compiler.Class> classList = loadClassfiles(directory);
        classList.addAll(librariesList);

        CZECHJavaInterpreter interpreter = new CZECHJavaInterpreter(classList, heapSize, frameCount, stackSize);
        interpreter.run(Arrays.asList(arguments));
    }

    public static List<cz.fit.cvut.czechjava.compiler.Class> loadLibraries() throws IOException {
        return loadClassfiles(Globals.COMPILED_LIBRARIES_DIRECTORY);
    }

    public static List<cz.fit.cvut.czechjava.compiler.Class> loadClassfiles(String directoryName) throws IOException {
        File directory = new File(directoryName);

        List<cz.fit.cvut.czechjava.compiler.Class> classList = new ArrayList<>();

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
            System.out.println("Please include directory of class files");
            System.exit(0);
        }

        return classList;
    }

    /**
     * Main method
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        exec(args);
    }
}
