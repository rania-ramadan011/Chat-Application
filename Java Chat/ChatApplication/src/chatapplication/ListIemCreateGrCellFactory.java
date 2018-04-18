package chatapplication;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import rmiinterfaces.Client;

public class ListIemCreateGrCellFactory implements Callback<ListView<Client>, ListCell<Client>> {

    @Override
    public ListCell<Client> call(ListView<Client> param) {
        return new ListCell<Client>() {
            private HBox contact;
            private VBox nameAndStatus;
            private Circle status;
            private Label contactname;
            private ImageView contactImg;
            private CheckBox checkBox;
            private ChatController chatController = (ChatController) ServiceLocator.getService("chatController");

            private final ClientImp clientImp = (ClientImp) ServiceLocator.getService("clientService");

            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                contact = new HBox();
                nameAndStatus = new VBox();
                status = new Circle();
                contactname = new Label();
                contactImg = new ImageView();
                checkBox = new CheckBox();

                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    contactname.setText(item.getClient_name());
                    // contactImg.setImage(new Image(item.getClient_image()));
                    switch (item.getClient_status()) {
                        case "online":
                            status.setFill(Paint.valueOf("GREEN"));
                            break;
                        case "offline":
                            status.setFill(Paint.valueOf("GREY"));
                            break;
                        case "busy":
                            status.setFill(Paint.valueOf("RED"));
                            break;
                        case "away":
                            status.setFill(Paint.valueOf("YELLOW"));
                            break;
                        default:
                            status.setFill(Paint.valueOf("GREEN"));
                            break;
                    }
                    
                    checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                        if (newVal) {
                            chatController.addGroupMembers(item);
                            System.out.println("Checked");
                        } else {
                            chatController.removeItemGroupMembers(item);                           
                            System.out.println("Unchecked");
                        }
                    });

                    nameAndStatus.getChildren().addAll(contactname, status);
                    contact.getChildren().addAll(contactImg, nameAndStatus, checkBox);
                    setGraphic(contact);
                }
            }
        };
    }

}
