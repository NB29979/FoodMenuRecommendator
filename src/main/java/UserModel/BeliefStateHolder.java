package UserModel;

import java.util.ArrayList;
import java.util.List;

public class BeliefStateHolder {
    private List<BeliefState> beliefStateList = new ArrayList<>();

    public BeliefState getBeliefState(String _id){
        return this.beliefStateList.stream()
                .filter(c->c.getId().equals(_id))
                .findFirst()
                .orElse(null);
    }
    public BeliefState add(String _id){
        BeliefState beliefState_ = new BeliefState(_id);
        this.beliefStateList.add(beliefState_);
        return beliefState_;
    }
    public void remove(BeliefState _beliefState){
        this.beliefStateList.remove(_beliefState);
    }
    public boolean listIsEmpty(){return this.beliefStateList.isEmpty();}
}
