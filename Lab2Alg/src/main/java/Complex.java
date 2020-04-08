public class Complex {

    public double getA(ComplexNumber z){ //получить a из z = a + i*b
        return Math.abs(z.getZ())*Math.cos(z.getAngle());
    }

    public double getB(ComplexNumber z){ //получить a из z = a + i*b
        return Math.abs(z.getZ())*Math.sin(z.getAngle());
    }

    public double ComplexGetModule(double a, double b){
        return Math.sqrt(a*a + b*b);
    }
    public double ComplexGetAngle(double a, double b){
        return Math.atan2(b,a);
    }

    public ComplexNumber ComplexSum(ComplexNumber z1, ComplexNumber z2){
        double sumA, sumB,mag,ang;
        ComplexNumber zRes = new ComplexNumber();
        sumA = getA(z1) + getA(z2);
        sumB = getB(z1) + getB(z2);
        mag = ComplexGetModule(sumA,sumB);
        ang = ComplexGetAngle(sumA,sumB);
        zRes.setZ(mag);
        zRes.setAngle(ang);
        return zRes;
    }

    public ComplexNumber ComplexThreeSum(ComplexNumber z1, ComplexNumber z2, ComplexNumber z3){
        double sumA, sumB,mag,ang;
        ComplexNumber zRes = new ComplexNumber();
        sumA = getA(z1) + getA(z2) + getA(z3);
        sumB = getB(z1) + getB(z2) + getB(z3);

        mag = ComplexGetModule(sumA,sumB);
        ang = ComplexGetAngle(sumA,sumB);
        zRes.setZ(mag);
        zRes.setAngle(ang);
        return zRes;
    }
    public ComplexNumber ComplexMinus(ComplexNumber z1, ComplexNumber z2){
        /**
         * z1 - z2
         */
        double sumA, sumB,mag,ang;
        ComplexNumber zRes = new ComplexNumber();
        sumA = getA(z1) - getA(z2);
        sumB = getB(z1) - getB(z2);
        mag = ComplexGetModule(sumA,sumB);
        ang = ComplexGetAngle(sumA,sumB);
        zRes.setZ(mag);
        zRes.setAngle(ang);
        return zRes;
    }
    public ComplexNumber MaxFromThreeComplex(ComplexNumber z1, ComplexNumber z2, ComplexNumber z3){
        if( z1.getZ() > Math.max(z2.getZ(),z3.getZ()) ){ //если наибольший модуль у z1 то вернём его
            return z1;
        }else if(z2.getZ()>z3.getZ()) {
            return z2;
        }else{return z3;}
    }

}
