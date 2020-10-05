package demo;

import java.util.*;
import java.io.*;

/**
 * Represents an instance of a running command line chess application. Supports playing against
 * a bot as well as controlling both colors. For full usage details, look at the help instructions
 * supplied when the application is running
 */
public class Program {





    /**
     * runs an instance of the command line chess interface. Supports playing both
     * against a bot and by oneself
     *
     * @param args command line args
     */
    public static void main(String[] args) {
        ProgramStateMachine machine = new ProgramStateMachine();
        machine.start();
        while (machine.update()) {}
    }

    public static ChessPosition getChessPosition() {
        return ProgramStateMachine.chessPosition;
    }
}