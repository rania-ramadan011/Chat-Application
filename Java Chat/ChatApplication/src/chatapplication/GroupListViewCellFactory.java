package chatapplication;

import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.util.Callback;
import rmiinterfaces.*;

public class GroupListViewCellFactory implements Callback<ListView<Group>, ListCell<Group>> {

    @Override
    public ListCell<Group> call(ListView<Group> param) {
        return new ListCell<Group>() {
            private HBox contact;
            private VBox nameAndStatus;
            private Circle status;
            private Label contactname;
            private ImageView contactImg;
            private ChatController chatController = (ChatController) ServiceLocator.getService("chatController");

            @Override
            protected void updateItem(Group item, boolean empty) {
                super.updateItem(item, empty);
                contact = new HBox();
                contact.setSpacing(10);
                nameAndStatus = new VBox();
                nameAndStatus.setSpacing(5);
                status = new Circle(7.0);
                contactname = new Label();
                contactImg = new ImageView();
                contactImg.setFitHeight(40);
                contactImg.setFitWidth(40);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    contactname.setText(item.getGroup_name());
//                    if (item.getClient_image() != null) {
//                        Image img = new Image(new ByteArrayInputStream(item.getClient_image()), 40, 40, true, true);
//                        contactImg.setImage(img);
//                    }                   
                    setOnMouseClicked((e) -> {
                        for (Client client : item.getGroup()) {
                         Message msg = new Message();
                        msg.setReceiverName(client);
                        chatController.setMsgVBox(ChatController.getMsgArea().get(item.getGroup_id()));
                        chatController.setReceiverClient(client);
                        chatController.setContactData();     
                        }                                              
                    });
                    nameAndStatus.getChildren().addAll(contactname, status);
                    contact.getChildren().addAll(contactImg, nameAndStatus);
                    setGraphic(contact);
                }
            }
        };
    }

}
