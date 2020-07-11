package com.thomasariyanto.octofund.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class TransactionStatus {
	@Id
	private int id;
	
	private String name;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "transactionStatus", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<TransactionStatus> transactionStatus;

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

	public List<TransactionStatus> getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(List<TransactionStatus> transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	
	
	
	
}
