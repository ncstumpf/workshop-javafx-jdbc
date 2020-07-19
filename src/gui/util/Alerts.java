package gui.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Alerts {
	
	public static void ShowAlert (String title, String head, String content, AlertType type) {
		Alert alert = new Alert (type); //Alert type, when is called, print the information;
		alert.setTitle(title);
		alert.setHeaderText(head);
		alert.setContentText(content);
		alert.show();		
	}

}
