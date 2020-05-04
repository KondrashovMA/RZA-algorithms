public class Main {
    public static void main(String[] args) {
        InputData inputData = new InputData();


        Charts.createAnalogChart("U Фаза А", 0);
        Charts.createAnalogChart("U Фаза В", 1);
        Charts.createAnalogChart("U Фаза С", 2);

        //Charts.createAnalogChart("I Фаза А", 3);
        //Charts.createAnalogChart("I Фаза В", 4);
        //Charts.createAnalogChart("I Фаза С", 5);


        Charts.addSeries("фаза А (sv)",0,0);
        Charts.addSeries("фаза А (rms)",0,1);

        Charts.addSeries("фаза B (sv)",1,0);
        Charts.addSeries("фаза B (rms)",1,1);

        Charts.addSeries("фаза C (sv)",2,0);
        Charts.addSeries("фаза C (rms)",2,1);

        /*Charts.addSeries("фаза А (sv)",3,0);
        Charts.addSeries("фаза А (rms)",3,1);

        Charts.addSeries("фаза B (sv)",4,0);
        Charts.addSeries("фаза B (rms)",4,1);

        Charts.addSeries("фаза C (sv)",5,0);
        Charts.addSeries("фаза C (rms)",5,1);*/

        /*Charts.createAnalogChart("Прирощение токов по фазам", 6);

        Charts.addSeries("фаза A",6,0);
        Charts.addSeries("фаза B",6,1);
        Charts.addSeries("фаза C",6,2);


        Charts.createAnalogChart("Сопротивления по фазам", 7);

        Charts.addSeries("фаза A",7,0);
        Charts.addSeries("фаза B",7,1);
        Charts.addSeries("фаза C",7,2); */

        Charts.createAnalogChart("Прирощение токов по фазам", 3);

        Charts.addSeries("фаза A",3,0);
        Charts.addSeries("фаза B",3,1);
        Charts.addSeries("фаза C",3,2);


        Charts.createAnalogChart("Сопротивления по фазам", 4);

        Charts.addSeries("фаза A",4,0);
        Charts.addSeries("фаза B",4,1);
        Charts.addSeries("фаза C",4,2);


        ChartsXY.createAnalogChart("Попадание в зоны защиты", 0);
        ChartsXY.addSeries("Зона срабатывания 1",0,0);
        ChartsXY.addSeries("Фаза А",0,1);
        ChartsXY.addSeries("Фаза B",0,2);
        ChartsXY.addSeries("Фаза C",0,3);


        //ChartsXY.createAnalogChart("Годограф сопротивлений", 1);
        //ChartsXY.addSeries("Полные сопротивления",1,0);


        Charts.createDiscreteChart("I ступень", 0);
        Charts.createDiscreteChart("II ступень", 1);
        Charts.createDiscreteChart("III ступень", 2);



        inputData.start();
    }
}
