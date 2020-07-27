package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable {
	
	
	private Seller entity;
	
	private SellerService service;
	
	private List <DataChangedListener> dataChangedListeners = new ArrayList<>();
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	@FXML 
	private TextField txtId;
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;

	
	
	public void setSeller(Seller entity) {
		this.entity = entity;//Instanced when pressing the new button by the createDialog form
	}
	
	public void setDeparmentService(SellerService service) {
		this.service = service;
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null)
			throw new IllegalStateException("Entity was null");
		if (service == null)
			throw new IllegalStateException("Service was null");
		try {
			entity = getFormData();//Instantiate a department and set the fields, return and exception if the fields doesn't contain the appropriate information
			service.saveOrUpdate(entity);//check if there is an Id or not, to decide if is going to add or update
			notifyDataChangeListeners();//tell to the listeners what is happening to make them update
			Utils.currentStage(event).close();//closes this window
		}
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());//exception created when the fields doesn't match. 			
		}
		catch (DbException e) {
			Alerts.ShowAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);//when happens and error using SQL
		}
		
	}
	private void notifyDataChangeListeners() {
		for (DataChangedListener listener : dataChangedListeners)//go through all listeners and run the method which calls the changes
			listener.onDataChanged();
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rs) {
		initializeNodes();//put the constraints
		
	}
	private void initializeNodes() {//called when the window opens
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
		
	}
	
	
	public void updateFormData () {// is called when the windows is instantiated. can receive an empty department which will result in a new one, or a department that already exists, where you can update.
		if (entity == null)
			throw new IllegalStateException();
		
		txtId.setText(String.valueOf(entity.getId())); // put both information on the box that will be displayed;
		txtName.setText(entity.getName());
	}
	
	private Seller getFormData() {// take the information from the window and check if there is errors. Passing, returns a department with those values
		Seller obj = new Seller();
		ValidationException exception = new ValidationException("Validation Error");
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtName.getText()==null ||txtName.getText().trim().equals(""))
			exception.AddErrors("Name", "Field can't be empty");
		else
			obj.setName(txtName.getText());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
			
		return obj;
	}
	
	public void subscribeDataChangeListener(DataChangedListener listener) {//add a listener, usually to be updated
		dataChangedListeners.add(listener);
	}
	
	public void setErrorMessages(Map<String, String> errors){//throw the error messages in the error label on the window
		Set<String> fields = errors.keySet();
		if (fields.contains("Name")) 
			labelErrorName.setText(errors.get("Name"));
	}
	
}
