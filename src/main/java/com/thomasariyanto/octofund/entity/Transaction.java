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

@Entity
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private int type;
	private Date date;
	private String memberName;
	private String productName;
	private String managerName;
	private String bankName;
	private long totalPrice;
	private double totalUnit;
	private String paymentProof;
	private String invoice;
	private String rejectMessage;
	
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "status_id")
	private TransactionStatus transactionStatus;
	
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "bank_account_id")
	private BankAccount bankAccount;
	
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public long getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(long totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getRejectMessage() {
		return rejectMessage;
	}
	public void setRejectMessage(String rejectMessage) {
		this.rejectMessage = rejectMessage;
	}
	public TransactionStatus getTransactionStatus() {
		return transactionStatus;
	}
	public void setTransactionStatus(TransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
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
	public BankAccount getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getPaymentProof() {
		return paymentProof;
	}
	public void setPaymentProof(String paymentProof) {
		this.paymentProof = paymentProof;
	}
	public String getInvoice() {
		return invoice;
	}
	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}
	public double getTotalUnit() {
		return totalUnit;
	}
	public void setTotalUnit(double totalUnit) {
		this.totalUnit = totalUnit;
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	
}
