public class DistanceProtection {
    public RMSValues getRms() {
        return rms;
    }
    public void setRms(RMSValues rms) {
        this.rms = rms;
    }

    RMSValues rms = new RMSValues();
    private double impedance; // вычисляемое сопротивление
    private double solveTimer = 6000; // время, на которое разрешается работа дистанционной защиты
    private double firstStepTimer; // начало отсчёта для 1 ступени
    private double secondSteptimer; // начало отсчёта для 2 ступени
    private double thirdStepTimer; // начало отсчёта для 3 ступени

    private double firstStepTimeSet = 150; // уставка по времени, 1 ступень, всё в мс
    private double secondStepTimeSet = firstStepTimeSet + 350; // уставка по времени, 2 ступень
    private double thirdStepTimeSet = secondStepTimeSet + 350; // уставка по времени, 3 ступень

    private boolean runFirstStep = true; // проверка того, что защита запускается в первый раз
    private boolean runSecondStep = true; // нужно для задания начального времени для отсчёта
    private boolean runThirdStep = true; // для каждой ступени

    private double firstStepImpedance = 76.93; // значение модуля Z сопротивления уставки 1 ступени
    private double lineAngle = Math.toRadians(81.67); // угол линии (см. расчёт уставок)
    private double secondStepImpedance = 113; // уставка 2 ступени
    private double thirdStepImpedance = 217.2; // уставка 3 ступени

    private double xImp, yImp; // в xImp - активная составляющая сопротивления, в yImp - реактивная

    private int counter = 0; // отсчёт вводится для того, чтобы сравнивать не соседние значения токов по времени
    private  double delta;  // приращение тока
    private double timerStart = 0; // начало отсчёта для отсечек и для таймера,  в случае, если произошло КЗ
    private double lastCurrent = 0; // предыдущее значение тока
    private boolean protectOnWork = false; // разрешает работу защиты, если произошло КЗ

    private double yDoub; //
    private double xDoub;
    private int x1,x2;

    private double nowTime; // переменная для записи текущего времени
    private int numberOfPlot; // номер графика, задаётся в конструкторе
    private int zone;  // номер графика XY. задаётся в конструкторе

    private String phase;

    public DistanceProtection(String phase, int zone) {
        this.phase = phase;
        this.zone = zone;
        switch (phase){
            case "A":
                this.numberOfPlot = 0;
                break;
            case "B":
                this.numberOfPlot = 1;
                break;
            case "C":
                this.numberOfPlot = 2;
                break;
        }

    }

    public void graphZones(){
        xDoub = -57.58*100;
        x2 = (int) xDoub;
        for(int i = 0; i > x2 ;i--){
            xDoub = (double) i/100;
            yDoub = (double) (xDoub)*(-3.73);
            ChartsXY.addAnalogData(0,0,xDoub,yDoub);
        }

        addGraph(-20.39, 30.04,48.43, firstStepImpedance);
        addGraph(-29.98, 44.8,71.28, secondStepImpedance);
        addGraph(-57.58, 85.96,136.9, thirdStepImpedance);
    }
    public void addGraph(double L1, double R1, double R2, double Y1){
        yDoub = Y1*Math.sin(lineAngle);
        xDoub = L1*100;
        x1 = (int) xDoub;
        xDoub = R2*100;
        x2 = (int) xDoub;
        for(int i = x1; i < x2 ;i++){
            xDoub = (double) i/100;
            ChartsXY.addAnalogData(0,zone,xDoub,yDoub);
        }
        yDoub = 0;
        xDoub = R1*100;
        x2 = (int) xDoub;
        for(int i = 0; i < x2 ;i++){
            xDoub = (double) i/100;
            ChartsXY.addAnalogData(0,zone,xDoub,0);
        }

        xDoub = R1*100;
        x1 = (int) xDoub;
        xDoub = R2*100;
        x2 = (int) xDoub;
        for(int i = x1; i < x2 ;i++){
            xDoub = (double) i/100;
            yDoub = (double) (-R1 + xDoub)*4.22;
            ChartsXY.addAnalogData(0,zone,xDoub,yDoub);
        }
        /*xDoub = L1*100;
        x2 = (int) xDoub;
        for(int i = 0; i > x2 ;i--){
            xDoub = (double) i/100;
            yDoub = (double) (xDoub)*(-3.73);
            ChartsXY.addAnalogData(0,0,xDoub,yDoub);
        }*/
    }
    private double leftSide(double x){ // вычсляет значение y по x для левой линии
        return -x*3.73; //tg(f слева)
    }
    private double rightSide(double R, double x){ // вычисляет значение y по х для правой лиии
        return (-R + x)*4.22; //tg(f справа)
    }

    private boolean dotInArea(double x, double y, double bot, double top, double R){
        return (y < top && y > bot && y > leftSide(x) && y > rightSide(R, x)); // проверяет попадание в зону защиты
    }

    public int logic(double current, double voltage, double angleCur, double angleVolt){
        double angle = angleVolt - angleCur;
        impedance = voltage/current;

        xImp = impedance*Math.cos(angle);
        yImp = impedance*Math.sin(angle);

        Charts.addAnalogData(4,numberOfPlot, impedance);

        ChartsXY.addAnalogData(0,(zone+1),xImp,yImp);
        if (counter==5){
            delta = (current - lastCurrent)/(counter*0.00025);
            //Charts.addAnalogData(6, numberOfPlot, delta);
            Charts.addAnalogData(3, numberOfPlot, delta);
            counter=0;
            if (Math.abs(delta)>50 ){
                timerStart = System.currentTimeMillis();
                protectOnWork = true;
            }
        }else{
            //Charts.addAnalogData(6, numberOfPlot, 0);
            Charts.addAnalogData(3, numberOfPlot, 0);
        }
        if (counter==0){
            lastCurrent = current;
        }
        counter++;

        if(protectOnWork) {
            nowTime = System.currentTimeMillis();

            if (nowTime - timerStart >= solveTimer){
                protectOnWork = false;
            }

            if(dotInArea(xImp, yImp, 0, firstStepImpedance*Math.sin(lineAngle), 30.04)){ // задаём активное и реактивное сопротивление, нижнюю границу, верхнюю границу, R макс (см. расчёт уставок)
                if(runFirstStep){
                    firstStepTimer = nowTime;
                    runFirstStep = false;
                }
                if(nowTime - firstStepTimer > firstStepTimeSet){
                    protectOnWork = true;
                    return 1;
                }
            } else{
                runFirstStep = true;
            }

            if(dotInArea(xImp, yImp, 0, secondStepImpedance*Math.sin(lineAngle), 44.8)){
                if(runSecondStep){
                    secondSteptimer = nowTime;
                    runSecondStep = false;
                }
                if(nowTime - secondSteptimer > secondStepTimeSet){
                    System.out.println((nowTime - secondSteptimer)+" "+protectOnWork);
                    protectOnWork = true;
                    return 2;
                }
            }else{
                runSecondStep = true;
            }

            if(dotInArea(xImp, yImp, 0, thirdStepImpedance*Math.sin(lineAngle), 85.96)){
                if(runThirdStep){
                    thirdStepTimer = nowTime;
                    runThirdStep = false;
                }
                if(nowTime - thirdStepTimer  > thirdStepTimeSet) {
                    protectOnWork = true;
                    return 3;
                }
            }else{
                runThirdStep = true;
            }
        }

        //return false;
        return 0;
    }
}
