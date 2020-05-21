public class TZNP {

    RMSValues rms = new RMSValues();

    ComplexNumber zeroCurrent = new ComplexNumber();
    ComplexNumber zeroVoltage = new ComplexNumber();
    //токи по фазам
    ComplexNumber currentPhaseA = new ComplexNumber();
    ComplexNumber currentPhaseB = new ComplexNumber();
    ComplexNumber currentPhaseC = new ComplexNumber();
    // напряжения по фазам
    ComplexNumber voltagePhaseA = new ComplexNumber();
    ComplexNumber voltagePhaseB = new ComplexNumber();
    ComplexNumber voltagePhaseC = new ComplexNumber();
    WorkWithComplexNumber handlerComplex = new WorkWithComplexNumber();

    Block blocker = new Block();

    private double firstStep = 2.6; // уставки направленные
    private double secondStep = 1.1;
    private double thirdStep = 0.45;

    private boolean letFirstStep = false;
    private boolean letSecondStep = false;
    private boolean letThirdStep = false;

    private double firstStepTimer = 300;
    private double secondStepTimer = 800;
    private double thirdStepTimer = 1300;

    private long startFirstStepTime;
    private long startSecondStepTime;
    private long startThirdStepTime;

    ////////////////////////////////////////////////////////////////////////

    private double firstStepNonDirectional = 1.1; // уставки ненаправленные
    private double secondStepNonDirectional = 0.66;

    private double firstStepNonDirectionalTimer = 1200;
    private double secondStepNonDirectionalTimer = 1700;

    private boolean letFirstStepNonDirectional = false;
    private boolean letSecondStepNonDirectional = false;

    private long startFirstStepNonDirectionalTime;
    private long startSecondStepNonDirectionalTime;

    /////////////////////////////////////////////////////////////////////

    private long nowTime = 0;

    boolean isBlock = true;


    public void logic(){
        currentPhaseA.setZ(rms.getCurrentPhA()); // получаем действующие значения токов всех фаз
        currentPhaseB.setZ(rms.getCurrentPhB());
        currentPhaseC.setZ(rms.getCurrentPhC());

        currentPhaseA.setAngle(rms.getCurrentAnglePhA()); // получаем углы токов всех фаз
        currentPhaseB.setAngle(rms.getCurrentAnglePhB());
        currentPhaseC.setAngle(rms.getCurrentAnglePhC());

        voltagePhaseA.setZ(rms.getVoltagePhA()); // действующие значения напряжений всех фаз
        voltagePhaseB.setZ(rms.getVoltagePhB());
        voltagePhaseC.setZ(rms.getVoltagePhC());

        voltagePhaseA.setAngle(rms.getVoltageAnglePhA());
        voltagePhaseB.setAngle(rms.getVoltageAnglePhB());
        voltagePhaseC.setAngle(rms.getVoltageAnglePhC());

        zeroVoltage = handlerComplex.ComplexThreeSum(voltagePhaseA, voltagePhaseB, voltagePhaseC);
        //zeroVoltage.setZ(zeroVoltage.getZ()/3);

        zeroCurrent = handlerComplex.ComplexThreeSum(currentPhaseA, currentPhaseB, currentPhaseC);
        //zeroCurrent.setZ(zeroCurrent.getZ()/3);

        //Charts.addAnalogData(3, 0, zeroVoltage.getZ());
        Charts.addAnalogData(3, 0, zeroCurrent.getZ());

        isBlock = blocker.isBlock(zeroCurrent, zeroVoltage);

        nowTime = System.currentTimeMillis();
        Charts.addAnalogData(3, 1, firstStep);
        Charts.addAnalogData(3, 2, secondStep);
        Charts.addAnalogData(3, 3, thirdStep);

        if(!isBlock){ // сигнал о разрешении направленной защиты
            if(zeroCurrent.getZ() > firstStep){
                if(!letFirstStep){
                    startFirstStepTime = System.currentTimeMillis();
                    letFirstStep = true;
                }else{
                if(nowTime - startFirstStepTime > firstStepTimer){
                        Charts.addDiscreteData(0, true);
                    }
                }
            } else{
                letFirstStep = false;
            }
            if(zeroCurrent.getZ() > secondStep){
                if(!letSecondStep){
                    startSecondStepTime = System.currentTimeMillis();
                    letSecondStep = true;
                }else{
                    if(nowTime - startSecondStepTime > secondStepTimer){
                        Charts.addDiscreteData(1, true);
                    }
                }
            } else{
                letSecondStep = false;
            }
            if(zeroCurrent.getZ() > thirdStep){
                if(!letThirdStep){
                    startThirdStepTime = System.currentTimeMillis();
                    letThirdStep = true;
                }else{
                    if(nowTime - startThirdStepTime > thirdStepTimer){
                        Charts.addDiscreteData(2, true);
                    }
                }
            } else{
                letThirdStep = false;
            }
        }

        if(zeroCurrent.getZ() > firstStepNonDirectional) {
            if(!letFirstStepNonDirectional){
                startFirstStepNonDirectionalTime = System.currentTimeMillis();
                letFirstStepNonDirectional = true;
            }else{
                if(nowTime - startFirstStepNonDirectionalTime > firstStepNonDirectionalTimer){
                    Charts.addDiscreteData(3, true);
                }
            }
        }else {
            letFirstStepNonDirectional = false;
        }
        if(zeroCurrent.getZ() > secondStepNonDirectional) {
            if(!letSecondStepNonDirectional){
                startSecondStepNonDirectionalTime = System.currentTimeMillis();
                letSecondStepNonDirectional = true;
            }else{
                if(nowTime - startSecondStepNonDirectionalTime > secondStepNonDirectionalTimer){
                    Charts.addDiscreteData(4, true);
                }
            }
        }else{
            letSecondStepNonDirectional = false;
        }

    }



    public RMSValues getRms() {
        return rms;
    }

    public void setRms(RMSValues rms) {
        this.rms = rms;
    }
}
