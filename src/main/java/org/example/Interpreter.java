package org.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.yaml.snakeyaml.Yaml;
public class Interpreter {
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: java Interpreter <binary file> <result file> <memory range>");
            return;
        }

        String binaryFile = args[0];
        String resultFile = args[1];
        String[] range = args[2].split("-");
        int start = Integer.parseInt(range[0]);
        int end = Integer.parseInt(range[1]);

        byte[] binary = Files.readAllBytes(Path.of(binaryFile));
        Stack<Integer> stack = new Stack<>();
        int[] memory = new int[1024];

        for (int i = 0; i < binary.length; i += 4) {
            int instruction = (binary[i] & 0xFF) |
                    ((binary[i + 1] & 0xFF) << 8) |
                    ((binary[i + 2] & 0xFF) << 16) |
                    ((binary[i + 3] & 0xFF) << 24);

            int opcode = instruction & 0x1F;
            int operand = (instruction >> 5) & 0x1FFFFFF;

            System.out.printf("Executing opcode: %d, operand: %d%n", opcode, operand);
            System.out.println("Stack before operation: " + stack);

            switch (opcode) {
                case 3 -> stack.push(operand); // Загрузка константы
                case 10 -> {
                    if (stack.isEmpty()) {
                        System.err.println("Stack is empty for operation READ.");
                        return;
                    }
                    stack.push(memory[stack.pop() + operand]);
                }
                case 8 -> {
                    if (stack.size() < 2) {
                        System.err.println("Not enough elements in stack for operation WRITE.");
                        break;
                    }
                    memory[stack.pop()] = stack.pop();
                }
                case 15 -> {
                    if (stack.size() < 2) {
                        System.err.println("Not enough elements in stack for operation ROTATE.");
                        return;
                    }
                    int b = stack.pop();
                    int a = stack.pop();
                    stack.push(Integer.rotateRight(a, b));
                }
                default -> {
                    System.err.println("Unknown opcode: " + opcode);
                    return;
                }
            }

            System.out.println("Stack after operation: " + stack);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("memory", Arrays.copyOfRange(memory, start, end + 1));

        Yaml yaml = new Yaml();
        try (Writer writer = Files.newBufferedWriter(Path.of(resultFile))) {
            yaml.dump(result, writer);
        }
    }
}

