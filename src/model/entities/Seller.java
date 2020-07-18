package model.entities;

import java.io.Serializable;
import java.util.Date;

public class Seller implements Serializable {


	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name, email;
	private Date BirthDate;
	private Double baseSalary;
	
	private Department department;
	
	public Seller (){}
	public Seller(Integer id, String name, String email, Date birthDate, Double baseSalary, Department department) {
		this.id = id;
		this.name = name;
		this.email = email;
		BirthDate = birthDate;
		this.baseSalary = baseSalary;
		this.department = department;
	}

	public int getId() {
		return id;
	}

	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getBirthDate() {
		return BirthDate;
	}

	public void setBirthDate(Date birthDate) {
		BirthDate = birthDate;
	}

	public Double getBaseSalary() {
		return baseSalary;
	}

	public void setBaseSalary(Double baseSalary) {
		this.baseSalary = baseSalary;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Seller other = (Seller) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Seller [id=" + id + ", name=" + name + ", email=" + email + ", BirthDate=" + BirthDate + ", baseSalary="
				+ baseSalary + ", department=" + department + "]";
	}
	
	
}
