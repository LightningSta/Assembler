import org.example.Assembler;
import org.example.Interpreter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class test {
    @Test
    public void test() {
        String[] params= new String[3];
        params[0] = "res.asm";
        params[1] = "res.bin";
        params[2] = "res.yaml";
        try {
            Assembler.run(params);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void test2() {
        String[] params= new String[3];
        params[0] = "res.bin";
        params[1] = "res_res.yaml";
        params[2] = "0-15";
        try {
            Interpreter.main(params);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testAssembler() {
        String[] params= new String[3];
        params[0] = "";
        params[1] = "res.bin";
        params[2] = "res.yaml";
        Assertions.assertThrows(Exception.class, () -> Assembler.run(params));
    }
    @Test
    public void testEmptyInterpreter() {
        String[] params= new String[3];
        params[0] = "res.bin";
        params[1] = "res_res.yaml";
        params[2] = "";
        Assertions.assertThrows(Exception.class, ()->{
            Interpreter.main(params);
        });
    }
}
