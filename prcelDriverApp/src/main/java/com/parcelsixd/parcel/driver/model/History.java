/**
 * 
 */
package com.parcelsixd.parcel.driver.model;

import java.io.Serializable;

/**
 * @author Elluminati elluminati.in
 * 
 */
@SuppressWarnings("serial")
public class History implements Serializable {

	private String package_id="",package_type="",booking_date="",booking_time="",delivery_date="",delivery_time="",
			pickup_address="",destination_address="",price="",owner_name="";

	public String getPackage_type() {
		return package_type;
	}

	public void setPackage_type(String package_type) {
		this.package_type = package_type;
	}

	public String getPackage_id() {
		return package_id;
	}

	public void setPackage_id(String package_id) {
		this.package_id = package_id;
	}

	public String getBooking_date() {
		return booking_date;
	}

	public void setBooking_date(String booking_date) {
		this.booking_date = booking_date;
	}

	public String getBooking_time() {
		return booking_time;
	}

	public void setBooking_time(String booking_time) {
		this.booking_time = booking_time;
	}

	public String getDelivery_date() {
		return delivery_date;
	}

	public void setDelivery_date(String delivery_date) {
		this.delivery_date = delivery_date;
	}

	public String getDelivery_time() {
		return delivery_time;
	}

	public void setDelivery_time(String delivery_time) {
		this.delivery_time = delivery_time;
	}

	public String getPickup_address() {
		return pickup_address;
	}

	public void setPickup_address(String pickup_address) {
		this.pickup_address = pickup_address;
	}

	public String getDestination_address() {
		return destination_address;
	}

	public void setDestination_address(String destination_address) {
		this.destination_address = destination_address;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getOwner_name() {
		return owner_name;
	}

	public void setOwner_name(String owner_name) {
		this.owner_name = owner_name;
	}

	@Override
	public boolean equals(Object object) {
		if (object != null && object instanceof History) {
			return this.delivery_date == ((History) object).getDelivery_date();
		}
		return false;
	}


}
