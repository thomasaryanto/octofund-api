package com.thomasariyanto.octofund.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Portfolio {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private long totalInvest;
	private double totalUnit;
	
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "member_id")
    private Member member;
	
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "product_id")
    private MutualFund mutualFund;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTotalInvest() {
		return totalInvest;
	}

	public void setTotalInvest(long totalInvest) {
		this.totalInvest = totalInvest;
	}

	public double getTotalUnit() {
		return totalUnit;
	}

	public void setTotalUnit(double totalUnit) {
		this.totalUnit = totalUnit;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public MutualFund getMutualFund() {
		return mutualFund;
	}

	public void setMutualFund(MutualFund mutualFund) {
		this.mutualFund = mutualFund;
	}

}
