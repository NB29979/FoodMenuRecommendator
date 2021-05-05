package FoodModel;

import java.util.*;
import java.util.stream.Collectors;

public class FoodInfoCommentator {
    private Deque<List<Food>> focusedFoods = new ArrayDeque<>();

    public String buildFoodEnergyInfo(){
        List<String> foodEnergies_ =
                focusedFoods.getFirst().stream()
                        .map(food->food.name+":"+food.nutrients.energy+"kcal")
                        .collect(Collectors.toList());
        return buildString(FoodConst.FoodAttr.Energy, foodEnergies_);
    }
    public String buildFoodCostInfo(){
        List<String> foodCosts_ =
                focusedFoods.getFirst().stream()
                        .map(food->food.name+":"+food.cost+"円")
                        .collect(Collectors.toList());
        return buildString(FoodConst.FoodAttr.Cost, foodCosts_);
    }
    public String buildFoodNutritinoalBalanceInfo(){
        List<String> foodBalances_ =
                focusedFoods.getFirst().stream()
                        .map(food->food.name+":\n"+food.buildFoodColorString())
                        .collect(Collectors.toList());
        return buildString(FoodConst.FoodAttr.Balance, foodBalances_);
    }
    public void pushFood(Food _food){
        focusedFoods.push(Arrays.asList(_food));
    }
    private String buildString(String _genre, List<String> _foodParams){
        String str_;
        str_ = _genre+"は\n";
        str_ += String.join("\n", _foodParams);
        str_ += "\nです";
        return str_;
    }
}
