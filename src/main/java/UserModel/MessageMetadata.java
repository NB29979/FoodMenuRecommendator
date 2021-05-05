package UserModel;

import java.util.ArrayList;

public class MessageMetadata {
    public String dialogId;
    public String messageUser;
    public ArrayList<String> dialogTags;
    public String message;

    public MessageMetadata(String _dialogId, String _messageUser,
                           ArrayList<String> _dialogTags, String _message){
        this.dialogId = _dialogId;
        this.messageUser = _messageUser;
        this.dialogTags = _dialogTags;
        this.message = _message;
    }
    public void Debug(){
        System.out.println(
            "dialogId: " + dialogId + ",\n" +
            "dialogTags: " + dialogTags + ",\n" +
            "message: " + message
        );
    }
}
