import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String comtrName = "";
        Scanner k = new Scanner(System.in);
        System.out.println("Какой опыт выполняется? :\n" +
                "1 - Внутреннее КЗ фаз A и B \n" +
                "2 - Внутреннее КЗ фазы C \n" +
                "3 - Внешнее трёхфазное КЗ \n" +
                "4 - Включение трансформатора \n");
        int num = k.nextInt();//считывает первое число и присваивает значение в number1
        System.out.println("Для какой фазы? Ввести A,B или C на латинице");
        String choose = k.next();//считывает второе число и присваивает значение в number2
        choose = choose.trim();
        choose = choose.toLowerCase();
        System.out.println(num);
        k.close();

        switch (num){
            case 1:
                comtrName = "Trans3ObmVnutAB";
                break;
            case 2:
                comtrName = "Trans3ObmVnutC";
                break;
            case 3:
                comtrName = "Trans3ObmVneshABC";
                break;
            case 4:
                comtrName = "Trans3ObmVkl";
                break;
            default:
                System.out.println("нужно ввести число от 1 до 4");
                System.exit(0);
        }
        InputData input = new InputData(comtrName);

        switch (choose){
            case("a"):
                Charts.createAnalogChart("Фаза А ВН", 0);
                Charts.createAnalogChart("Фаза А СН", 1);
                Charts.createAnalogChart("Фаза А НН", 2);
                Charts.createAnalogChart("Диф. ток фазы", 3);

                Charts.addSeries("фаза А ВН (sv)",0,0);
                Charts.addSeries("Фурье фза А (rms)",0,1);

                Charts.addSeries("фаза А СН (sv)",1,0);
                Charts.addSeries("Фурье фза А (rms)",1,1);

                Charts.addSeries("фаза А НН (sv)",2,0);
                Charts.addSeries("Фурье фза А (rms)",2,1);

                Charts.addSeries("Диф. ток по фазе А ",3,0);
                Charts.addSeries("Уставка A",3,1);
                break;

            case ("b"):
                Charts.createAnalogChart("Фаза B ВН", 0);
                Charts.createAnalogChart("Фаза B СН", 1);
                Charts.createAnalogChart("Фаза B НН", 2);
                Charts.createAnalogChart("Диф. ток фазы", 3);

                Charts.addSeries("фаза B ВН (sv)",0,0);
                Charts.addSeries("Фурье фза B (rms)",0,1);

                Charts.addSeries("фаза B СН (sv)",1,0);
                Charts.addSeries("Фурье фза B (rms)",1,1);

                Charts.addSeries("фаза И НН (sv)",2,0);
                Charts.addSeries("Фурье фза А (rms)",2,1);

                Charts.addSeries("Диф. ток по фазе B ",3,0);
                Charts.addSeries("Уставка B",3,1);
                break;

            case ("c"):
                Charts.createAnalogChart("Фаза C ВН", 0);
                Charts.createAnalogChart("Фаза C СН", 1);
                Charts.createAnalogChart("Фаза C НН", 2);
                Charts.createAnalogChart("Диф. ток фазы", 3);


                Charts.addSeries("фаза C ВН (sv)",0,0);
                Charts.addSeries("Фурье фза C (rms)",0,1);

                Charts.addSeries("фаза C СН (sv)",1,0);
                Charts.addSeries("Фурье фза C (rms)",1,1);

                Charts.addSeries("фаза C НН (sv)",2,0);
                Charts.addSeries("Фурье фза C (rms)",2,1);

                Charts.addSeries("Диф. ток по фазе C ",3,0);
                Charts.addSeries("Уставка C",3,1);
                break;
            default:
                System.out.println("нет такой команды");
                System.exit(0);
           }

        input.setChoose(choose);

        Charts.createAnalogChart("Тормозной ток ", 4);
        Charts.createAnalogChart("Блокировка ", 5);

        Charts.addSeries("Тормозной ток ф.А",4,0);
        Charts.addSeries("Тормозной ток ф.B",4,1);
        Charts.addSeries("Тормозной ток ф.C",4,2);

        Charts.addSeries("Уставка по блокировке, %",5,0);
        Charts.addSeries("Отношение пятой гармоники к первой ф А",5,1);
        Charts.addSeries("Отношение пятой гармоники к первой ф B",5,2);
        Charts.addSeries("Отношение пятой гармоники к первой ф C",5,3);
        Charts.createDiscreteChart("Trip", 0);

        input.start();
    }
}
