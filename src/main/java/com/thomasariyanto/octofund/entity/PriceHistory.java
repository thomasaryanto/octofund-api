package com.thomasariyanto.octofund.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@JsonInclude(Include.NON_NULL)
public class PriceHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "product_id")
	@JsonBackReference(value="mutualfund-pricehistory")
    private MutualFund mutualFund;
	
	private Date date;
	private double price;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public MutualFund getMutualFund() {
		return mutualFund;
	}
	public void setMutualFund(MutualFund mutualFund) {
		this.mutualFund = mutualFund;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
}
