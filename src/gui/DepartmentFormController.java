package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable {
	
	/*calls and empty department called entity and instantiate using a set method
created method Updateformdata, first testing if entity is null throwing illegalstate
and after setting the txtbox with the values from entity

the department will be instantiated from onbtnewaction on dplistcontrol
in there, implement a new department null and send as parameter to the createdialog form
so when the window form is called, the department is already ready to be edited

on createdialogform  instantiate a depformcontroller and getcont from loader and use it to the update form data

The ideia behind is to use this with the update class, promoving reuse. instantiate the object with the action is a normal pratice in MVC

*/
	private Department entity;
	
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
		this.entity = entity;//instancied when pressing the new button by the createDialog form
	}
	
	@FXML
	public void onBtSaveAction() {
		System.out.println("save");
	}
	@FXML
	public void onBtCancelAction() {
		System.out.println("cancel");
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
	
}
