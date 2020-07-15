package com.thomasariyanto.octofund.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Entity
public class MutualFundPackage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH })
	@JoinColumn(name = "manager_id")
	private Manager manager;
	
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH })
	@JoinColumn(name = "product_one_id")
	private MutualFund productOne;
	
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH })
	@JoinColumn(name = "product_two_id")
	private MutualFund productTwo;
	
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH })
	@JoinColumn(name = "product_three_id")
	private MutualFund productThree;
	
	@NotEmpty(message = "Nama paket tidak boleh kosong!")
	String packageName;
	@NotEmpty(message = "Deskripsi tidak boleh kosong!")
	String description;
	Date date;
	@Positive(message = "Persentase reksadana 1 tidak boleh kosong!")
	int percentageOne;
	@Positive(message = "Persentase reksadana 2 tidak boleh kosong!")
	int percentageTwo;
	@Positive(message = "Persentase reksadana 3 tidak boleh kosong!")
	int percentageThree;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

	public MutualFund getProductOne() {
		return productOne;
	}

	public void setProductOne(MutualFund productOne) {
		this.productOne = productOne;
	}

	public MutualFund getProductTwo() {
		return productTwo;
	}

	public void setProductTwo(MutualFund productTwo) {
		this.productTwo = productTwo;
	}

	public MutualFund getProductThree() {
		return productThree;
	}

	public void setProductThree(MutualFund productThree) {
		this.productThree = productThree;
	}

	public int getPercentageOne() {
		return percentageOne;
	}

	public void setPercentageOne(int percentageOne) {
		this.percentageOne = percentageOne;
	}

	public int getPercentageTwo() {
		return percentageTwo;
	}

	public void setPercentageTwo(int percentageTwo) {
		this.percentageTwo = percentageTwo;
	}

	public int getPercentageThree() {
		return percentageThree;
	}

	public void setPercentageThree(int percentageThree) {
		this.percentageThree = percentageThree;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
