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
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
//@JsonInclude(Include.NON_NULL)
public class MutualFund {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "manager_id")
	private Manager manager;
	
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "category_id")
	private MutualFundCategory mutualFundCategory;
	
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "type_id")
	private MutualFundType mutualFundType;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mutualFund", cascade = CascadeType.ALL)
	@JsonManagedReference(value="mutualfund-pricehistory")
	private List<PriceHistory> priceHistory;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mutualFund", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Transaction> transactions;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mutualFund", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Portfolio> portfolios;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productOne", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<MutualFundPackage> mutualFundPackagesOne;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productTwo", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<MutualFundPackage> mutualFundPackagesTwo;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productThree", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<MutualFundPackage> mutualFundPackagesThree;

	
	@NotEmpty(message = "Nama reksadana tidak boleh kosong!")
	private String name;
	
	@NotEmpty(message = "Minimum beli tidak boleh kosong!")
	private int minimumBuy;

	@NotEmpty(message = "Jumlah dana kelolaan tidak boleh kosong!")
	private String totalFund;
	
	@NotEmpty(message = "Bank kustodian tidak boleh kosong!")
	private String custodyBank;
	
	@NotEmpty(message = "File prospectus tidak boleh kosong!")
	private String prospectusFile;
	
	@NotEmpty(message = "File fund fact sheet tidak boleh kosong!")
	private String factsheetFile;
	
	@NotNull(message = "Harga tidak boleh kosong!")
	private double lastPrice;
	
	@JsonFormat(timezone = "GMT+07:00")
	private Date lastUpdatePrice;
	
	@NotNull(message = "Stock tidak boleh kosong!")
	private double stock;
	
	private boolean isLimited;

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

	public List<PriceHistory> getPriceHistory() {
		return priceHistory;
	}

	public void setPriceHistory(List<PriceHistory> priceHistory) {
		this.priceHistory = priceHistory;
	}

	public boolean isLimited() {
		return isLimited;
	}

	public void setLimited(boolean isLimited) {
		this.isLimited = isLimited;
	}

	public double getStock() {
		return stock;
	}

	public void setStock(double stock) {
		this.stock = stock;
	}

	public MutualFundCategory getMutualFundCategory() {
		return mutualFundCategory;
	}

	public void setMutualFundCategory(MutualFundCategory mutualFundCategory) {
		this.mutualFundCategory = mutualFundCategory;
	}

	public MutualFundType getMutualFundType() {
		return mutualFundType;
	}

	public void setMutualFundType(MutualFundType mutualFundType) {
		this.mutualFundType = mutualFundType;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public List<Portfolio> getPortfolios() {
		return portfolios;
	}

	public void setPortfolios(List<Portfolio> portfolios) {
		this.portfolios = portfolios;
	}

	public List<MutualFundPackage> getMutualFundPackagesOne() {
		return mutualFundPackagesOne;
	}

	public void setMutualFundPackagesOne(List<MutualFundPackage> mutualFundPackagesOne) {
		this.mutualFundPackagesOne = mutualFundPackagesOne;
	}

	public List<MutualFundPackage> getMutualFundPackagesTwo() {
		return mutualFundPackagesTwo;
	}

	public void setMutualFundPackagesTwo(List<MutualFundPackage> mutualFundPackagesTwo) {
		this.mutualFundPackagesTwo = mutualFundPackagesTwo;
	}

	public List<MutualFundPackage> getMutualFundPackagesThree() {
		return mutualFundPackagesThree;
	}

	public void setMutualFundPackagesThree(List<MutualFundPackage> mutualFundPackagesThree) {
		this.mutualFundPackagesThree = mutualFundPackagesThree;
	}
}
