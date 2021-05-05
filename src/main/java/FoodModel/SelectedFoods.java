package FoodModel;

import java.util.ArrayList;
import java.util.List;

public class SelectedFoods implements Cloneable {
    int cost;
    FoodColors foodColors;
    Nutrients nutrients;
    public List<String> selectedFoodNames;

    SelectedFoods(){
        cost = 0;
        foodColors = new FoodColors();
        nutrients = new Nutrients();
        selectedFoodNames = new ArrayList<>();
    }
    SelectedFoods updateAttributes(Food _food){
        if(!_food.name.equals(FoodConst.Dummy)) {
            this.cost += _food.cost;
            this.foodColors.plus(_food.foodColors);
            this.nutrients.plus(_food.nutrients);
            this.selectedFoodNames.add(_food.name);
        }
        return this;
    }
    public Food convert2Food(){
        return new Food(String.join(",", selectedFoodNames), this.cost,
                this.foodColors.clone(), this.nutrients.clone(), 1111);
    }

    public String buildRecommendMenuString(){
        return String.join(",", selectedFoodNames) + "\\" + cost + "円" + "\\はどうですか？";
    }

    @Override
    public SelectedFoods clone(){
        SelectedFoods selectedFoods_ = new SelectedFoods();
        try{
            selectedFoods_ = (SelectedFoods)super.clone();
            selectedFoods_.foodColors = this.foodColors.clone();
            selectedFoods_.nutrients = this.nutrients.clone();
            selectedFoods_.selectedFoodNames = new ArrayList(this.selectedFoodNames);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return selectedFoods_;
    }
}
