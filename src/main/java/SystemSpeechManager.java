import UserModel.MessageMetadata;
import UserModel.BeliefState;
import UserModel.UserModelConst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


class SystemSpeechManager {
    private InputAnalyzer inputAnalyzer;

    private SystemSpeechManager(){
        inputAnalyzer = new InputAnalyzer();
    }
    private static class SingletonHolder{ private static final SystemSpeechManager INSTANCE = new SystemSpeechManager(); }
    static SystemSpeechManager getInstance() throws ExceptionInInitializerError { return SingletonHolder.INSTANCE; }

    synchronized List<MessageMetadata> SelectAct(BeliefState _beliefState, MessageMetadata _userMessageMetadata){
        List<MessageMetadata> systemOutputs = new ArrayList<>();
        String receivedMessage = _userMessageMetadata.message;

        inputAnalyzer.analyze(_beliefState.getGenre(), _userMessageMetadata);

        if (_beliefState.messageHasPrevQuestionTag()){
            // ユーザは食品の詳細説明を求めているか？
            if(inputAnalyzer.detectMethodType()==UserModelConst.AnswerType.EnergyInfo) {
                // いまは一時的にしているが将来的にはtypeで振り分け
                String buildMessage_;
                if(inputAnalyzer.variableQueue.isEmpty())
                    buildMessage_ = _beliefState.generateDescription(UserModelConst.AnswerType.EnergyInfo);
                else
                    // 食品の栄養バランスなどを聞かれたとき
                    buildMessage_ = _beliefState.generateDescription(UserModelConst.AnswerType.EnergyInfo, inputAnalyzer.variableQueue.getFirst());

                systemOutputs.add(
                        new MessageMetadata(_beliefState.getId(), "Server",
                                new ArrayList<>(Arrays.asList(_beliefState.getGenre())), buildMessage_));
            }
            else {
                HashMap<String, String> convertedData;
                switch (_beliefState.getGenre()) {
                    // 繰り返し献立推薦
                    case Const.DialogGenre.ConfirmMenu:
                        if (inputAnalyzer.methodQueue.size() != 0) {
                            String dialogId = _beliefState.getId();
                            String buildMessage_="すみません，よくわかりません";

                            switch (inputAnalyzer.detectMethodType()) {
                                case UserModelConst.AnswerType.No:
                                    if (inputAnalyzer.variableQueue.size() != 0) {
                                        String foodName_ = inputAnalyzer.variableQueue.getFirst();
                                        _beliefState.removeFood(foodName_);
                                        buildMessage_ = _beliefState.provideMenu().buildRecommendMenuString();
                                    }
                                    break;
                                case UserModelConst.AnswerType.Add:
                                    if (inputAnalyzer.variableQueue.size() != 0) {
                                        convertedData = inputAnalyzer.convertMessage2Map(_beliefState, receivedMessage);
                                        _beliefState.updateSlot(convertedData);
                                        _beliefState.initRecommendationGenerator();
                                        buildMessage_ = _beliefState.provideMenu().buildRecommendMenuString();
                                    }
                                    break;
                                case UserModelConst.AnswerType.Other:
                                    buildMessage_ = _beliefState.selectNextRecommendation().buildRecommendMenuString();
                                    break;
                                case UserModelConst.AnswerType.Reduce:
                                    _beliefState.reduceCost();
                                    _beliefState.initRecommendationGenerator();
                                    buildMessage_ = _beliefState.provideMenu().buildRecommendMenuString();
                                    break;
                                case UserModelConst.AnswerType.Yes:
                                    if (inputAnalyzer.methodQueue.size() <= 1) {
                                        buildMessage_ = "お役に立ててうれしいです\\Bye";
                                    }
                                    break;
                            }
                            systemOutputs.add(
                                    new MessageMetadata(dialogId, "Server",
                                            new ArrayList<>(Arrays.asList("ConfirmMenu")), buildMessage_));
                        }
                        break;
                    default:
                        if (inputAnalyzer.variableQueue.size() != 0) {
                            convertedData = inputAnalyzer.convertMessage2Map(_beliefState, receivedMessage);
                            _beliefState.updateSlot(convertedData);
                        }
                        break;
                }
            }
        }

        // Questionリストが空になった <=> 質問し終えた
        if (_beliefState.dialogQuestionsIsEmpty()) {
            switch (_beliefState.getGenre()) {
                case Const.DialogGenre.Setup:
                    _beliefState.setGender();
                    _beliefState.init(Const.DialogGenre.Food);
                    systemOutputs.add(_beliefState.generateQuestion());
                    break;

                case Const.DialogGenre.Food:
                    _beliefState.initRecommendationGenerator();
                    String recommendationMessage = _beliefState.provideMenu().buildRecommendMenuString();
                    _beliefState.setGenre(Const.DialogGenre.ConfirmMenu);
                    systemOutputs.add(
                            new MessageMetadata(_beliefState.getId(), "Server",
                                    new ArrayList<>(Arrays.asList(_beliefState.getGenre())),
                                    recommendationMessage));
                    break;
            }
        } else {
            systemOutputs.add(_beliefState.generateQuestion());
        }
        if(systemOutputs.isEmpty())
            systemOutputs.add(
                    new MessageMetadata(_beliefState.getId(),"Server",
                            new ArrayList<>(_beliefState.getDialogTags()), "すみません，よくわかりません"));

        inputAnalyzer.clear();

        return systemOutputs;
    }
}
