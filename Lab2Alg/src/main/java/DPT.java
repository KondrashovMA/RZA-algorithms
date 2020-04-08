public class DPT { //диф. защита трансформатора, экра

    public void setChoose(String choose) {
        this.choose = choose;
    }
    private String choose;
    private EffectiveValues VNrms = new EffectiveValues();
    private EffectiveValues SNrms = new EffectiveValues();
    private EffectiveValues NNrms = new EffectiveValues();


    ComplexNumber z1phA = new ComplexNumber();//ток фазы А стороны ВН тр-ра
    ComplexNumber z2phA = new ComplexNumber();//ток фазы A стороны СН тр-ра
    ComplexNumber z3phA = new ComplexNumber();//ток фазы A стороны НН тр-ра

    ComplexNumber z1phB = new ComplexNumber();//ток фазы B стороны ВН тр-ра
    ComplexNumber z2phB = new ComplexNumber();//ток фазы B стороны СН тр-ра
    ComplexNumber z3phB = new ComplexNumber();//ток фазы B стороны НН тр-ра

    ComplexNumber z1phC = new ComplexNumber();//ток фазы C стороны НН тр-ра
    ComplexNumber z2phC = new ComplexNumber();//ток фазы C стороны НН тр-ра
    ComplexNumber z3phC = new ComplexNumber();//ток фазы C стороны НН тр-ра

    ComplexNumber CurDiffphA = new ComplexNumber(); //диф. ток фазы А
    ComplexNumber CurStopphA = new ComplexNumber(); //Тормозной ток фазы А

    ComplexNumber CurDiffphB = new ComplexNumber(); //диф. ток фазы B
    ComplexNumber CurStopphB = new ComplexNumber(); //Тормозной ток фазы B

    ComplexNumber CurDiffphC = new ComplexNumber(); //диф. ток фазы C
    ComplexNumber CurStopphC = new ComplexNumber(); //Тормозной ток фазы C

    Block block = new Block();
    OutputData outData = new OutputData();


    Complex complex = new Complex();
    private double IbazVN=3.6, IbazSN=3.28, IbazNN=3.6; //базисные токи
    private double I_0_dpt = 1.3 * (2*1*0.1 + 0.02); //0.286 начальный ток срабатывания ДЗТ
    private double I_start_stop = 1; // ток начала торможения ДЗТ
    private double k_stop = 0.28; //коэффициент торможения
    private double I_cutoff_dpt = 8.88; //токовая отсечка диф. тока
    private double triggerCurrentPhA, triggerCurrentPhB, triggerCurrentPhC; //уставки пофазно, с учётом торозного тока
    private boolean blockA, blockB, blockC; //блокировки, если true хоть по одной фазе - то защита блокируется
    private  double K_Five_to_One=10; //уставка по блокировке по пятой гармонике



    private double max=0;


    public DPT(EffectiveValues VNrms,EffectiveValues SNrms, EffectiveValues NNrms){
        this.VNrms=VNrms;
        this.SNrms=SNrms;
        this.NNrms=NNrms;
    }
    public double GetDiffCurr(double I_stop_res){ //вычисляет ток срабатывания для случая, когда углы между векторами токов более 180
        return I_0_dpt + k_stop*(I_stop_res - I_start_stop);
    }

    public void Calculation( ComplexNumber z1, ComplexNumber z2, ComplexNumber z3, String phase) {
        ComplexNumber Cur1 = new ComplexNumber();
        ComplexNumber Cur2 = new ComplexNumber();
        ComplexNumber CurTemp = new ComplexNumber();
        ComplexNumber CurStop = new ComplexNumber();
        double temp;
        double angleStop;
        double triggerCurrent=0;
        Cur1 = complex.MaxFromThreeComplex(z1, z2, z3); //определение первого тока, по СТО
        CurTemp = complex.ComplexThreeSum(z1, z2, z3); //сумма токов
        Cur2 = complex.ComplexMinus(CurTemp , Cur1); //второй ток, по СТО
        if(Cur1.getAngle() > Cur2.getAngle()){ //определение угла между векторами, проверка нужна чтобы избежать ситуации с отрицательными углами
            angleStop = Cur1.getAngle() - Cur2.getAngle();
        }else{
            angleStop = Cur2.getAngle() - Cur1.getAngle();
        }
        if  ((90 < Math.toDegrees(angleStop) ) && (270 > Math.toDegrees(angleStop) )){ //определение тормозного тока
            temp = Math.sqrt( Cur1.getZ() * Cur2.getZ() * Math.cos( Math.toRadians(180) - angleStop ) );
            CurStop.setZ(temp);
            triggerCurrent = GetDiffCurr(temp); //уставка, с учётом томрозного тока
        } else if ((-90 < Math.toDegrees(angleStop) ) && (90 > Math.toDegrees(angleStop) )){
            CurStop.setZ(0);
            triggerCurrent = I_0_dpt; //если разница углов не выходит за допустимые пределы то уставка = начальному току срабатывания
        } else{
            CurStop.setZ(0);
            triggerCurrent = I_0_dpt;
        }
        switch (phase){
            case "a":
                CurDiffphA = complex.ComplexSum(Cur1,Cur2);
                CurStopphA = CurStop;
                triggerCurrentPhA = triggerCurrent;
                break;
            case "b":
                CurDiffphB = complex.ComplexSum(Cur1,Cur2);
                CurStopphB = CurStop;
                triggerCurrentPhB = triggerCurrent;
                break;
            case "c":
                CurDiffphC = complex.ComplexSum(Cur1,Cur2);
                CurStopphC = CurStop;
                triggerCurrentPhC = triggerCurrent;
                break;
        }

    }

    public void Compare(){
        //System.out.println("I vn = "+VNrms.getPhA()*800/5 + " I sn = "+SNrms.getPhA()*2000/5 + "  I nn = "+NNrms.getPhA()*40000/5);

        //Приведение токов через базисные к относительным единицам
        z1phA.setZ(VNrms.getPhA() / IbazVN ); z1phA.setAngle(VNrms.getAnglePhA()); //3 комплексных числа - одна и та же фаза трёх сторон соответственно
        z2phA.setZ(SNrms.getPhA() / IbazSN ); z2phA.setAngle(SNrms.getAnglePhA());
        z3phA.setZ(NNrms.getPhA() / IbazNN); z3phA.setAngle(NNrms.getAnglePhA());

        z1phB.setZ(VNrms.getPhB() / IbazVN ); z1phB.setAngle(VNrms.getAnglePhB()); //3 комплексных числа - одна и та же фаза трёх сторон соответственно
        z2phB.setZ(SNrms.getPhB() / IbazSN ); z2phB.setAngle(SNrms.getAnglePhB());
        z3phB.setZ(NNrms.getPhB() / IbazNN); z3phB.setAngle(NNrms.getAnglePhB());

        z1phC.setZ(VNrms.getPhC() / IbazVN ); z1phC.setAngle(VNrms.getAnglePhC()); //3 комплексных числа - одна и та же фаза трёх сторон соответственно
        z2phC.setZ(SNrms.getPhC() / IbazSN ); z2phC.setAngle(SNrms.getAnglePhC());
        z3phC.setZ(NNrms.getPhC() / IbazNN); z3phC.setAngle(NNrms.getAnglePhC());


        Calculation(z1phA, z2phA, z3phA, "a");
        Calculation(z1phB, z2phB, z3phB, "b");
        Calculation(z1phC, z2phC, z3phC, "c");

        block.setK_Five_to_One(K_Five_to_One); //устанавливается значение уставки для органа блокировки

        block.setVNph(VNrms.getPhA());
        block.setSNph(SNrms.getPhA());
        block.setNNph(NNrms.getPhA());
        block.setVNphFive(VNrms.getPhAFive());
        block.setSNphFive(SNrms.getPhAFive());
        block.setNNphFive(NNrms.getPhAFive());
        blockA = block.Compare();

        block.setVNph(VNrms.getPhB());
        block.setSNph(SNrms.getPhB());
        block.setNNph(NNrms.getPhB());
        block.setVNphFive(VNrms.getPhBFive());
        block.setSNphFive(SNrms.getPhBFive());
        block.setNNphFive(NNrms.getPhBFive());
        blockB = block.Compare();

        block.setVNph(VNrms.getPhC());
        block.setSNph(SNrms.getPhC());
        block.setNNph(NNrms.getPhC());
        block.setVNphFive(VNrms.getPhCFive());
        block.setSNphFive(SNrms.getPhCFive());
        block.setNNphFive(NNrms.getPhCFive());
        blockC = block.Compare();

        if( (CurDiffphA.getZ()>I_cutoff_dpt) | (CurDiffphB.getZ()>I_cutoff_dpt) | (CurDiffphC.getZ()>I_cutoff_dpt) ) {//Если превысили отсечку
            outData.trip(true);
        }


        if (!( blockA | blockB | blockC )){
            if((CurDiffphA.getZ() > triggerCurrentPhA) | (CurDiffphB.getZ() > triggerCurrentPhB) | (CurDiffphC.getZ() > triggerCurrentPhC)) { //если превысили уставку
                outData.trip(true);
            }else {
                outData.trip(false);
            }
        }else{
            outData.trip(false);

        }

        switch (choose) {
            case ("a"):
                Charts.addAnalogData(3,0, CurDiffphA.getZ());
                Charts.addAnalogData(3,1, triggerCurrentPhA);
                break;
            case ("b"):
                Charts.addAnalogData(3,0, CurDiffphB.getZ());
                Charts.addAnalogData(3,1, triggerCurrentPhB);
                break;
            case ("c"):
                Charts.addAnalogData(3,0, CurDiffphC.getZ());
                Charts.addAnalogData(3,1, triggerCurrentPhC);
                break;
        }

        Charts.addAnalogData(4,0, CurStopphA.getZ());
        Charts.addAnalogData(4,1, CurStopphB.getZ());
        Charts.addAnalogData(4,2, CurStopphC.getZ());

        Charts.addAnalogData(5,0, K_Five_to_One);
        Charts.addAnalogData(5,1, VNrms.getPhAFive()/VNrms.getPhA()*100);
        Charts.addAnalogData(5,2, VNrms.getPhBFive()/VNrms.getPhB()*100);
        Charts.addAnalogData(5,3, VNrms.getPhCFive()/VNrms.getPhC()*100);

   }

}
