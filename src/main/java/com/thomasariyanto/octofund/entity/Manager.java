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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Manager {
	@Id
    @Column(name = "id")
    private int id;
	
	@OneToOne
    @MapsId
    @JsonBackReference
    private User user;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "manager", cascade = CascadeType.ALL)
	@JsonBackReference
	private List<MutualFund> mutualFunds;
	
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
	
}
