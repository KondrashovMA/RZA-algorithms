public class OutputSignal {
    public void trip(boolean signal1, boolean signal2, boolean signal3){
        if(signal1 || signal2 || signal3) {
            Charts.addDiscreteData(0, true);
        }else{
            Charts.addDiscreteData(0, false);
        }
    }
    public void tripSignalFirst(boolean signal){
        Charts.addDiscreteData(0,signal);
    }

    public void tripSignalSecond(boolean signal){
        Charts.addDiscreteData(1,signal);
    }

    public void tripSignalThird(boolean signal){
        Charts.addDiscreteData(2,signal);
    }

}
