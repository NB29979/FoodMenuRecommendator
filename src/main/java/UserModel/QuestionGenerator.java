package UserModel;

import java.util.ArrayList;
import java.util.HashMap;

class Question{
    public String text;
}
class QuestionGenerator {
    private HashMap<String, Question> dialogQuestions;
    private String currentQuestionTag;

    QuestionGenerator(){
        dialogQuestions = new HashMap<>();
    }

    void init(String _genre){
        QuestionInfo.allQuestions.get(_genre).forEach(dialogQuestions::put);
    }

    MessageMetadata generateQuestion(String _dialogId){
        ArrayList<String> questionTags_ =
                new ArrayList<>(dialogQuestions.keySet());
        int generateQuestionIndex_ = (int)(Math.random()*questionTags_.size());
        String questionTag_ = questionTags_.get(generateQuestionIndex_);

        currentQuestionTag = questionTag_;

        MessageMetadata questionOutput_ = new MessageMetadata(_dialogId, "Server",
                new ArrayList<String>(){{add(questionTag_);}},
                dialogQuestions.get(questionTag_).text);

        return questionOutput_;
    }

    void removeQuestion(){
        dialogQuestions.remove(currentQuestionTag);
    }

    boolean dialogQuestionsIsEmpty(){
        return dialogQuestions.isEmpty();
    }

    ArrayList<String> getQuestionTags(){
        return new ArrayList<>(dialogQuestions.keySet());
    }
    String getCurrentQuestionTag(){
        return currentQuestionTag;
    }
}
