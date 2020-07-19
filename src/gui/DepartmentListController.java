package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable{
	
	private DepartmentService service;//Will be instantiated when the method is called from the MainViewController
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private Button btNew;	
	
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {//When pressing the button, event captures the reference to the stage;
		createDialogForm(Utils.currentStage(event), "/gui/DepartmentForm.fxml");//When called, the method receives the Stage and the path to the gui;
	}
	

	public void setDepartmentService(DepartmentService service) {//Control inversion(?). This method is called but another class (in this case happens when the button is pressed in through the MainViewController
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {//when the view starts, call this method to prepare 
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));//Standard to throw a value in the TableView;
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		Stage stage = (Stage)Main.getMainScene().getWindow(); //Saves the window configuration, like size and stuff like that
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty()); //makes the table go along with the view
		
	}
	
	public void updateTableView() {//turns the list into an observable list, which can be shown in the program
		if (service == null) {
			throw new IllegalStateException("Service was null");			
		}
		List <Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
	}
	
	private void createDialogForm(Stage currentStage, String absoluteName) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));//new FXML get all the resources from the view that will be on top;
			Pane pane = loader.load();//Because we used an anchor pane as view;
			
			Stage dialogStage = new Stage();//Stage, which is the window
			dialogStage.setTitle("Enter department data: ");
			dialogStage.setScene(new Scene(pane));//Scene is the view, that was loaded for the loader and put in the pane;
			dialogStage.setResizable(false); //you can't resize, obviously;
			dialogStage.initOwner(currentStage);//When you close the owner you close the new window; 
			dialogStage.initModality(Modality.WINDOW_MODAL);//Without that, you'd be able to use the parent page with the children being used independently (just works if it has an owner);
			dialogStage.showAndWait();//without that, nothing happens;
		} catch (IOException e) {
			Alerts.ShowAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}
