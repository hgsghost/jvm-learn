package class1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterpreterTest {

    public static int test() {
        int i = 1;
        return i;
    }
    public static void main(String[] args) {
        Map<Integer,Instruction> instructionMap=new HashMap<>();
        instructionMap.put(0,new IConst1());
        instructionMap.put(1,new ILoad0());
        instructionMap.put(2,new IStore0());
        instructionMap.put(3,new IReturn());
        Interpreter.run(new Frame(),instructionMap);
        System.out.println("Hello World!");
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
