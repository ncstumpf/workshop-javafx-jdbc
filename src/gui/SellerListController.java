package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangedListener{
	
	private SellerService service;//Will be instantiated when the method is called from the MainViewController
	
	@FXML
	private TableView<Seller> tableViewSeller;
	
	@FXML
	private TableColumn<Seller, Integer> tableColumnId;

	@FXML
	private TableColumn<Seller, String> tableColumnName;
	
	@FXML
	private TableColumn<Seller, String> tableColumnEmail;
	
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
	
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;

	@FXML
	private TableColumn<Seller, Seller> tableColumnRemove;

	@FXML
	private Button btNew;	
	
	private ObservableList<Seller> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {//When pressing the button, event captures the reference to the stage;
		Stage parentStage = Utils.currentStage(event);
		Seller obj = new Seller();
		createDialogForm(parentStage, obj, "/gui/SellerForm.fxml");//When called, the method receives the Stage and the path to the gui;
	}
	

	public void setSellerService(SellerService service) {//Control inversion(?). This method is called but another class (in this case happens when the button is pressed in through the MainViewController
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {//when the view starts, call this method to prepare 
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));//Standard to throw a value in the TableView;
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
		
		
		Stage stage = (Stage)Main.getMainScene().getWindow(); //Saves the window configuration, like size and stuff like that
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty()); //makes the table go along with the view
		
	}
	
	public void updateTableView() {//turns the list into an observable list, which can be shown in the program
		if (service == null) {
			throw new IllegalStateException("Service was null");//defensive programming			
		}
		List <Seller> list = service.findAll();//create list
		obsList = FXCollections.observableArrayList(list);//turn to observable
		tableViewSeller.setItems(obsList);//pass to the table view
		initEditButtons();
		initRemoveButtons();
	}
	


	private void createDialogForm(Stage currentStage, Seller obj, String absoluteName) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));//new FXML get all the resources from the view that will be on top;
			Pane pane = loader.load();//Because we used an anchor pane as view;
			
			SellerFormController controller = loader.getController();//loads the controller of the view on top
			controller.setSeller(obj); //received from the action button, will set the department (in case of new, info will be null)
			controller.setDeparmentService(new SellerService());
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
			Alerts.ShowAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));//standard
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>(){//just changes the type of the TableCell
			private final Button button = new Button("edit"); //you instantiate the button, changing the name and which text is displayed
			@Override
			protected void updateItem(Seller obj, boolean empty) {//just changes the object
				super.updateItem(obj, empty);
				
				if(obj == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(//here you dictate what is going to happen if you press the button (edit), where you could put the final (like delete from there);
								Utils.currentStage(event), obj, "/gui/SellerForm.fxml"));
			}
		});
		
	}

	
	private void initRemoveButtons() {
		tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));//standard
		tableColumnRemove.setCellFactory(param -> new TableCell<Seller, Seller>(){//just changes the type of the TableCell
			private final Button button = new Button("remove"); //you instantiate the button, changing the name and which text is displayed
			@Override
			protected void updateItem(Seller obj, boolean empty) {//just changes the object
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

	protected void removeEntity(Seller obj) {
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
 
 */
