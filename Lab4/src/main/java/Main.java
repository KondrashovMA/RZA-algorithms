public class Main {
    public static void main(String[] args) {
        InputData inputData = new InputData();

        // токи
        Charts.createAnalogChart("I Фаза А", 0);
        Charts.createAnalogChart("I Фаза В", 1);
        Charts.createAnalogChart("I Фаза С", 2);

        Charts.addSeries("фаза А (sv)",0,0);
        Charts.addSeries("фаза А (rms)",0,1);

        Charts.addSeries("фаза B (sv)",1,0);
        Charts.addSeries("фаза B (rms)",1,1);

        Charts.addSeries("фаза C (sv)",2,0);
        Charts.addSeries("фаза C (rms)",2,1);

        // 0-ые последовательности
        //Charts.createAnalogChart("Напряжение 0 посл.", 3);
        //Charts.addSeries("U0, А",3,0);

        Charts.createAnalogChart("Ток 0 посл.", 3);
        Charts.addSeries("Ток, А",3,0);
        Charts.addSeries("1 ступень",3,1);
        Charts.addSeries("2 ступень",3,2);
        Charts.addSeries("3 ступень",3,3);

        // мощность

        //Charts.createAnalogChart("Угол между U0 и I0", 5);
        //Charts.addSeries("Угол, град.",5,0);

        Charts.createAnalogChart("P0", 4);
        Charts.addSeries("P0",4,0);

        // блокировка
        Charts.createAnalogChart("Блокировка", 5);
        Charts.addSeries("Блок",5,0);

        // угол
        //Charts.createAnalogChart("Угол", 6);
        //Charts.addSeries("Угол",6,0);



        // дискретные сигналы


        Charts.createDiscreteChart("1", 0);
        Charts.createDiscreteChart("2", 1);
        Charts.createDiscreteChart("3", 2);

        Charts.createDiscreteChart("1'", 3);
        Charts.createDiscreteChart("2'", 4);

        inputData.start();
    }
}
