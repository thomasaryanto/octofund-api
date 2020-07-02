package com.thomasariyanto.octofund.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
public class Member {
	@Id
    @Column(name = "id")
	@JsonIgnore
    private int id;
	
	@OneToOne
    @MapsId
    @JsonBackReference
    private User user;
	
	@NotEmpty(message = "No KTP tidak boleh kosong")
	@Column(unique = true)
	private String identityNumber;
	
	@Column(unique = true)
	private String sid;
	
	@Column(unique = true)
	private String ifua;
	
	private String identityPhoto;
	private String selfiePhoto;	
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
	
	
	
}
