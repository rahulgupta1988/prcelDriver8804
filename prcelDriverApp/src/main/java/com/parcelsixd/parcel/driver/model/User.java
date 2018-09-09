package com.parcelsixd.parcel.driver.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable {

	private String user_id;
	/*private String fname, lname, contact, address, bio, zipcode, email,
			picture, carModel, carNumber;*/

	private String full_name,first_name,last_name,phone,email,picture,bio,address,state,country,zipcode,login_by,social_unique_id,
			is_approved,car_model,is_approved_txt,is_available,otp_verified,car_number,car_make,car_year,package_type;

	public String getCar_number() {
		return car_number;
	}

	public String getPackage_type() {
		return package_type;
	}

	public void setPackage_type(String package_type) {
		this.package_type = package_type;
	}

	public String getCar_year() {
		return car_year;
	}

	public void setCar_year(String car_year) {
		this.car_year = car_year;
	}

	public String getUser_id() {
		return user_id;
	}

	public String getCar_make() {
		return car_make;
	}

	public void setCar_make(String car_make) {
		this.car_make = car_make;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public void setCar_number(String car_number) {
		this.car_number = car_number;

	}



	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getLogin_by() {
		return login_by;
	}

	public void setLogin_by(String login_by) {
		this.login_by = login_by;
	}

	public String getSocial_unique_id() {
		return social_unique_id;
	}

	public void setSocial_unique_id(String social_unique_id) {
		this.social_unique_id = social_unique_id;
	}

	public String getIs_approved() {
		return is_approved;
	}

	public void setIs_approved(String is_approved) {
		this.is_approved = is_approved;
	}

	public String getCar_model() {
		return car_model;
	}

	public void setCar_model(String car_model) {
		this.car_model = car_model;
	}

	public String getIs_approved_txt() {
		return is_approved_txt;
	}

	public void setIs_approved_txt(String is_approved_txt) {
		this.is_approved_txt = is_approved_txt;
	}

	public String getIs_available() {
		return is_available;
	}

	public void setIs_available(String is_available) {
		this.is_available = is_available;
	}

	public String getOtp_verified() {
		return otp_verified;
	}

	public void setOtp_verified(String otp_verified) {
		this.otp_verified = otp_verified;
	}
}
