import FoodModel.FoodInfo;
import UserModel.BeliefState;
import UserModel.MessageMetadata;
import UserModel.UserModelConst;

import java.util.*;
import java.util.stream.Collectors;

class InputAnalyzer {
    Deque<String> variableQueue;
    Deque<String> methodQueue;
    private UserAnswerHolder answerHolder;

    InputAnalyzer(){
        variableQueue = new ArrayDeque<>();
        methodQueue = new ArrayDeque<>();
        answerHolder = new UserAnswerHolder();
    }
    void analyze(String _genre, MessageMetadata _userMessageMetadata){
        List<String> dialogTags_ = _userMessageMetadata.dialogTags;
        String userInput_ = _userMessageMetadata.message;
        if(!dialogTags_.isEmpty()) {
            switch(_genre){
                case Const.DialogGenre.Setup:
                    if (dialogTags_.contains(Const.QuestionGenre.Gender))
                        variableQueue.addAll(extractWords(userInput_, answerHolder.genders));
                    else if (dialogTags_.contains(Const.QuestionGenre.Feeling))
                        variableQueue.add(userInput_);
                    break;
                case Const.DialogGenre.Food:
                    variableQueue.addAll(extractWords(userInput_, answerHolder.foodNames));
                    methodQueue.addAll(extractWords(userInput_, answerHolder.info));
                    break;
                case Const.DialogGenre.ConfirmMenu:
                    variableQueue.addAll(extractWords(userInput_, answerHolder.foodNames));
                    methodQueue.addAll(extractWords(userInput_, answerHolder.methods));
                    methodQueue.addAll(extractWords(userInput_, answerHolder.info));
                    break;
            }
            if(methodQueue.size()==0) methodQueue.add("NOP");
        }
        if(variableQueue.size()!=0)
//            System.out.println("[variable] "+variableQueue.getFirst());
            ;
        if(methodQueue.size()!=0)
//            System.out.println("[method] "+methodQueue.getFirst());
            ;
    }
    void clear(){
        variableQueue.clear();
        methodQueue.clear();
    }
    private List<String> extractWords(String _input, List<String> _wordList){
        List<String> words_ = new ArrayList<>(_wordList);
        words_.sort(Comparator.comparingInt(_input::indexOf));
        return words_.stream()
                .filter(_input::contains)
                .collect(Collectors.toList());
    }
    int detectMethodType(){
        return answerHolder.getMethodType(methodQueue.getFirst());
    }

    HashMap<String, String> convertMessage2Map(BeliefState _beliefState, String _userMessage){
        List<String> dialogTags_ = _beliefState.getDialogTags();
        HashMap<String, String> targetMessages_ = new HashMap<>();

        switch (_beliefState.getGenre()) {
            case Const.DialogGenre.Setup:
                if (dialogTags_.contains(Const.QuestionGenre.Gender))
                    dialogTags_.forEach(tag -> targetMessages_.put(tag, variableQueue.getFirst()));
                else if (dialogTags_.contains(Const.QuestionGenre.Feeling))
                    dialogTags_.forEach(tag -> targetMessages_.put(tag, _userMessage));
                break;
            case Const.DialogGenre.Food:
            case Const.DialogGenre.ConfirmMenu:
                dialogTags_.forEach(tag -> targetMessages_.put(tag, variableQueue.getFirst()));
                break;
        }
        return targetMessages_;
    }
    class UserAnswerHolder {
        List<String> foodNames;
        List<String> methods;
        List<String> info;
        private List<String> genders = Arrays.asList("男", "女");
        private List<String> yAnswers = Arrays.asList("いい", "良い", "そうする", "さすが", "なるほど", "それにする", "それにします", "ありがとう", "さっき", "先ほど", "はい");
        private List<String> nAnswers = Arrays.asList("いや",  "以外", "嫌い", "きらい", "いらない", "要らない", "不要", "苦手");
        private List<String> addAnswers = Arrays.asList("加えて", "追加して", "含め", "食べたい", "ほしい", "変更");
        private List<String> reduceCostAnswers = Arrays.asList("安く", "安い", "高く", "高い");
        private List<String> otherAnswers = Arrays.asList("他", "ほか");
        private List<String> energyWords = Arrays.asList("カロリー", "エネルギー");
        private List<String> costWords = Arrays.asList("値段","いくら");
        private List<String> nutritionalBalance = Arrays.asList("栄養バランス", "バランス");

        UserAnswerHolder(){
            foodNames = FoodInfo.foodList.stream()
                    .map(food -> food.name).collect(Collectors.toList());
            methods = new ArrayList<>();
            methods.addAll(yAnswers); methods.addAll(nAnswers);
            methods.addAll(addAnswers); methods.addAll(reduceCostAnswers);
            methods.addAll(otherAnswers);
            info = new ArrayList<>();
            info.addAll(energyWords); info.addAll(costWords);
            info.addAll(nutritionalBalance);
        }

        int getVariableType(String _extractedVariable){
            if(genders.contains(_extractedVariable))
                return Const.AnswerVariableType.Gender;
            else if(foodNames.contains(_extractedVariable))
                return Const.AnswerVariableType.Food;
            else
                return -1;
        }
        int getMethodType(String _extractedMethod){
            if(yAnswers.contains(_extractedMethod))
                return UserModelConst.AnswerType.Yes;
            else if(nAnswers.contains(_extractedMethod))
                return UserModelConst.AnswerType.No;
            else if(addAnswers.contains(_extractedMethod))
                return UserModelConst.AnswerType.Add;
            else if(otherAnswers.contains(_extractedMethod))
                return UserModelConst.AnswerType.Other;
            else if(reduceCostAnswers.contains(_extractedMethod))
                return UserModelConst.AnswerType.Reduce;
            else if(energyWords.contains(_extractedMethod))
                return UserModelConst.AnswerType.EnergyInfo;
            else if(costWords.contains(_extractedMethod))
                return UserModelConst.AnswerType.CostInfo;
            else if(nutritionalBalance.contains(_extractedMethod))
                return UserModelConst.AnswerType.NutritionalBalanceInfo;
            else if(info.contains(_extractedMethod))
                return UserModelConst.AnswerType.Info;
            else
                return -1;
        }
    }
}
