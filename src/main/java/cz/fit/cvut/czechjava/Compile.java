package cz.fit.cvut.czechjava;

import cz.fit.cvut.czechjava.compiler.CZECHJavaCompiler;
import cz.fit.cvut.czechjava.interpreter.ClassPool;
import cz.fit.cvut.czechjava.parser.Node;
import cz.fit.cvut.czechjava.compiler.Class;
import cz.fit.cvut.czechjava.compiler.Classfile;
import cz.fit.cvut.czechjava.compiler.CompilerException;
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

/**
 *
 * @author Jakub
 */
public class Compile {

    public final static String COMPILED_LIBRARIES_DIRECTORY = "czechjava_lib/out/";
    public final static String SOURCE_LIBRARIES_DIRECTORY = "czechjava_lib/src/";

    final static String CLASS_TYPE_EXTENSION = "trida";

    public static void printHelp() {
        System.out.println("Pouziti: czechjava <moznosti> <zdrojove soubory nebo slozka>\n"
                + "dale muzu byt: \n"
                + "-d slozka pro vygenerovane .trida soubory \n");
    }

    public static void exec(String[] args) throws Exception {
        if (args.length == 0) {
            printHelp();
            System.exit(0);
        }

        // zpracovani argumentu
        List<String> filenames = new ArrayList<>(Arrays.asList(args));
        String outputDirectory = "./";

        for (int i = 0; i < args.length - 1; i++) {
            String param = args[i];
            String value = args[i + 1];

            if (param.equals("-d")) {
                filenames.remove(i);
                filenames.remove(i);
                outputDirectory = value;
            }
        }

        if (filenames.isEmpty()) {
            printHelp();
            System.exit(0);
        }

        //Add all libraries for type control (sources)
        filenames.add(SOURCE_LIBRARIES_DIRECTORY);

        List<Node> rootNodeList = parse(filenames);

        List<cz.fit.cvut.czechjava.compiler.Class> classList = new ArrayList<>();
        CZECHJavaCompiler compiler = new CZECHJavaCompiler();

        try {
            //First stage - precompilation
            for (Node node : rootNodeList) {
                classList.addAll(compiler.precompile(node));
            }

            //Second stage - compilation
            ClassPool classPool = new ClassPool(classList);

            classList.clear();

            for (Node node : rootNodeList) {
                classList.addAll(compiler.compile(node, classPool));
            }

            //We don't want the libraries to be generated again
            classList = removeLibraries(classList);

            //Create output directory
            File outputDir = new File(outputDirectory);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            //Clean the directory
            removeClassfiles(outputDirectory);

            //Generate files
            for (Class clazz : classList) {
                Classfile.toFile(clazz, outputDir.getAbsolutePath() + "/" + clazz.getClassName() + "." + CLASS_TYPE_EXTENSION);
            }
        } catch (CompilerException e) {
            System.out.println("compile error: " + e.getMessage());
        }

    }

    protected static void removeClassfiles(String dir) {
        File file = new File(dir);
        if (!file.isDirectory()) {
            System.out.println(dir + " is not a directory");
            System.exit(0);
        }

        for (File dirFile : file.listFiles()) {
            String extension = "";

            int i = dirFile.getName().lastIndexOf('.');
            if (i > 0) {
                extension = dirFile.getName().substring(i + 1);
            }

            if (extension.equals(Compile.CLASS_TYPE_EXTENSION)) {
                dirFile.delete();
            }

        }

    }

    protected static List<File> listAllFiles(List<String> filenames) {
        List<File> files = new ArrayList<>();

        for (String fileName : filenames) {
            File file = new File(fileName);
            files.addAll(getFilesRecursively(file));
        }

        return files;
    }

    protected static List<File> getFilesRecursively(File file) {
        List<File> files = new ArrayList<>();

        if (file.isDirectory()) {
            File[] dirFiles = file.listFiles();
            for (File dirFile : dirFiles) {
                files.addAll(getFilesRecursively(dirFile));
            }

        } else {
            files.add(file);
        }

        return files;
    }

    protected static List<Node> parse(List<String> filenames) throws FileNotFoundException, ParseException, ParseException {
        CZECHJavaParser jp = null;
        List<Node> rootNodeList = new ArrayList<>();

        for (File file : listAllFiles(filenames)) {
            Reader fr = new InputStreamReader(new FileInputStream(file));

            if (jp == null) {
                jp = new CZECHJavaParser(fr);
            } else {
                jp.ReInit(fr);
            }

            try {
                //Parse
                jp.CompilationUnit();
                ASTCompilationUnit node = (ASTCompilationUnit) jp.rootNode();

                rootNodeList.add(node);
            } catch (ParseException ex) {
                System.out.println("parse error in file " + file.getName() + ": " + ex.getMessage());
                throw ex;
            }
        }

        return rootNodeList;
    }

    public static List<Class> removeLibraries(List<Class> classList) throws IOException {

        File directory = new File(COMPILED_LIBRARIES_DIRECTORY);
        File[] dirFiles = directory.listFiles();
        for (File dirFile : dirFiles) {
            String extension = "";

            int i = dirFile.getName().lastIndexOf('.');
            if (i > 0) {
                extension = dirFile.getName().substring(i + 1);
            }

            if (extension.equals(Compile.CLASS_TYPE_EXTENSION)) {
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
}
