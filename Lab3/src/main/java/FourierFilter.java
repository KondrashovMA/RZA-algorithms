public class FourierFilter {
    SampleValues sv = new SampleValues(); // объект класса мнгновенных значений
    RMSValues rms = new RMSValues(); // объект класса действующих значений
    private int dots = 80; // число точек за период
    private double[] bufferSinCurrent = new double[dots]; // массивы для формирования ортогональных значений
    private double[] bufferCosCurrent = new double[dots]; // для токов и напряжений
    private double[] bufferSinVoltage = new double[dots];
    private double[] bufferCosVoltage = new double[dots];
    private double[] arraySin = new double[dots]; // массив синусов и косинсов, для предварительного расчёт
    private double[] arrayCos = new double[dots];
    private double sumSinCurrent = 0; // ортогональные составляющие напряжений и токов
    private double sumCosCurrent = 0;
    private double sumSinVoltage = 0;
    private double sumCosVoltage = 0;
    private double im,re; // мгновенные составляющие
    private String phase; // задание, для какой фазы ведётся расчёт
    private int count;
    private double resCurrent, resVoltage; // действующие значения тока/напряжения
    private double angleCurrent, angleVoltage; // угол тока/напряжения

    public FourierFilter(String phase){
        this.phase = phase;
        for(int i=0; i<dots; i++){
            arraySin[i] = Math.sin(2*Math.PI*i*1/dots);
            arrayCos[i] = Math.cos(2*Math.PI*i*1/dots);
        }
    }

    public void calculate(double phaseCurrent, double phaseVoltage) {
        // токи

        re = phaseCurrent * arraySin[count];
        im = phaseCurrent * arrayCos[count];

        sumCosCurrent = sumCosCurrent + re - bufferCosCurrent[count];
        sumSinCurrent = sumSinCurrent + im - bufferSinCurrent[count];
        bufferCosCurrent[count] = re;
        bufferSinCurrent[count] = im;

        resCurrent = Math.sqrt(Math.pow(sumCosCurrent, 2) + Math.pow(sumSinCurrent, 2))/dots;
        angleCurrent = Math.atan2(sumSinCurrent, sumCosCurrent);


        // напряжения
        re = phaseVoltage * arraySin[count];
        im = phaseVoltage * arrayCos[count];

        sumCosVoltage = sumCosVoltage + re - bufferCosVoltage[count];
        sumSinVoltage = sumSinVoltage+ im - bufferSinVoltage[count];
        bufferCosVoltage[count] = re;
        bufferSinVoltage[count] = im;

        resVoltage = Math.sqrt(Math.pow(sumCosVoltage, 2) + Math.pow(sumSinVoltage, 2))/dots;
        angleVoltage = Math.atan2(sumSinVoltage, sumCosVoltage);

        if(++count==dots){
            count = 0;
        }

        switch(phase){
            case("A"):
                rms.setCurrentPhA(resCurrent);
                rms.setVoltagePhA(resVoltage);
                rms.setCurrentAnglePhA(angleCurrent);
                rms.setVoltageAnglePhA(angleVoltage);
                break;
            case("B"):
                rms.setCurrentPhB(resCurrent);
                rms.setVoltagePhB(resVoltage);
                rms.setCurrentAnglePhB(angleCurrent);
                rms.setVoltageAnglePhB(angleVoltage);
                break;
            case("C"):
                rms.setCurrentPhC(resCurrent);
                rms.setVoltagePhC(resVoltage);
                rms.setCurrentAnglePhC(angleCurrent);
                rms.setVoltageAnglePhC(angleVoltage);
                break;
        }
    }

    public RMSValues getRms() { return rms;}
    public void setRms(RMSValues rms) {this.rms = rms;}
    public SampleValues getSv() { return sv;}
    public void setSv(SampleValues sv) { this.sv = sv; }
}
