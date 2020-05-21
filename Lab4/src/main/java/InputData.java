import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class InputData {
    private File comtrCfg, comtrDat; // для обработки файла
    private BufferedReader br; // буффер для обработки файла
    private String line; // строка для построчного считывания файла cfg
    private String[] lineData; // строка для построчного считывания файла dat
    private double k1[], k2[]; // запись каллибровки и смещения из cfg-файла


    private String comtrName = "KZ7";
    private String path = "C:\\Users\\Misha\\Desktop\\Алгоритмы РЗА\\Лр 4\\Опыты\\";


    private SampleValues sv = new SampleValues(); // класс для записи мнгновенных значений токов и напряжений
    private RMSValues rms = new RMSValues(); // класс для записи действующих значений токов, напряжений и углов
    FourierFilter filterPhaseA = new FourierFilter("A"); // классы фильтров Фурье для всех фаз, в фильтре обрабатывается и ток, и напряжение
    FourierFilter filterPhaseB = new FourierFilter("B");
    FourierFilter filterPhaseC = new FourierFilter("C");

    TZNP protection = new TZNP();



    public InputData(){
        String cfgName = path + comtrName + ".cfg";
        String datName = path + comtrName + ".dat";
        comtrCfg = new File(cfgName);
        comtrDat = new File(datName);
    }


    public void start(){
        filterPhaseA.setSv(sv);
        filterPhaseB.setSv(sv);
        filterPhaseC.setSv(sv);

        filterPhaseA.setRms(rms);
        filterPhaseB.setRms(rms);
        filterPhaseC.setRms(rms);

        protection.setRms(rms);

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

                protection.logic();


                //signal.trip(tripA, tripB, tripC);

/*
                Charts.addAnalogData(0, 0, sv.getVoltagePhA());
                Charts.addAnalogData(0, 1, rms.getVoltagePhA());

                Charts.addAnalogData(1, 0, sv.getVoltagePhB());
                Charts.addAnalogData(1, 1, rms.getVoltagePhB());

                Charts.addAnalogData(2, 0, sv.getVoltagePhC());
                Charts.addAnalogData(2, 1, rms.getVoltagePhC());
*/

                Charts.addAnalogData(0, 0, sv.getCurrentPhA());
                Charts.addAnalogData(0, 1, rms.getCurrentPhA());

                Charts.addAnalogData(1, 0, sv.getCurrentPhB());
                Charts.addAnalogData(1, 1, rms.getCurrentPhB());

                Charts.addAnalogData(2, 0, sv.getCurrentPhC());
                Charts.addAnalogData(2, 1, rms.getCurrentPhC());

            }//end while

        } catch (IOException e) { //end try
            e.printStackTrace();
        }

    }//end start()

} // end class

