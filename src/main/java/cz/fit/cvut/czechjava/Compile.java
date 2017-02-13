package cz.fit.cvut.czechjava;

import cz.fit.cvut.czechjava.compiler.CZECHJavaCompiler;
import cz.fit.cvut.czechjava.interpreter.ClassPool;
import cz.fit.cvut.czechjava.parser.Node;
import cz.fit.cvut.czechjava.compiler.model.Class;
import cz.fit.cvut.czechjava.compiler.model.Classfile;
import cz.fit.cvut.czechjava.compiler.exceptions.CompilerException;
import cz.fit.cvut.czechjava.parser.ASTCompilationUnit;
import cz.fit.cvut.czechjava.parser.CZECHJavaParser;
import cz.fit.cvut.czechjava.parser.ParseException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

/**
 *
 * @author Jakub
 */
public class Compile {

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(Compile.class.getName());

    /**
     * Prepare arguments options
     *
     * @return
     */
    private static Options prepareOptions() {
        Option sourceOpt = new Option("s", "source", true, "Zdrojove soubory nebo slozka.");
        sourceOpt.setRequired(true);
        Option targetOpt = new Option("t", "target", true, "Slozka pro vygenerovani .trida soubrou. (volitelne)");
        targetOpt.setOptionalArg(true);

        Options options = new Options();
        options.addOption(sourceOpt);
        options.addOption(targetOpt);
        return options;
    }

    /**
     * Run compile
     *
     * @param args program arguments
     * @throws Exception
     */
    public static void exec(String[] args) throws Exception {
        // perform args
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        Options options = prepareOptions();

        List<String> filenames = new ArrayList<>();
        String outputDirectory = "./";

        try {
            CommandLine cmd = parser.parse(options, args);
            filenames.addAll(Arrays.asList(cmd.getOptionValues("source")));

            if (cmd.hasOption("target")) {
                outputDirectory = cmd.getOptionValue("target");
            }
        } catch (NumberFormatException | org.apache.commons.cli.ParseException ex) {
            LOGGER.fatal(ex);
            formatter.printHelp("czechjavac", options);
            System.exit(0);
        }

        // Add all libraries for type control (sources)
        filenames.add(Globals.SOURCE_LIBRARIES_DIRECTORY);

        List<Node> rootNodeList = parse(filenames);

        List<Class> classList = new ArrayList<>();
        CZECHJavaCompiler compiler = new CZECHJavaCompiler();

        try {
            // First stage - PRECOMPULATION
            for (Node node : rootNodeList) {
                classList.addAll(compiler.precompile(node));
            }

            // Second stage - COMPILATION
            ClassPool classPool = new ClassPool(classList);

            classList.clear();

            for (Node node : rootNodeList) {
                classList.addAll(compiler.compile(node, classPool));
            }

            // We don't want the libraries to be generated again
            classList = removeLibraries(classList);

            // Create output directory
            File outputDir = new File(outputDirectory);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // Clean the directory
            removeClassfiles(outputDirectory);

            // Generate files
            for (Class clazz : classList) {
                Classfile.toFile(clazz, outputDir.getAbsolutePath() + "/" + clazz.getClassName() + "." + Globals.CLASS_TYPE_EXTENSION);
            }
        } catch (CompilerException e) {
            LOGGER.fatal(e);
            System.out.println("compile error: " + e.getMessage());
        }
    }

    /**
     * Romve class files
     *
     * @param dir
     */
    protected static void removeClassfiles(String dir) {
        File file = new File(dir);
        if (!file.isDirectory()) {
            LOGGER.fatal(dir + " is not a directory");
            System.out.println(dir + " is not a directory");
            System.exit(0);
        }

        for (File dirFile : file.listFiles()) {
            String extension = "";

            int i = dirFile.getName().lastIndexOf('.');
            if (i > 0) {
                extension = dirFile.getName().substring(i + 1);
            }

            if (extension.equals(Globals.CLASS_TYPE_EXTENSION)) {
                dirFile.delete();
            }

        }

    }

    /**
     * Return all files to compile
     *
     * @param fileNames
     * @return
     */
    protected static List<File> listAllFiles(List<String> fileNames) {
        List<File> files = new ArrayList<>();
        fileNames.forEach(fileName -> files.addAll(getFilesRecursively(new File(fileName))));
        return files;
    }

    /**
     * If file is directory, return list of files, else return file
     *
     * @param file
     * @return
     */
    protected static List<File> getFilesRecursively(File file) {
        List<File> files = new ArrayList<>();

        if (file.isDirectory()) {
            File[] dirFiles = file.listFiles();
            for (File dirFile : dirFiles) {
                if (dirFile.getName().endsWith(Globals.SOURCE_TYPE_EXTENSION)) {
                    files.addAll(getFilesRecursively(dirFile));
                } else {
                    LOGGER.warn("Attempt to compile non supported file '" + dirFile.getName() + "'");
                }
            }
        } else {
            files.add(file);
        }

        return files;
    }

    /**
     * Parse files
     *
     * @param fileNames
     * @return
     * @throws FileNotFoundException
     * @throws ParseException
     */
    protected static List<Node> parse(List<String> fileNames) throws FileNotFoundException, ParseException, ParseException {
        CZECHJavaParser jp = null;
        List<Node> rootNodeList = new ArrayList<>();

        for (File file : listAllFiles(fileNames)) {
            Reader fr = new InputStreamReader(new FileInputStream(file));

            if (jp == null) {
                jp = new CZECHJavaParser(fr);
            } else {
                jp.ReInit(fr);
            }

            try {
                // Parse
                jp.CompilationUnit();
                ASTCompilationUnit node = (ASTCompilationUnit) jp.rootNode();

                rootNodeList.add(node);
            } catch (ParseException ex) {
                LOGGER.fatal(ex);
                System.out.println("Parse error in file " + file.getName() + ": " + ex.getMessage());
                throw ex;
            }
        }

        return rootNodeList;
    }

    /**
     * Remove libraries from class list
     *
     * @param classList
     * @return
     * @throws IOException
     */
    public static List<Class> removeLibraries(List<Class> classList) throws IOException {

        File directory = new File(Globals.COMPILED_LIBRARIES_DIRECTORY);
        File[] dirFiles = directory.listFiles();
        for (File dirFile : dirFiles) {
            String extension = "";

            int i = dirFile.getName().lastIndexOf('.');
            if (i > 0) {
                extension = dirFile.getName().substring(i + 1);
            }

            if (extension.equals(Globals.CLASS_TYPE_EXTENSION)) {
                Class library = Classfile.fromFile(dirFile);

                for (Iterator<Class> iter = classList.iterator(); iter.hasNext();) {
                    Class clazz = iter.next();
                    if (clazz.getClassName().equals(library.getClassName())) {
                        iter.remove();
                    }
                }
            }
        }

        return classList;
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        exec(args);
    }
}
