package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String[] params= new String[3];
        params[0] = "res.asm";
        params[1] = "res.bin";
        params[2] = "res.yaml";
        Assembler.run(params);
        params = new String[3];
        params[0] = "res.bin";
        params[1] = "res_res.yaml";
        params[2] = "0-15";
        Interpreter.main(params);
    }
}