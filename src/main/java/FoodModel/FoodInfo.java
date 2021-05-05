package FoodModel;

import Utility.FileUtility;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class FoodInfo {
    public static ArrayList<Food> foodList;

    public static void build(){
        try {
            foodList = new ObjectMapper().readValue(
                    FileUtility.ReadAllLines("", "FoodInfo.json"),
                    new TypeReference<ArrayList<Food>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
