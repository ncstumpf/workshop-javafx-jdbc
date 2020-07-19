package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;

public class MainViewController implements Initializable{

	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("Vendedor");
	}
	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller)-> {
			controller.setDepartmentService(new DepartmentService());//Using a lambda expression, instantiate the department service and prepare the ground to receive the new view
			controller.updateTableView();
		});;
	}
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x->{});
	}
	 
	
	@Override
	public void initialize(URL url, ResourceBundle rs) {
		// TODO Auto-generated method stub
		
	}
	
	private synchronized <T> void loadView (String absoluteName, Consumer <T> initializingAction) {// the T can be substituted for a controller
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));//Load the new view that will be displayed
			VBox newVBox = loader.load();//throw the new view in a VBox
			Scene mainScene = Main.getMainScene();//takes the main scene from the Main and kind of copy it
			
			VBox mainVBox = (VBox)((ScrollPane) mainScene.getRoot()).getContent();//saves the content of the scrollpane (downcasting) and put in a VBox. Root is the first element of the view
			Node mainMenu = mainVBox.getChildren().get(0);//takes the menu from the top and saves in the Node;
			mainVBox.getChildren().clear();//erases everything
			mainVBox.getChildren().add(mainMenu);//add the menu saved
			mainVBox.getChildren().addAll(newVBox.getChildren());//add the new scene inside a VBox
			
			T controller = loader.getController();//loads the controller (can be the Department or Seller)
			initializingAction.accept(controller);//Consumer method to apply the methods that were injected when this method is called

		} catch (IOException e) {
			Alerts.ShowAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
			e.printStackTrace();
		}
	}
	

}
