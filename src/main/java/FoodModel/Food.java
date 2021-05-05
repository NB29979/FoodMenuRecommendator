package FoodModel;

public class Food implements Cloneable{
    public String name;
    public int cost;
    public FoodColors foodColors;
    public Nutrients nutrients;
    public int layout;

    Food(String _name, int _cost, FoodColors _foodColors, Nutrients _nutrients, int _layout){
        this.name = _name;
        this.cost = _cost;
        this.foodColors = _foodColors;
        this.nutrients = _nutrients;
        this.layout = _layout;
    }
    Food(){}

    public String buildFoodColorString(){
        return "赤" + this.foodColors.red + "点, " +
                "緑" + this.foodColors.green + "点, " +
                "黄" + this.foodColors.yellow + "点";
    }
    @Override
    public Food clone(){
        Food food_ = new Food();
        try{
            food_ = (Food)super.clone();
            food_.foodColors = this.foodColors.clone();
            food_.nutrients = this.nutrients.clone();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return food_;
    }
}
