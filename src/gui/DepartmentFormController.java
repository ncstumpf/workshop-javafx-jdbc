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
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	
	private Department entity;
	
	private DepartmentService service;
	
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

	
	
	public void setDepartment(Department entity) {
		this.entity = entity;//Instanced when pressing the new button by the createDialog form
	}
	
	public void setDeparmentService(DepartmentService service) {
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
	
	private Department getFormData() {// take the information from the window and check if there is errors. Passing, returns a department with those values
		Department obj = new Department();
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


/*calls and empty department called entity and instantiate using a set method
created method Updateformdata, first testing if entity is null throwing illegalstate
and after setting the txtbox with the values from entity

the department will be instantiated from onbtnewaction on dplistcontrol
in there, implement a new department null and send as parameter to the createdialog form
so when the window form is called, the department is already ready to be edited

on createdialogform  instantiate a depformcontroller and getcont from loader and use it to the update form data

The ideia behind is to use this with the update class, promoving reuse. instantiate the object with the action is a normal pratice in MVC


-------------------------------------------------------------------------------------------------------------------------------

In utils class, create the method tryParseToInt which receives a String and try to convert into an int inside a try block. 
if it fails catch a NumberFomatException and return null
-saveOrUpdate on DepartmentService receiving a Department obj and checking if the id is null to decide if is going to update or insert
-Create a setDepartmentService to make the control inversion
-create a method getFormData that takes the data from the fields

IN THE SAVE BUTTON INJECT DEFENSIVE PROGRAMMING TO CHECK IF THE ENTITY IS NULL
illegalstate
DO THE SAME WITH SERVICE
PUT EVERYTHING INSIDE A TRY BLOCK AND CATCH DB EXCEPTION SHOWING ALERT
ERROR SAVING OBJECT, NULL, ERROR
TAKE THE EVENT AND USE IT TO CLOSE USING CURRENTSTAGE FROM UTILS (DO THE SAME IN CANCEL)

//create listener
 * 
 * primary create the interface datachangedlistener and empty void ondatachanged
 * departmentformcontroller will have a list of datachangelistener interested in receive the event
 * create a method to add listeners called subscribedatalistener
 * create a method that is called when press the save button called notifydatachangelistener, which will send the message to them using ondatachanged
 * lidtcontroller implements listener with updatetableview and in dialog form put subscribelistener sending "this"

create validation exception of type runtime. start a map of strings and one normal get and adderror receiving fieldName and errorMessage
on getFormData instantiate exception ("Validation Error") and test if the name is empty by null or using trim and add error field cant be empty
if error's qantity are greater than 0 trhwo the message
create a setErrorMessages receiving a map




*/
