package class2;

 interface  Instruction{
    default int offSet(){
        return 1;
    }
    void eval(InterpreterTest2.Frame frame);
}