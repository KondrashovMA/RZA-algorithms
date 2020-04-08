import java.io.*;
public class InputData {
    private File comtrCfg, comtrDat;
    private BufferedReader br;
    private String line;
    private String[] lineData;
    private double k1[], k2[];

    private String choose; //выбор фазы
    private String comtrName;
    private String path = "C:\\Users\\Misha\\Desktop\\Алгоритмы РЗА\\ОпытыComtrade\\DPT\\Trans3Obm\\";

    private ValuesForCalculation VNvalCalc = new ValuesForCalculation(); //по одному объекту для записей значений токов трёх фаз
    private ValuesForCalculation SNvalCalc = new ValuesForCalculation();
    private ValuesForCalculation NNvalCalc = new ValuesForCalculation();

    private ThreePhaseFourier VNfourie = new ThreePhaseFourier(); //три объекта ФФ, который анализирует значения сразу по трём фазам
    private ThreePhaseFourier SNfourie = new ThreePhaseFourier();
    private ThreePhaseFourier NNfourie = new ThreePhaseFourier();


    private EffectiveValues VNrms = new EffectiveValues(); //для записи действующих значений, сразу три фазы
    private EffectiveValues SNrms = new EffectiveValues();
    private EffectiveValues NNrms = new EffectiveValues();
    private DPT logic = new DPT(VNrms, SNrms, NNrms);


    public InputData(String comtrName){ //constructor
        this.comtrName = comtrName;
        String cfgName = path + comtrName + ".cfg";
        String datName = path + comtrName + ".dat";
        comtrCfg = new File(cfgName);
        comtrDat = new File(datName);
        VNvalCalc.setCount(0);
        SNvalCalc.setCount(0);
        NNvalCalc.setCount(0);
    }

    public void setChoose(String choose) {this.choose = choose; }

    public void start(){
        logic.setChoose(choose);
        VNfourie.setValCalc(VNvalCalc); //ВН
        SNfourie.setValCalc(SNvalCalc); //СН
        NNfourie.setValCalc(NNvalCalc); //НН

        VNfourie.setEff(VNrms); //ВН
        SNfourie.setEff(SNrms); //СН
        NNfourie.setEff(NNrms); //НН

        try{
            br = new BufferedReader(new FileReader(comtrCfg));
            int lineNumber=0, count = 0, numberData = 100;
            while ((line = br.readLine())!=null){
                lineNumber++;
                if(lineNumber==2){
                    numberData = Integer.parseInt(line.split(",")[1].replaceAll("A",""));
                    k1 = new double [numberData];
                    k2 = new double [numberData];
                    System.out.println("Number Data: "+numberData);
                }
                if(lineNumber>2 && lineNumber<numberData+3){
                    k1[count] = Double.parseDouble(line.split(",")[5]);//каллибровка
                    k2[count] = Double.parseDouble(line.split(",")[6]);//смещение
                    count++;
                }

            }//end while
            count=0;
            br = new BufferedReader(new FileReader(comtrDat));
            double time = 0;

            while((line = br.readLine())!=null){

                lineData = line.split(",");

                //Токи на стороне ВН
                VNvalCalc.setPhA( (Double.parseDouble(lineData[2])*k1[0]+k2[0]) ); //для k[] 0 - 1 - 2 / 3 - 4 -5 / 6 -7 8
                VNvalCalc.setPhB( (Double.parseDouble(lineData[3])*k1[1]+k2[1]) );
                VNvalCalc.setPhC( (Double.parseDouble(lineData[4])*k1[2]+k2[2]) ); //для данных 2-3-4  5 -6 -7 / 8 - 9 - 10

                //Токи на стороне СН
                SNvalCalc.setPhA( (Double.parseDouble(lineData[5])*k1[3]+k2[3]) );
                SNvalCalc.setPhB( (Double.parseDouble(lineData[6])*k1[4]+k2[4]) );
                SNvalCalc.setPhC( (Double.parseDouble(lineData[7])*k1[5]+k2[5]) );

                //Токи на стороне НН
                NNvalCalc.setPhA( (Double.parseDouble(lineData[8])*k1[6]+k2[6]) );
                NNvalCalc.setPhB( (Double.parseDouble(lineData[9])*k1[7]+k2[7]) );
                NNvalCalc.setPhC( (Double.parseDouble(lineData[10])*k1[8]+k2[8]) );
                time = Double.parseDouble(lineData[1]);
                VNvalCalc.setTime(time);

                VNfourie.calculate();
                SNfourie.calculate();
                NNfourie.calculate();

                logic.Compare();

                switch (choose) {
                    case ("a"):
                        Charts.addAnalogData(0, 0, VNvalCalc.getPhA());
                        Charts.addAnalogData(0, 1, VNrms.getPhA());

                        Charts.addAnalogData(1, 0, SNvalCalc.getPhA());
                        Charts.addAnalogData(1,1, SNrms.getPhA());

                        Charts.addAnalogData(2, 0, NNvalCalc.getPhA());
                        Charts.addAnalogData(2,1, NNrms.getPhA());
                        break;
                    case ("b"):
                        Charts.addAnalogData(0, 0, VNvalCalc.getPhB());
                        Charts.addAnalogData(0, 1, VNrms.getPhB());

                        Charts.addAnalogData(1, 0, SNvalCalc.getPhB());
                        Charts.addAnalogData(1,1, SNrms.getPhB());

                        Charts.addAnalogData(2, 0, NNvalCalc.getPhB());
                        Charts.addAnalogData(2,1, NNrms.getPhB());
                        break;
                    case ("c"):
                        Charts.addAnalogData(0, 0, VNvalCalc.getPhC());
                        Charts.addAnalogData(0, 1, VNrms.getPhC());

                        Charts.addAnalogData(1, 0, SNvalCalc.getPhC());
                        Charts.addAnalogData(1,1, SNrms.getPhC());

                        Charts.addAnalogData(2, 0, NNvalCalc.getPhC());
                        Charts.addAnalogData(2,1, NNrms.getPhC());
                        break;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
