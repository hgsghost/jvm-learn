package class2;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InterpreterTest2 {

    public static int test() {
        int i = 1;
        return i;
    }
    public static void main(String[] args) {
        Map<Integer,Instruction> instructionMap=parse(args[0]);
        /*instructionMap.put(0,new IConst1());
        instructionMap.put(1,new ILoad0());
        instructionMap.put(2,new IStore0());
        instructionMap.put(3,new IReturn());*/
        Interpreter.run(new Frame(),instructionMap);
        System.out.println("Hello World!");
    }
    private static Map<Integer, Instruction> parse(String file) {
        List<String> rawLines;
        try {
            // 获取文件的所有行
            rawLines = Files.readAllLines(Paths.get(file));
        } catch (Exception e) {
            System.out.println("file not found ?");
            return null;
        }

        if (rawLines.isEmpty()) {
            System.out.println("empty file");
            return null;
        }

        List<String> lines = rawLines.stream()
                .map(String::trim) // 消除首尾空格
                .map(it -> it.replaceAll(": ", " ")) // 替换冒号为空格
                .map(it -> it.replaceAll(", ", " ")) // 替换逗号为空格
                .map(it -> it.replaceAll(" +", " ")) // 多个空格合并
                .filter(it -> it.length() > 0)
                .collect(Collectors.toList());

        Map<Integer, Instruction> instructionMap = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            String raw = lines.get(i);
            String[] terms = raw.split(" ");
            int pc = Integer.parseInt(terms[0]);
            String inst = terms[1];

            Instruction instruction = null;
            switch (inst.toLowerCase()) {
                case "iconst_1":
                    instruction = new IConst1();
                    break;
                case "istore_0":
                    instruction = new IStore0();
                    break;
                case "iload_0":
                    instruction = new ILoad0();
                    break;
                case "ireturn":
                    instruction = new IReturn();
                    break;
                default:
                    break;
            }

            if (instruction == null) {
                System.out.println("parse file failed. raw : " + raw);
                return null;
            }
            instructionMap.put(pc, instruction);
        }
        return instructionMap;
    }
    //栈帧
    static class Frame{
        //操作数栈
        public Stack<Integer> operationStack=new Stack<>();
        //计数器
        public int pcCount=0;
        //局部变量表
        public Map<Integer,Integer> localVariable=new HashMap<>();
        //动态链接
        //返回地址
    }
    static class Stack<T>{
        List<T> list=new ArrayList<>();
        public void push(T t) {
            list.add(0,t);
        }
        public T pop(){
            return list.remove(0);
        }
    }
    interface  Instruction{
        default int offSet(){
            return 1;
        }
        void eval(Frame frame);
    }
    static class IConst1 implements  Instruction{
        @Override
        public void eval(Frame frame) {
            frame.operationStack.push(1);
            frame.pcCount+=offSet();
        }
    }
    static class IStore0 implements  Instruction{
        @Override
        public void eval(Frame frame) {
            frame.localVariable.put(0,frame.operationStack.pop());
            frame.pcCount+=offSet();
        }
    }
    static class ILoad0 implements  Instruction{
        @Override
        public void eval(Frame frame) {
            frame.operationStack.push(frame.localVariable.get(0));
            frame.pcCount+=offSet();
        }
    }
    static class IReturn implements  Instruction{
        @Override
        public void eval(Frame frame) {
            int tmp=frame.operationStack.pop();
            System.out.println(tmp);
            frame.pcCount+=offSet();
        }
    }
    static  class Interpreter{
        public static void run (Frame frame,Map<Integer,Instruction> instructionMap ){
            do{
                instructionMap.get(frame.pcCount).eval(frame);
            }
            while(instructionMap.containsKey(frame.pcCount));
        }
    }


}
