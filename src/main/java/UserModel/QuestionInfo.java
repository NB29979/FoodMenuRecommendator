package UserModel;

import Utility.FileUtility;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

public class QuestionInfo {
    static HashMap<String, HashMap<String, Question>> allQuestions;

    public static void build(){
        try{
            allQuestions = new ObjectMapper().readValue(
                            FileUtility.ReadAllLines("Questions", "Questions.json"),
                            new TypeReference<HashMap<String, HashMap<String, Question>>>(){});
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
