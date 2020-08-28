package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Callback;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;

	private SellerService service;
	
	private DepartmentService departmentService;

	private List<DataChangedListener> dataChangedListeners = new ArrayList<>();

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpBirthDate;// Class responsible for having calendars and stuff

	@FXML
	private TextField txtBaseSalary;
	
	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorBaseSalary;

	private ObservableList<Department> obsList; //integration between the logic and the visual
	
	public void setSeller(Seller entity) {
		this.entity = entity;// Instanced when pressing the new button by the createDialog form
	}

	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null)
			throw new IllegalStateException("Entity was null");
		if (service == null)
			throw new IllegalStateException("Service was null");
		try {
			entity = getFormData();// Instantiate a department and set the fields, return and exception if the
									// fields doesn't contain the appropriate information
			service.saveOrUpdate(entity);// check if there is an Id or not, to decide if is going to add or update
			notifyDataChangeListeners();// tell to the listeners what is happening to make them update
			Utils.currentStage(event).close();// closes this window
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());// exception created when the fields doesn't match.
		} catch (DbException e) {
			Alerts.ShowAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);// when happens and error
																							// using SQL
		}

	}

	private void notifyDataChangeListeners() {
		for (DataChangedListener listener : dataChangedListeners)// go through all listeners and run the method which
																	// calls the changes
			listener.onDataChanged();
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rs) {
		initializeNodes();// put the constraints

	}

	private void initializeNodes() {// called when the window opens
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		
		initializeComboBoxDepartment();
	}

	public void updateFormData() {// is called when the windows is instantiated. can receive an empty department
									// which will result in a new one, or a department that already exists, where
									// you can update.
		if (entity == null)
			throw new IllegalStateException();

		txtId.setText(String.valueOf(entity.getId())); // put both information on the box that will be displayed;
		txtName.setText(entity.getName());
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		txtEmail.setText(entity.getEmail());
		if (entity.getBirthDate() != null)
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), // transforms the Date into
																						// LocalDate (which is necessary
																						// to translate the time to
																						// wherever the computer is
					ZoneId.systemDefault()));// takes the zone from the system
		if (entity.getDepartment()==null) 
			comboBoxDepartment.getSelectionModel().selectFirst();
		else
			comboBoxDepartment.setValue(entity.getDepartment());
		
	}

	private Seller getFormData() {// take the information from the window and check if there is errors. Passing,
									// returns a department with those values
		Seller obj = new Seller();
		ValidationException exception = new ValidationException("Validation Error");
		obj.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals(""))
			exception.AddErrors("Name", "Field can't be empty");
		else
			obj.setName(txtName.getText());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}

	public void subscribeDataChangeListener(DataChangedListener listener) {// add a listener, usually to be updated
		dataChangedListeners.add(listener);
	}
	
	public void loadAssociatedObjects() {//method to load all the objects that aren't sellers. This method is called when the SellerListController instantiate the view
		if (departmentService==null)
			throw new IllegalStateException("Department Service was null");
			
		List <Department> list = departmentService.findAll();//take the departments from the database
		obsList = FXCollections.observableArrayList(list);//make them observable
		comboBoxDepartment.setItems(obsList);//throw them into the combobox on the view
	}
	
	private void initializeComboBoxDepartment() {//standard, to apply or copy here or check in the PDF (this project or javafx class)
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		

		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

	public void setErrorMessages(Map<String, String> errors) {// throw the error messages in the error label on the
																// window
		Set<String> fields = errors.keySet();
		if (fields.contains("Name"))
			labelErrorName.setText(errors.get("Name"));
	}

}
