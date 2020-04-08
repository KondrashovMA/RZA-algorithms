import java.sql.SQLOutput;

public class ThreePhaseFourier {
    private int dots = 20; //число выборок за период
    private  double sumPhARe=0, sumPhBRe=0, sumPhCRe=0; //суммы значений по действительной оси для вычисления действующего значения первой гармоники, пофазно
    private  double sumPhAIm=0, sumPhBIm=0, sumPhCIm=0;//суммы значений по мнимой для вычисления действующего значения первой гармоники, пофазно
    private  double sumPhAReFive=0, sumPhBReFive=0, sumPhCReFive=0;//суммы значений по действительной оси для вычисления действующего значения пятой гармоники, пофазно
    private  double sumPhAImFive=0, sumPhBImFive=0, sumPhCImFive=0;//суммы значений по мнимой для вычисления действующего значения пятой гармоники, пофазно
    private double bufferPhaseARe[] = new double[dots];// массивы полученных значений, все время держит в себе прошлые значения, пофазно, для первой гармоники
    private double bufferPhaseAIm[] = new double[dots];
    private double bufferPhaseBRe[] = new double[dots];
    private double bufferPhaseBIm[] = new double[dots];
    private double bufferPhaseCRe[] = new double[dots];
    private double bufferPhaseCIm[] = new double[dots];
    private double bufferPhaseAReFive[] = new double[dots];// массивы полученных значений, все время держит в себе прошлые значения, пофазно, для пятой гармоники
    private double bufferPhaseAImFive[] = new double[dots];
    private double bufferPhaseBReFive[] = new double[dots];
    private double bufferPhaseBImFive[] = new double[dots];
    private double bufferPhaseCReFive[] = new double[dots];
    private double bufferPhaseCImFive[] = new double[dots];
    public int count;//считает, сколько уже было снято выборок. обнуляем, когда достигнем -dots выборок
    double res, ang; //вспомогательные для расчета
      double re, im;//вспомогательные для расчета
    double phaseData=0; //в зависимотси от фазы,

    double kOne = 1; //коэффициент фазы 1ой гармоники
    double kFive = 5; //коэффициент фазы 5ой гармоники
    private ValuesForCalculation valCalc; //ссылка на экземлпяр объекта, из которого берутся значения токов фаз и текущий номер в буффере
    private EffectiveValues rms;//ссылка на экземлпяр объекта, в который записываются полученные значения
    private double ArrSin[] = new double[dots]; //массивы синусов и косинусов для первой и пятой гармоник, для выполнения вычислений один раз
    private double ArrCos[] = new double[dots];
    private double ArrSinFive[] = new double[dots];
    private double ArrCosFive[] = new double[dots];

    public ThreePhaseFourier() { //пересчитывыаем значения для массива синусов/косинусов
        for(int i=0;i<dots;i++){
            ArrSin[i] = Math.sin(kOne*2*Math.PI*i/dots);
            ArrCos[i] = Math.cos(kOne*2*Math.PI*i/dots);

            ArrSinFive[i] = Math.sin(kFive*2*Math.PI*i/dots);
            ArrCosFive[i] = Math.cos(kFive*2*Math.PI*i/dots);
        }
    }

