package FoodModel;

public class FoodColors implements Cloneable{
    public double red, green, yellow;
    public void plus(FoodColors _other){
        this.red += _other.red;
        this.green += _other.green;
        this.yellow += _other.yellow;
    }

    @Override
    public FoodColors clone(){
        FoodColors foodColors_ = new FoodColors();
        try{
            foodColors_ = (FoodColors)super.clone();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return foodColors_;
    }
}
