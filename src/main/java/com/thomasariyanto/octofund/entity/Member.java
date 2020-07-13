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
import com.fasterxml.jackson.annotation.JsonManagedReference;


@Entity
public class Member {
	@Id
    @Column(name = "id")
    private int id;
	
	@OneToOne
    @MapsId
    @JsonBackReference(value="user-member")
    private User user;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Transaction> transactions;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Portfolio> portfolios;
	
	@Column(unique = true)
	private String sid;
	
	@Column(unique = true)
	private String ifua;
	
	@NotEmpty(message = "No KTP tidak boleh kosong")
	@Column(unique = true)
	private String identityNumber;
	
	@NotEmpty(message = "Tanggal lahir tidak boleh kosong!")
	private String birthDate;
	
	@NotEmpty(message = "Tempat lahir tidak boleh kosong!")
	private String birthPlace;
	
	@NotEmpty(message = "Jenis kelamin tidak boleh kosong!")
	private String sex;
	
	@NotEmpty(message = "Agama tidak boleh kosong!")
	private String religion;
	
	@NotEmpty(message = "Pekerjaan tidak boleh kosong!")
	private String job;
	
	@NotEmpty(message = "Status pernikahan tidak boleh kosong!")
	private String maritalStatus;
	
	@NotEmpty(message = "Alamat tidak boleh kosong!")
	private String address;
	
	private String identityName;
	
//	@NotEmpty(message = "Foto KTP harus diupload")
	private String identityPhoto;
	
//	@NotEmpty(message = "Foto selfie dengan KTP harus diupload")
	private String selfiePhoto;	
	
	private String signature;
	private int point;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getIfua() {
		return ifua;
	}
	public void setIfua(String ifua) {
		this.ifua = ifua;
	}
	public String getIdentityNumber() {
		return identityNumber;
	}
	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}
	public String getIdentityPhoto() {
		return identityPhoto;
	}
	public void setIdentityPhoto(String identityPhoto) {
		this.identityPhoto = identityPhoto;
	}
	public String getSelfiePhoto() {
		return selfiePhoto;
	}
	public void setSelfiePhoto(String selfiePhoto) {
		this.selfiePhoto = selfiePhoto;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	public String getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	public String getBirthPlace() {
		return birthPlace;
	}
	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getReligion() {
		return religion;
	}
	public void setReligion(String religion) {
		this.religion = religion;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getMaritalStatus() {
		return maritalStatus;
	}
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
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
	public String getIdentityName() {
		return identityName;
	}
	public void setIdentityName(String identityName) {
		this.identityName = identityName;
	}
	
}
