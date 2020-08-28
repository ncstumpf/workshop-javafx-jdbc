package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangedListener{
	
	private DepartmentService service;//Will be instantiated when the method is called from the MainViewController
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;

	@FXML
	private TableColumn<Department, Department> tableColumnRemove;

	@FXML
	private Button btNew;	
	
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {//When pressing the button, event captures the reference to the stage;
		Stage parentStage = Utils.currentStage(event);
		Department obj = new Department();
		createDialogForm(parentStage, obj, "/gui/DepartmentForm.fxml");//When called, the method receives the Stage and the path to the gui;
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
			throw new IllegalStateException("Service was null");//defensive programming			
		}
		List <Department> list = service.findAll();//create list
		obsList = FXCollections.observableArrayList(list);//turn to observable
		tableViewDepartment.setItems(obsList);//pass to the table view
		initEditButtons();
		initRemoveButtons();
	}
	


	private void createDialogForm(Stage currentStage, Department obj, String absoluteName) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));//new FXML get all the resources from the view that will be on top;
			Pane pane = loader.load();//Because we used an anchor pane as view;
			
			DepartmentFormController controller = loader.getController();//loads the controller of the view on top
			controller.setDepartment(obj); //received from the action button, will set the department (in case of new, info will be null)
			controller.setDeparmentService(new DepartmentService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();//put the info in the text fields
			
			Stage dialogStage = new Stage();//Stage, which is the window
			dialogStage.setTitle("Enter department data: ");
			dialogStage.setScene(new Scene(pane));//Scene is the view, that was loaded for the loader and put in the pane;
			dialogStage.setResizable(false); //you can't resize, obviously;
			dialogStage.initOwner(currentStage);//When you close the owner you close the new window; 
			dialogStage.initModality(Modality.WINDOW_MODAL);//Without that, you'd be able to use the parent page with the children being used independently (just works if it has an owner);
			dialogStage.showAndWait();//without that, nothing happens;
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.ShowAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));//standard
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>(){//just changes the type of the TableCell
			private final Button button = new Button("edit"); //you instantiate the button, changing the name and which text is displayed
			@Override
			protected void updateItem(Department obj, boolean empty) {//just changes the object
				super.updateItem(obj, empty);
				
				if(obj == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(//here you dictate what is going to happen if you press the button (edit), where you could put the final (like delete from there);
								Utils.currentStage(event), obj, "/gui/DepartmentForm.fxml"));
			}
		});
		
	}

	
	private void initRemoveButtons() {
		tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));//standard
		tableColumnRemove.setCellFactory(param -> new TableCell<Department, Department>(){//just changes the type of the TableCell
			private final Button button = new Button("remove"); //you instantiate the button, changing the name and which text is displayed
			@Override
			protected void updateItem(Department obj, boolean empty) {//just changes the object
				super.updateItem(obj, empty);
				
				if(obj == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction(
						event -> removeEntity(obj));// calls method that will instantiate a window to confirm if is wanted to delete the obj
			}
		});
		
	}

	protected void removeEntity(Department obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		if (result.get()== ButtonType.OK) {
			if (service==null)
				throw new IllegalStateException("Service was null");
			try {
				service.remove(obj);
				updateTableView();
			}
			catch (DbIntegrityException e) {//exception from the DAO
				Alerts.ShowAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
			
		}
	}


	@Override
	public void onDataChanged() {
		updateTableView();
	}
}

/*
 Create a new tablecolumn called tableColumnEDIT recei9ving Department,Department.
 Create a initEditButtons() which has a ready code, just copy and paste.
 This method will be called in updateTableView
 On the FXML file, create new table receiving the EDIT
  
  to the same to create the deletebutton, but to do that is necessary to create um static optionL buttontype ShowConfirmation which will be responsible for creating a window to confirm returning a show and wait alert\
  create a void remove in depService
  create a tablecolumnremove an initRemoveButtons (copying the last one but receiving the event removeEntity
  private void removeEntity will send alert confirmation with the sentence r u sure to delete
  the result will be inside an Optional result. After check if the buttonType of the result.get() is equal to OK
  Inside the if, check if is null to throw IllegalState, and after try with remove and updateTableView
  after put another catch with dbintegrityException and check in the class at 8min
  call method in updateTableView
  
  
  
  
  
  
  
  
 
  
 
 * */
