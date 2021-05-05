package UserModel;

import FoodModel.*;

import java.util.*;

public class BeliefState {
    private final String dialogId;
    private String genre;
    private String gender;
    private List<String> dialogTags = new ArrayList<>();
    private SlotFiller slotFiller = new SlotFiller();
    private QuestionGenerator questionGenerator = new QuestionGenerator();

    private FoodInfoCommentator foodInfoCommentator = new FoodInfoCommentator();
    private MenuRecommendationGenerator recommendationGenerator;
    private List<SelectedFoods> recommendMenuList;
    private SelectedFoods recommendMenu;
    private int currentBest;
    private double targetCost;

    BeliefState(String _dialogId){
        this.dialogId = _dialogId;
        currentBest = 0;
        targetCost = UserModelConst.NormalCost;
        recommendationGenerator = new MenuRecommendationGenerator();
    }

    public void init(String _genre){
        setGenre(_genre);
        questionGenerator.init(_genre);
        ArrayList<String> questionTags_ = questionGenerator.getQuestionTags();
        slotFiller.init(questionTags_);
    }
    public void updateSlot(HashMap<String, String> _answers){
        slotFiller.updateSlot(_answers);
        slotFiller.debug();
        questionGenerator.removeQuestion();
    }
    public boolean messageHasPrevQuestionTag(){
        return dialogTags.stream()
                .anyMatch(tag->questionGenerator.getCurrentQuestionTag().contains(tag));
    }
    public boolean dialogQuestionsIsEmpty(){
        return questionGenerator.dialogQuestionsIsEmpty();
    }
    public MessageMetadata generateQuestion(){
        MessageMetadata questionOutput_ = questionGenerator.generateQuestion(this.dialogId);
        this.dialogTags = questionOutput_.dialogTags;
        return questionOutput_;
    }
    public String generateDescription(int _methodType, String _foodName){
        if(_foodName != null) {
            Food food_ = FoodInfo.foodList.stream()
                    .filter(food -> food.name.equals(_foodName)).findFirst().orElse(null);
            foodInfoCommentator.pushFood(food_);
        }
        return generateDescription(_methodType);
    }
    public String generateDescription(int _methodType){
        switch(_methodType){
            case UserModelConst.AnswerType.EnergyInfo:
                return foodInfoCommentator.buildFoodEnergyInfo();
            case UserModelConst.AnswerType.CostInfo:
                return foodInfoCommentator.buildFoodCostInfo();
            case UserModelConst.AnswerType.NutritionalBalanceInfo:
                return foodInfoCommentator.buildFoodNutritinoalBalanceInfo();
        }
        return "すみません，よくわかりません";
    }
    public void initRecommendationGenerator(){
        recommendationGenerator.init(gender, slotFiller.getCommonGround().get("FavoriteFood"), targetCost);
    }
    public SelectedFoods provideMenu(){
        currentBest = 0;
        recommendMenuList = recommendationGenerator.provideMenu();
        recommendMenu = recommendMenuList.get(currentBest);
        foodInfoCommentator.pushFood(recommendMenu.convert2Food());

        return recommendMenu;
    }
    public void removeFood(String _foodName){
        recommendationGenerator.removeFoodNameFromMatrix(_foodName);
    }
    public SelectedFoods selectNextRecommendation(){
        // 現状副菜で2つのダブりが存在
        currentBest = currentBest+2 < recommendMenuList.size() ? currentBest+2 : currentBest;
        recommendMenu = recommendMenuList.get(currentBest);
        foodInfoCommentator.pushFood(recommendMenu.convert2Food());

        return recommendMenu;
    }
    public void reduceCost(){
        targetCost = UserModelConst.ReducedCost;
    }

    public void setGender(){gender = slotFiller.getCommonGround().get("Gender").get(0);}
    public void setGenre(String _genre){ this.genre = _genre; }

    public String getId(){ return this.dialogId; }
    public String getGenre(){return this.genre;}
    public List<String> getDialogTags(){ return dialogTags; }
}
