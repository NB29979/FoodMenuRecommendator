package FoodModel;

class FoodScoreCalculator {
    private double redLimit, greenLimit, yellowLimit, saltLimit, vegetablesLimit, costLimit;

    FoodScoreCalculator(BestScoreModel _bestModel, double _cost){
        this.redLimit = _bestModel.red;
        this.greenLimit = _bestModel.green;
        this.yellowLimit = _bestModel.yellow;
        this.saltLimit = 3.0;
        this.vegetablesLimit = 130.0;
        this.costLimit = _cost;
    }
    double score(SelectedFoods _selectedFoods){
        return Math.pow((redLimit-_selectedFoods.foodColors.red)/redLimit, 2)+
                    Math.pow((greenLimit-_selectedFoods.foodColors.green)/greenLimit, 2)+
                    Math.pow((yellowLimit-_selectedFoods.foodColors.yellow)/yellowLimit, 2)+
                    Math.pow((saltLimit-_selectedFoods.nutrients.salt)/saltLimit, 2)+
                    Math.pow((vegetablesLimit-_selectedFoods.nutrients.vegetables)/vegetablesLimit, 2)+
                    2*Math.pow((costLimit-_selectedFoods.cost)/costLimit, 2);
    }
}
