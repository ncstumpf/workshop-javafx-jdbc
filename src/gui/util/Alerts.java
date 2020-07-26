package gui.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Alerts {
	
	public static void ShowAlert (String title, String head, String content, AlertType type) {
		Alert alert = new Alert (type); //Alert type, when is called, print the information;
		alert.setTitle(title);
		alert.setHeaderText(head);
		alert.setContentText(content);
		alert.show();		
	}
	public static Optional<ButtonType> showConfirmation(String title, String content){
		Alert alert = new Alert (AlertType.CONFIRMATION); 
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		return alert.showAndWait();		
	}

}
