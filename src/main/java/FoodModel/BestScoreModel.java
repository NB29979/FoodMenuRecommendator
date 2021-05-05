package FoodModel;

class BestScoreModel {
    double red, green, yellow;

    BestScoreModel(String _gender){
        switch (_gender){
            case FoodConst.Man:
                this.red = 2.0; this.green = 1.0; this.yellow = 7.0;
                break;
            case  FoodConst.Woman:
                this.red = 2.0; this.green = 1.0; this.yellow = 4.0;
                break;
        }
    }
}
