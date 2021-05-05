package UserModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;

public class SlotFiller {
    private HashMap<String, List<String>> commonGround;

    SlotFiller(){
        commonGround = new HashMap<>();
    }

    public void init(List<String> _questionTags){
        commonGround.clear();
        _questionTags.forEach(this::addAttribute);
    }
    private void addAttribute(String _tag){
        commonGround.put(_tag, new ArrayList<>());
    }

    void updateSlot(HashMap<String, String>_targetMessages){
        _targetMessages.entrySet().forEach(entry->{
            String key_ = entry.getKey();
            if(commonGround.get(key_).isEmpty())
                commonGround.get(key_).add(entry.getValue());
            else
                commonGround.get(key_).add(entry.getValue());
        });
    }
    HashMap<String, List<String>> getCommonGround(){
        return commonGround;
    }
    void debug(){
        commonGround.entrySet()
                .forEach(entry-> System.out.println(entry.getKey()+"-"+entry.getValue()));
    }
}
