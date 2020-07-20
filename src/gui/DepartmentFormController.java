package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	
	private Department entity;
	
	private DepartmentService service;
	
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
			entity = getFormData();
			service.saveOrUpdate(entity);
			Utils.currentStage(event).close();
		}
		catch (DbException e) {
			Alerts.ShowAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
		
	}
	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rs) {
		initializeNodes();//put the constraints
		
	}
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
		
	}
	
	
	public void updateFormData () {
		if (entity == null)
			throw new IllegalStateException();
		
		txtId.setText(String.valueOf(entity.getId())); // put both information on the box that will be displayed;
		txtName.setText(entity.getName());
	}
	
	private Department getFormData() {
		Department obj = new Department();
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		obj.setName(txtName.getText());
		return obj;
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


*/
