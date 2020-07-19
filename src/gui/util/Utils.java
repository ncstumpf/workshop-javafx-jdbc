package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	
	public static Stage currentStage(ActionEvent event) {
		return (Stage)((Node) event.getSource()).getScene().getWindow();//getSource gets everything, is not specific, so is downcasted to Node;
		//the method take the scene from the node, and gets the windows from the scene, and the window is downcasted to Stage;
	}

}
