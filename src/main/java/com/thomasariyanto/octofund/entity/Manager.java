package com.thomasariyanto.octofund.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Manager {
	@Id
    @Column(name = "id")
    private int id;
	
	@OneToOne
    @MapsId
    @JsonBackReference(value="user-manager")
    private User user;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "manager", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<MutualFund> mutualFunds;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "manager", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<MutualFundPackage> mutualFundPackages;
	
	@NotEmpty(message = "Nama perusahaan tidak boleh kosong!")
	private String companyName;
	
	@NotEmpty(message = "Alamat website tidak boleh kosong!")
	private String website;
	
	private String logo;

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public List<MutualFund> getMutualFunds() {
		return mutualFunds;
	}

	public void setMutualFunds(List<MutualFund> mutualFunds) {
		this.mutualFunds = mutualFunds;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public List<MutualFundPackage> getMutualFundPackages() {
		return mutualFundPackages;
	}

	public void setMutualFundPackages(List<MutualFundPackage> mutualFundPackages) {
		this.mutualFundPackages = mutualFundPackages;
	}
	
	
}
