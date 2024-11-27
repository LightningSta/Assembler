package org.example;

import org.yaml.snakeyaml.Yaml;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Assembler {
    public static void run(String[] args) throws IOException, IOException {
        if (args.length < 3) {
            System.err.println("Usage: java Assembler <source file> <binary file> <log file>");
            return;
        }
        String sourceFile = args[0];
        String binaryFile = args[1];
        String logFile = args[2];

        List<String> sourceLines = Files.readAllLines(Path.of(sourceFile));
        List<Instruction> instructions = new ArrayList<>();
        List<Map<String, Object>> logEntries = new ArrayList<>();

        for (String line : sourceLines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            Instruction instruction = Instruction.parse(line);
            instructions.add(instruction);

            Map<String, Object> logEntry = new LinkedHashMap<>();
            logEntry.put("instruction", line);
            logEntry.put("binary", instruction.toBinaryString());
            logEntries.add(logEntry);
        }

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(binaryFile))) {
            for (Instruction instr : instructions) {
                dos.write(instr.toBinary());
            }
        }

        Yaml yaml = new Yaml();
        try (Writer writer = Files.newBufferedWriter(Path.of(logFile))) {
            yaml.dump(logEntries, writer);
        }
    }
}

class Instruction {
    private final int opcode;
    private final int operand;

    public Instruction(int opcode, int operand) {
        this.opcode = opcode;
        this.operand = operand;
    }

    public static Instruction parse(String line) {
        String[] parts = line.split("\\s+");
        int opcode = Integer.parseInt(parts[0]);
        int operand = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        return new Instruction(opcode, operand);
    }

    public byte[] toBinary() {
        int instruction = (opcode & 0x1F) | ((operand & 0x1FFFFFF) << 5);
        return new byte[] {
                (byte) (instruction & 0xFF),
                (byte) ((instruction >> 8) & 0xFF),
                (byte) ((instruction >> 16) & 0xFF),
                (byte) ((instruction >> 24) & 0xFF)
        };
    }

    public String toBinaryString() {
        return String.format("%08X", (opcode & 0x1F) | ((operand & 0x1FFFFFF) << 5));
    }
}
