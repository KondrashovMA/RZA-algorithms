import java.io.*;
import java.util.Arrays;

public class InputData {
    private File comtrCfg, comtrDat; // для обработки файла
    private BufferedReader br; // буффер для обработки файла
    private String line; // строка для построчного считывания файла cfg
    private String[] lineData; // строка для построчного считывания файла dat
    private double k1[], k2[]; // запись каллибровки и смещения из cfg-файла
    private int tripPhA, tripPhB, tripPhC; // информация о том, какие ступени сработали


    private String comtrName = "KZ7";
    private String path = "C:\\Users\\Misha\\Desktop\\Алгоритмы РЗА\\Лр 3\\Опыты\\";


    private SampleValues sv = new SampleValues(); // класс для записи мнгновенных значений токов и напряжений
    private RMSValues rms = new RMSValues(); // класс для записи действующих значений токов, напряжений и углов
    FourierFilter filterPhaseA = new FourierFilter("A"); // классы фильтров Фурье для всех фаз, в фильтре обрабатывается и ток, и напряжение
    FourierFilter filterPhaseB = new FourierFilter("B");
    FourierFilter filterPhaseC = new FourierFilter("C");
    OutputSignal signal = new OutputSignal(); // класс для вывода сигналов защиты

    DistanceProtection protectPhaseA = new DistanceProtection("A",0); // классы дистанционной защиты (расчёты сопротивлений и формирование логики), пофазно
    DistanceProtection protectPhaseB = new DistanceProtection("B", 1);
    DistanceProtection protectPhaseC = new DistanceProtection("C", 2);


    public InputData(){ //constructor
        String cfgName = path + comtrName + ".cfg";
        String datName = path + comtrName + ".dat";
        comtrCfg = new File(cfgName);
        comtrDat = new File(datName);
    } //end constructor


