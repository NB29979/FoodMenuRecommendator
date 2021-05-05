import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import UserModel.MessageMetadata;
import UserModel.BeliefStateHolder;
import UserModel.BeliefState;
import UserModel.QuestionInfo;

import FoodModel.FoodInfo;

class ServerMain {
    private static BeliefStateHolder beliefStateHolder = new BeliefStateHolder();

    static{
        FoodInfo.build();
        QuestionInfo.build();
    }

    public static void main(String args[]) throws Exception {
        boolean isEnd = false;
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> dialogTags = new ArrayList<>();

        while(!isEnd){
            String userInput = scanner.next();

            MessageMetadata userMessageMetadata_ =
                    new MessageMetadata("id", "Client", dialogTags, userInput);

            String dialogId_ = userMessageMetadata_.dialogId;

            if(userMessageMetadata_.messageUser.equals("Client")) {
                System.out.println("[Received]: ");
                userMessageMetadata_.Debug();

                BeliefState beliefState_ = getHoldingContext(dialogId_);

                List<MessageMetadata> systemOutputMetadataList_ =
                        SystemSpeechManager.getInstance().SelectAct(beliefState_, userMessageMetadata_);

                for (MessageMetadata metadata: systemOutputMetadataList_) {
                    System.out.println("[Send]: ");
                    metadata.Debug();
                    dialogTags = metadata.dialogTags;

                    if (metadata.message.contains("Bye")) {
                        beliefStateHolder.remove(beliefState_);
                        if(beliefStateHolder.listIsEmpty()) {
                            isEnd = true;
                        }
                    }
                }
            }
        }
    }

    private static BeliefState getHoldingContext(String _dialogId){
        BeliefState beliefState_ = beliefStateHolder.getBeliefState(_dialogId);
        if(beliefState_ == null){
            beliefState_ = beliefStateHolder.add(_dialogId);
            try {
                beliefState_.init(Const.DialogGenre.Setup);
            }
            catch (Exception e){e.printStackTrace();}
        }
        return beliefState_;
    }
}
