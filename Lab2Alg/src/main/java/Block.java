public class Block {
    private double VNph;
    private double SNph;
    private double NNph;

    private double VNphFive;
    private double SNphFive;
    private double NNphFive;
    private double K_Five_to_One;

    public void setK_Five_to_One(double k_Five_to_One) {
        K_Five_to_One = k_Five_to_One;
    }

    public void setVNph(double VNph) {
        this.VNph = VNph;
    }

    public void setSNph(double SNph) {
        this.SNph = SNph;
    }

    public void setNNph(double NNph) {
        this.NNph = NNph;
    }

    public void setVNphFive(double VNphFive) {
        this.VNphFive = VNphFive;
    }

    public void setSNphFive(double SNphFive) {
        this.SNphFive = SNphFive;
    }

    public void setNNphFive(double NNphFive) {
        this.NNphFive = NNphFive;
    }

    public boolean Compare(){
        return (VNphFive / VNph * 100 > K_Five_to_One) | (SNphFive / SNph * 100 > K_Five_to_One) | (NNphFive / NNph * 100 > K_Five_to_One);
    }
}
