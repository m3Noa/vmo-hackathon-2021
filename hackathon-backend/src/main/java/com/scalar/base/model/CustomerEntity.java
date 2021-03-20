package com.scalar.base.model;

import java.text.ParseException;
import java.util.Date;

import com.scalar.base.utils.BaseUtils;

public class CustomerEntity {

	private String id;
	private String firstName;
	private String lastName;
	private String address;
	private String mobilephone;

	private int isDeleted;
	private int isActive;

	private String createBy;
	private String updateBy;
	private Date createAt;
	private Date updateAt;

	public CustomerEntity() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMobilephone() {
		return mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}

	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(String createAt) throws ParseException {
		this.createAt = BaseUtils.stringToDate(createAt);
	}

	public Date getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(String updateAt) throws ParseException {
		this.updateAt = BaseUtils.stringToDate(updateAt);
	}

}