    public void calculate() {
        count = valCalc.getCount();//текущая итерация для этого набора токов

        phaseData = valCalc.getPhA();// 1ая гармоника, основная
        re = phaseData*ArrCos[count];
        im = - phaseData*ArrSin[count];
        sumPhARe = sumPhARe + re - bufferPhaseARe[count];
        sumPhAIm = sumPhAIm + im - bufferPhaseAIm[count];
        bufferPhaseARe[count] = re;
        bufferPhaseAIm[count] = im;

        res = Math.sqrt(Math.pow(sumPhARe, 2) + Math.pow(sumPhAIm, 2));
        rms.setPhA(res/dots);
        ang = Math.atan2(sumPhAIm, sumPhARe); //atan2 преобразует горизонтальные коорд. (y,x) в вид R∠Ф и вернёт Ф. простая atan ограничена от -90 до 90
        rms.setAnglePhA(ang); //по первой гармонике

        re = phaseData*ArrCosFive[count]; //5-ая гармоника
        im = - phaseData*ArrSinFive[count];
        sumPhAReFive = sumPhAReFive + re - bufferPhaseAReFive[count];
        sumPhAImFive = sumPhAImFive + im - bufferPhaseAImFive[count];
        bufferPhaseAReFive[count] = re;
        bufferPhaseAImFive[count] = im;

        res = Math.sqrt(Math.pow(sumPhAReFive, 2) + Math.pow(sumPhAImFive, 2));
        rms.setPhAFive(res/dots);
        ang = Math.atan2(sumPhAImFive, sumPhAReFive); //atan2 преобразует горизонтальные коорд. (y,x) в вид R∠Ф и вернёт Ф. простая atan ограничена от -90 до 90
        rms.setAnglePhAFive(ang); //по первой гармонике


        //************************************************************************************************************

        phaseData = valCalc.getPhB();
        re = phaseData*ArrCos[count];
        im = -phaseData*ArrSin[count];
        sumPhBRe += re - bufferPhaseBRe[count];
        sumPhBIm += im - bufferPhaseBIm[count];
        bufferPhaseBRe[count] = re;
        bufferPhaseBIm[count] = im;

        res = Math.sqrt(Math.pow(sumPhBRe, 2) + Math.pow(sumPhBIm, 2));
        rms.setPhB(res/dots);
        ang = Math.atan2(sumPhBIm, sumPhBRe);
        rms.setAnglePhB(ang);


        re = phaseData*ArrCosFive[count]; //5-ая гармоника
        im = - phaseData*ArrSinFive[count];
        sumPhBReFive = sumPhBReFive + re - bufferPhaseBReFive[count];
        sumPhBImFive = sumPhBImFive + im - bufferPhaseBImFive[count];
        bufferPhaseBReFive[count] = re;
        bufferPhaseBImFive[count] = im;

        res = Math.sqrt(Math.pow(sumPhBReFive, 2) + Math.pow(sumPhBImFive, 2));
        rms.setPhBFive(res/dots);
        ang = Math.atan2(sumPhBImFive, sumPhBReFive); //atan2 преобразует горизонтальные коорд. (y,x) в вид R∠Ф и вернёт Ф. простая atan ограничена от -90 до 90
        rms.setAnglePhBFive(ang); //по первой гармонике

    //********************************************************************************************************************


        phaseData = valCalc.getPhC();
        re = phaseData*ArrCos[count];
        im = -phaseData*ArrSin[count];
        sumPhCRe += re - bufferPhaseCRe[count];
        sumPhCIm += im - bufferPhaseCIm[count];
        bufferPhaseCRe[count] = re;
        bufferPhaseCIm[count] = im;

        res = Math.sqrt(Math.pow(sumPhCRe, 2) + Math.pow(sumPhCIm, 2));
        rms.setPhC(res/dots);
        ang = Math.atan2(sumPhCIm, sumPhCRe);
        rms.setAnglePhC(ang);


        re = phaseData*ArrCosFive[count]; //5-ая гармоника
        im = - phaseData*ArrSinFive[count];
        sumPhCReFive = sumPhCReFive + re - bufferPhaseCReFive[count];
        sumPhCImFive = sumPhCImFive + im - bufferPhaseCImFive[count];
        bufferPhaseCReFive[count] = re;
        bufferPhaseCImFive[count] = im;

        res = Math.sqrt(Math.pow(sumPhCReFive, 2) + Math.pow(sumPhCImFive, 2));
        rms.setPhCFive(res/dots);
        ang = Math.atan2(sumPhCImFive, sumPhCReFive); //atan2 преобразует горизонтальные коорд. (y,x) в вид R∠Ф и вернёт Ф. простая atan ограничена от -90 до 90
        rms.setAnglePhCFive(ang); //по первой гармонике

        count++;
        valCalc.setCount(count);
        if(count>=dots){
            valCalc.setCount(0);
        }
        rms.setTime(valCalc.getTime());
    }

    public void setValCalc(ValuesForCalculation valCalc) { this.valCalc = valCalc;}
    public ValuesForCalculation getValCalc() {return valCalc;}
    public void setEff(EffectiveValues rms) {this.rms = rms;}
    public EffectiveValues getEff() {return rms;}

}