    public void start(){
        filterPhaseA.setSv(sv);
        filterPhaseB.setSv(sv);
        filterPhaseC.setSv(sv);

        filterPhaseA.setRms(rms);
        filterPhaseB.setRms(rms);
        filterPhaseC.setRms(rms);

        protectPhaseA.graphZones();

        protectPhaseA.setRms(rms);
        protectPhaseB.setRms(rms);
        protectPhaseC.setRms(rms);
        try {
            br = new BufferedReader(new FileReader(comtrCfg));
            int lineNumber=0, count = 0, numberData = 100;
            while ((line = br.readLine())!=null){
                System.out.println(line);
                lineNumber++;
                if(lineNumber==2){
                    numberData = Integer.parseInt(line.split(",")[1].replaceAll("A",""));
                    k1 = new double [numberData];
                    k2 = new double [numberData];
                    System.out.println("Number Data: "+numberData);
                }
                if(lineNumber>2 && lineNumber<numberData+3){
                    k1[count] = Double.parseDouble(line.split(",")[5]);//каллибровка, но это не точно
                    k2[count] = Double.parseDouble(line.split(",")[6]);//смещение, но это не точно
                    //System.out.println(" count = "+count+"  k1[count] = "+k1[count]+"  k2[count] = "+k2[count]);
                    count++;
                }

            }//end while
            br = new BufferedReader(new FileReader(comtrDat));
            double time = 0;
            System.out.println(Arrays.toString(k1));
            System.out.println(Arrays.toString(k2));


            while((line = br.readLine())!=null){
                lineData = line.split(",");

                //Наяпряжение
                sv.setVoltagePhA( (Double.parseDouble(lineData[2])*k1[0]+k2[0]) ); //для k[] 0 - 1 - 2 / 3 - 4 -5 / 6 -7 8
                sv.setVoltagePhB( (Double.parseDouble(lineData[3])*k1[1]+k2[1]) );
                sv.setVoltagePhC( (Double.parseDouble(lineData[4])*k1[2]+k2[2]) ); //для данных 2-3-4  5 -6 -7 / 8 - 9 - 10

                //Токи
                sv.setCurrentPhA( (Double.parseDouble(lineData[5])*k1[3]+k2[3]) );
                sv.setCurrentPhB( (Double.parseDouble(lineData[6])*k1[4]+k2[4]) );
                sv.setCurrentPhC( (Double.parseDouble(lineData[7])*k1[5]+k2[5]) );

                time = Double.parseDouble(lineData[1]);
                sv.setTime(time);
                rms.setTime(time);

                filterPhaseA.calculate(sv.getCurrentPhA(), sv.getVoltagePhA());
                filterPhaseB.calculate(sv.getCurrentPhB(), sv.getVoltagePhB());
                filterPhaseC.calculate(sv.getCurrentPhC(), sv.getVoltagePhC());

                //tripA = protectPhaseA.logic(rms.getCurrentPhA(),rms.getVoltagePhA(), (rms.getVoltageAnglePhA() - rms.getCurrentAnglePhA()));
                //tripB = protectPhaseB.logic(rms.getCurrentPhB(),rms.getVoltagePhB(), (rms.getVoltageAnglePhB() - rms.getCurrentAnglePhB()));
                //tripC = protectPhaseC.logic(rms.getCurrentPhC(),rms.getVoltagePhC(), (rms.getVoltageAnglePhC() - rms.getCurrentAnglePhC()));

                //tripPhA = protectPhaseA.logic(rms.getCurrentPhA(),rms.getVoltagePhA(), (rms.getVoltageAnglePhA() - rms.getCurrentAnglePhA()));
                //tripPhB = protectPhaseB.logic(rms.getCurrentPhB(),rms.getVoltagePhB(), (rms.getVoltageAnglePhB() - rms.getCurrentAnglePhB()));
                //tripPhC = protectPhaseC.logic(rms.getCurrentPhC(),rms.getVoltagePhC(), (rms.getVoltageAnglePhC() - rms.getCurrentAnglePhC()));

                tripPhA = protectPhaseA.logic(rms.getCurrentPhA(),rms.getVoltagePhA(), rms.getCurrentAnglePhA(), rms.getVoltageAnglePhA() );
                tripPhB = protectPhaseB.logic(rms.getCurrentPhB(),rms.getVoltagePhB(), rms.getCurrentAnglePhB(), rms.getVoltageAnglePhB());
                tripPhC = protectPhaseC.logic(rms.getCurrentPhC(),rms.getVoltagePhC(), rms.getCurrentAnglePhC(),rms.getVoltageAnglePhC() );

                if(tripPhA==1||tripPhB==1||tripPhC==1){
                    signal.tripSignalFirst(true);
                }else {
                    signal.tripSignalFirst(false);
                }

                if(tripPhA==2||tripPhB==2||tripPhC==2){
                    signal.tripSignalSecond(true);
                }else {
                    signal.tripSignalSecond(false);
                }

                if(tripPhA==3||tripPhB==3||tripPhC==3){
                    signal.tripSignalThird(true);
                }else {
                    signal.tripSignalThird(false);
                }

                //signal.trip(tripA, tripB, tripC);


                Charts.addAnalogData(0, 0, sv.getVoltagePhA());
                Charts.addAnalogData(0, 1, rms.getVoltagePhA());

                Charts.addAnalogData(1, 0, sv.getVoltagePhB());
                Charts.addAnalogData(1, 1, rms.getVoltagePhB());

                Charts.addAnalogData(2, 0, sv.getVoltagePhC());
                Charts.addAnalogData(2, 1, rms.getVoltagePhC());
/*
                Charts.addAnalogData(3, 0, sv.getCurrentPhA());
                Charts.addAnalogData(3, 1, rms.getCurrentPhA());

                Charts.addAnalogData(4, 0, sv.getCurrentPhB());
                Charts.addAnalogData(4, 1, rms.getCurrentPhB());

                Charts.addAnalogData(5, 0, sv.getCurrentPhC());
                Charts.addAnalogData(5, 1, rms.getCurrentPhC());
 */
            }//end while

        } catch (IOException e) { //end try
            e.printStackTrace();
        }

    }//end start()

} // end class

