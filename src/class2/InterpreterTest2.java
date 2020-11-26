package class2;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
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
        /*File file=new File("");
        System.out.println(file.getAbsolutePath());*/
        Map<Integer,Instruction> instructionMap=parse("doc"+File.separator+ "misc/Sum10.bc");
        System.out.println("999"+ JSONObject.toJSONString(instructionMap));
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
                case "iconst_0":
                    instruction = new IConst0();
                    break;
                case "iconst_1":
                    instruction = new IConst1();
                    break;
                case "istore_0":
                    instruction = new IStore0();
                    break;
                case "istore_1":
                    instruction = new IStore1();
                    break;
                case "istore_2":
                    instruction = new IStore2();
                    break;
                case "iload_0":
                    instruction = new ILoad0();
                    break;
                case "iload_1":
                    instruction = new ILoad1();
                    break;
                case "iload_2":
                    instruction = new ILoad2();
                    break;
                case "ireturn":
                    instruction = new IReturn();
                    break;
                case "bipush":
                    instruction = new Bipush(Byte.parseByte(terms[2]));
                    break;
                case "if_icmpgt":
                    instruction = new If_Icmpgt(Integer.parseInt(terms[2]));
                    break;
                case "iadd":
                    instruction = new Iadd();
                    break;
                case "iinc":
                    instruction = new Iinc(Integer.parseInt(terms[2]), Integer.parseInt(terms[3]));
                    break;
                case "goto":
                    instruction = new Goto(Short.parseShort(terms[2]));
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

    static class IConst0 implements  Instruction{
        @Override
        public void eval(Frame frame) {
            frame.operationStack.push(0);
            frame.pcCount+=offSet();
        }
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
    static class IStore1 implements  Instruction{
        @Override
        public void eval(Frame frame) {
            frame.localVariable.put(1,frame.operationStack.pop());
            frame.pcCount+=offSet();
        }
    }
    static class IStore2 implements  Instruction{
        @Override
        public void eval(Frame frame) {
            frame.localVariable.put(2,frame.operationStack.pop());
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
    static class ILoad1 implements  Instruction{
        @Override
        public void eval(Frame frame) {
            frame.operationStack.push(frame.localVariable.get(1));
            frame.pcCount+=offSet();
        }
    }
    static class ILoad2 implements  Instruction{
        @Override
        public void eval(Frame frame) {
            frame.operationStack.push(frame.localVariable.get(2));
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
    static class Bipush implements  Instruction{
        private int num;
        public Bipush(int num){
            this.num=num;
        }
        @Override
        public void eval(Frame frame) {
            frame.operationStack.push(num);
            frame.pcCount+=offSet();
        }
        @Override
        public int offSet(){
            return 2;
        }

    }
    static class If_Icmpgt implements  Instruction{
        private int position;
        public If_Icmpgt(int position){
            this.position=position;
        }
        @Override
        public void eval(Frame frame) {
            if(frame.operationStack.pop()<frame.operationStack.pop()){
                frame.pcCount=position;
            }else{
                frame.pcCount+=this.offSet();
            }
        }
        @Override
        public int offSet(){
            return 3;
        }
    }
    static class Iadd implements  Instruction{

        @Override
        public void eval(Frame frame) {
            frame.operationStack.push(frame.operationStack.pop()+frame.operationStack.pop());
             frame.pcCount+=offSet();
        }
    }
    static class Iinc implements  Instruction{
        private int localVariablePosition;
        private int num;
        public Iinc(int localVariablePosition,int num){
            this.localVariablePosition=localVariablePosition;
            this.num=num;
        }
        @Override
        public void eval(Frame frame) {
            frame.localVariable.put(localVariablePosition,frame.localVariable.get(localVariablePosition)+num);
            frame.pcCount+=this.offSet();
        }
        @Override
        public int offSet(){
            return 3;
        }
    }
    static class Goto implements  Instruction{
        private int position;
        public Goto(int position){
            this.position=position;
        }
        @Override
        public void eval(Frame frame) {
            frame.pcCount=position;
        }
        @Override
        public int offSet(){
            return 3;
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
