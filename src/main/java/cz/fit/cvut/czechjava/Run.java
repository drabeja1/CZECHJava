package cz.fit.cvut.czechjava;

import cz.fit.cvut.czechjava.compiler.Classfile;
import cz.fit.cvut.czechjava.interpreter.CZECHJavaInterpreter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Jakub
 */
public class Run {

    public static void printHelp() {
        System.out.println("Pouziti: czechjava <moznosti> <slozka se zkompilovanymi soubory> <argumenty>\n"
                + "moznosti: \n"
                + "-h velikost heap \n"
                + "-f pocet framu \n"
                + "-s velikost stacku ve framu \n"
        );
    }

    public static void exec(String[] args) throws Exception {
        if (args.length == 0) {
            printHelp();
            System.exit(1);
        }

        List<String> arguments = new ArrayList<>(Arrays.asList(args));

        int heap_size = 1024;
        int frame_count = 128;
        int stack_size = 128;

        for (int i = 0; i < args.length - 1; i++) {
            String param = args[i];
            String value = args[i + 1];
            boolean isParam = false;

            if (param.equals("-h")) {
                isParam = true;
                heap_size = Integer.parseInt(value);
            } else if (param.equals("-f")) {
                isParam = true;
                frame_count = Integer.parseInt(value);
            } else if (param.equals("-s")) {
                isParam = true;
                stack_size = Integer.parseInt(value);
            }

            if (isParam) {
                arguments.remove(param);
                arguments.remove(value);
            }
        }

        if (arguments.size() == 0) {
            printHelp();
            System.exit(1);
        }

        String directory = arguments.get(0);
        arguments.remove(0);

        List<cz.fit.cvut.czechjava.compiler.Class> librariesList = loadLibraries();

        List<cz.fit.cvut.czechjava.compiler.Class> classList = loadClassfiles(directory);

        classList.addAll(librariesList);

        CZECHJavaInterpreter interpreter = new CZECHJavaInterpreter(classList, heap_size, frame_count, stack_size);
        interpreter.run(arguments);

    }

    public static List<cz.fit.cvut.czechjava.compiler.Class> loadLibraries() throws IOException {
        return loadClassfiles(Compile.COMPILED_LIBRARIES_DIRECTORY);
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

                if (extension.equals(Compile.CLASS_TYPE_EXTENSION)) {
                    classList.add(Classfile.fromFile(dirFile));
                }
            }

        } else {
            System.out.println("Please include directory of class files");
            System.exit(0);
        }

        return classList;
    }
}
