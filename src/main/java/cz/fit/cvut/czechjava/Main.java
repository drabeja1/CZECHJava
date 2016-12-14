package cz.fit.cvut.czechjava;

import java.util.Arrays;

/**
 *
 * @author Jakub
 */
public class Main {

    public static void main(String[] args) throws Exception {
        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

        switch (args[0]) {
            case "run":
                Run.exec(commandArgs);
                break;
            case "compile":
                Compile.exec(commandArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported command, use either 'run' or 'compile' command!");
        }
    }
}
