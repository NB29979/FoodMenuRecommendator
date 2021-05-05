package FoodModel;

public class Nutrients implements Cloneable{
    public double energy, protein, lipid,
            carbohydrate, salt, calcium, vegetables;

    void plus(Nutrients _other){
        this.energy += _other.energy;
        this.protein += _other.protein;
        this.lipid += _other.lipid;
        this.carbohydrate += _other.carbohydrate;
        this.salt += _other.salt;
        this.calcium += _other.calcium;
        this.vegetables += _other.vegetables;
    }

    @Override
    public Nutrients clone(){
        Nutrients nutrients_ = new Nutrients();
        try{
            nutrients_ = (Nutrients)super.clone();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return nutrients_;
    }
}
