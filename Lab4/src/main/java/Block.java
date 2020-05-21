public class Block {

    RMSValues rms = new RMSValues();

    ComplexNumber zeroPower = new ComplexNumber();
    private boolean activation;

    private double zeroAngle;

    public boolean isBlock( ComplexNumber zeroCurrent,  ComplexNumber zeroVoltage){
        zeroAngle = zeroVoltage.getAngle() - zeroCurrent.getAngle();
        //Charts.addAnalogData(5, 0, Math.toDegrees(zeroAngle));

        zeroPower.setAngle(zeroAngle);
        zeroPower.setZ(zeroVoltage.getZ()*zeroCurrent.getZ()*Math.cos(zeroAngle));

        Charts.addAnalogData(4,0,zeroPower.getZ());

        if(zeroPower.getZ()<0){
            activation = true;
            Charts.addAnalogData(5, 0, 100);
        } else if (zeroPower.getZ()>0){
            activation = false;
            Charts.addAnalogData(5, 0, -100);
        }
        //Charts.addAnalogData(6, 0, Math.toDegrees(zeroAngle));

        return activation;

    }

}
