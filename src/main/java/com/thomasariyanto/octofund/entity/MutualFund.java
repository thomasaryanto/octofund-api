package com.thomasariyanto.octofund.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@JsonInclude(Include.NON_NULL)
public class MutualFund {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "manager_id")
	private Manager manager;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mutualFund", cascade = CascadeType.ALL)
	@JsonManagedReference(value="mutualfund-pricehistory")
	private List<PriceHistory> priceHistory;
	
	@NotEmpty(message = "Nama reksadana tidak boleh kosong!")
	private String name;
	
//	@NotEmpty(message = "Minimum beli tidak boleh kosong!")
	private int minimumBuy;
	
//	@NotEmpty(message = "Tanggal berdiri tidak boleh kosong!")
	private Date launchDate;
	
	@NotEmpty(message = "Jumlah dana kelolaan tidak boleh kosong!")
	private String totalFund;
	
	@NotEmpty(message = "Bank kustodian tidak boleh kosong!")
	private String custodyBank;
	
	@NotEmpty(message = "File prospectus tidak boleh kosong!")
	private String prospectusFile;
	
	@NotEmpty(message = "File fund fact sheet tidak boleh kosong!")
	private String factsheetFile;
	
	private double lastPrice;
	private Date lastUpdatePrice;

	public int getId() {
		return id;
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

	public int getMinimumBuy() {
		return minimumBuy;
	}

	public void setMinimumBuy(int minimumBuy) {
		this.minimumBuy = minimumBuy;
	}

	public Date getLaunchDate() {
		return launchDate;
	}

	public void setLaunchDate(Date launchDate) {
		this.launchDate = launchDate;
	}

	public String getTotalFund() {
		return totalFund;
	}

	public void setTotalFund(String totalFund) {
		this.totalFund = totalFund;
	}

	public String getCustodyBank() {
		return custodyBank;
	}

	public void setCustodyBank(String custodyBank) {
		this.custodyBank = custodyBank;
	}

	public String getProspectusFile() {
		return prospectusFile;
	}

	public void setProspectusFile(String prospectusFile) {
		this.prospectusFile = prospectusFile;
	}

	public String getFactsheetFile() {
		return factsheetFile;
	}

	public void setFactsheetFile(String factsheetFile) {
		this.factsheetFile = factsheetFile;
	}

	public Date getLastUpdatePrice() {
		return lastUpdatePrice;
	}

	public void setLastUpdatePrice(Date lastUpdatePrice) {
		this.lastUpdatePrice = lastUpdatePrice;
	}

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

	public double getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(double lastPrice) {
		this.lastPrice = lastPrice;
	}
	
}
