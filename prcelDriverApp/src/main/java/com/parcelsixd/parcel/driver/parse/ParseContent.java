package com.parcelsixd.parcel.driver.parse;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.parcelsixd.parcel.driver.R;
import com.parcelsixd.parcel.driver.db.DBHelper;
import com.parcelsixd.parcel.driver.maputills.PolyLineUtils;
import com.parcelsixd.parcel.driver.model.ApplicationPages;
import com.parcelsixd.parcel.driver.model.BeanRoute;
import com.parcelsixd.parcel.driver.model.BeanStep;
import com.parcelsixd.parcel.driver.model.History;
import com.parcelsixd.parcel.driver.model.RequestDetail;
import com.parcelsixd.parcel.driver.model.User;
import com.parcelsixd.parcel.driver.model.VehicalType;
import com.parcelsixd.parcel.driver.utills.AndyConstants;
import com.parcelsixd.parcel.driver.utills.AndyUtils;
import com.parcelsixd.parcel.driver.utills.AppLog;
import com.parcelsixd.parcel.driver.utills.MathUtils;
import com.parcelsixd.parcel.driver.utills.PreferenceHelper;
import com.parcelsixd.parcel.driver.utills.ReadFiles;

public class ParseContent {
	private Activity activity;
	private PreferenceHelper preferenceHelper;
	private final String KEY_SUCCESS = "success";
	private final String KEY_ERROR = "error";
	private final String IS_WALKER_STARTED = "is_walker_started";
	private final String IS_WALKER_ARRIVED = "is_walker_arrived";
	private final String IS_WALK_STARTED = "is_started";
	private final String IS_DOG_RATED = "is_dog_rated";
	private final String IS_WALK_COMPLETED = "is_completed";
	private final String IS_CANCELLED = "is_cancelled";
	private final String KEY_ERROR_CODE = "error_code";
	private final String PAYMENT_TYPE = "payment_type";
	private final String BASE_PRICE = "base_price";
	private final String TYPES = "types";
	private final String ID = "id";
	private final String ICON = "icon";
	// private final String IS_DEFAULT = "is_default";
	private final String PRICE_PER_UNIT_TIME = "price_per_unit_time";
	private final String PRICE_PER_UNIT_DISTANCE = "price_per_unit_distance";
	private final String KEY_ERROR_MESSAGES = "error_messages";

	public ParseContent(Activity activity) {
		this.activity = activity;
		preferenceHelper = new PreferenceHelper(activity);
	}

	/**
	 * @param applicationContext
	 */

	public BeanRoute parseRoute(String response, BeanRoute routeBean) {

		try {
			BeanStep stepBean;
			JSONObject jObject = new JSONObject(response);
			JSONArray jArray = jObject.getJSONArray("routes");
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject innerjObject = jArray.getJSONObject(i);
				if (innerjObject != null) {
					JSONArray innerJarry = innerjObject.getJSONArray("legs");
					for (int j = 0; j < innerJarry.length(); j++) {
						JSONObject jObjectLegs = innerJarry.getJSONObject(j);
						routeBean.setDistanceText(jObjectLegs.getJSONObject(
								"distance").getString("text"));
						routeBean.setDistanceValue(jObjectLegs.getJSONObject(
								"distance").getInt("value"));

						routeBean.setDurationText(jObjectLegs.getJSONObject(
								"duration").getString("text"));
						routeBean.setDurationValue(jObjectLegs.getJSONObject(
								"duration").getInt("value"));

						routeBean.setStartAddress(jObjectLegs
								.getString("start_address"));
						routeBean.setEndAddress(jObjectLegs
								.getString("end_address"));

						routeBean.setStartLat(jObjectLegs.getJSONObject(
								"start_location").getDouble("lat"));
						routeBean.setStartLon(jObjectLegs.getJSONObject(
								"start_location").getDouble("lng"));
						routeBean.setEndLat(jObjectLegs.getJSONObject(
								"end_location").getDouble("lat"));
						routeBean.setEndLon(jObjectLegs.getJSONObject(
								"end_location").getDouble("lng"));

						JSONArray jstepArray = jObjectLegs
								.getJSONArray("steps");
						if (jstepArray != null) {
							for (int k = 0; k < jstepArray.length(); k++) {
								stepBean = new BeanStep();
								JSONObject jStepObject = jstepArray
										.getJSONObject(k);
								if (jStepObject != null) {

									stepBean.setHtml_instructions(jStepObject
											.getString("html_instructions"));
									stepBean.setStrPoint(jStepObject
											.getJSONObject("polyline")
											.getString("points"));
									stepBean.setStartLat(jStepObject
											.getJSONObject("start_location")
											.getDouble("lat"));
									stepBean.setStartLon(jStepObject
											.getJSONObject("start_location")
											.getDouble("lng"));
									stepBean.setEndLat(jStepObject
											.getJSONObject("end_location")
											.getDouble("lat"));
									stepBean.setEndLong(jStepObject
											.getJSONObject("end_location")
											.getDouble("lng"));

									stepBean.setListPoints(new PolyLineUtils()
											.decodePoly(stepBean.getStrPoint()));
									routeBean.getListStep().add(stepBean);
								}
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return routeBean;
	}

	public boolean isSuccessWithId(String response) {
		if (TextUtils.isEmpty(response)) {
			return false;
		}
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				preferenceHelper.putUserId(jsonObject
						.getString(AndyConstants.Params.ID));
				preferenceHelper.putSessionToken(jsonObject
						.getString(AndyConstants.Params.TOKEN));
				preferenceHelper.putEmail(jsonObject
						.getString(AndyConstants.Params.EMAIL));
				// preferenceHelper.putEmail(jsonObject
				// .optString(AndyConstants.Params.EMAIL));
				preferenceHelper.putLoginBy(jsonObject
						.getString(AndyConstants.Params.LOGIN_BY));
				if (!preferenceHelper.getLoginBy().equalsIgnoreCase(
						AndyConstants.MANUAL)) {
					preferenceHelper.putSocialId(jsonObject
							.getString(AndyConstants.Params.SOCIAL_UNIQUE_ID));
				}

				/*if(jsonObject.getString(AndyConstants.Params.OTP_VERFIED).equals("0")){

					return false;
				}*/
				return true;
			} else {
				AndyUtils.showErrorToast(
						jsonObject.getJSONArray(KEY_ERROR_MESSAGES).getInt(0),
						activity);
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isSuccess(String response) {
		if (TextUtils.isEmpty(response))
			return false;
		try {
			JSONObject jsonObject = new JSONObject(response);

			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				return true;
			} else {
				// AndyUtils.showToast(jsonObject.getString(KEY_ERROR),
				// activity);
				AndyUtils.showToast(
						jsonObject.getString(KEY_ERROR_MESSAGES),
						activity);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public ArrayList<String> parseCountryCodes() {
		String response = "";
		ArrayList<String> list = new ArrayList<String>();
		try {
			response = ReadFiles.readRawFileAsString(activity,
					R.raw.countrycodes);

			JSONArray array = new JSONArray(response);
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				list.add(object.getString("phone-code") + " "
						+ object.getString("name"));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int getErrorCode(String response) {
		if (TextUtils.isEmpty(response))
			return 0;
		try {
			AppLog.Log("TAG", response);
			JSONObject jsonObject = new JSONObject(response);
			return jsonObject.getInt(KEY_ERROR_CODE);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int parseRequestInProgress(String response) {
		if (TextUtils.isEmpty(response)) {
			return AndyConstants.NO_REQUEST;
		}
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				int requestId = jsonObject
						.getInt(AndyConstants.Params.REQUEST_ID);
				preferenceHelper.putRequestId(requestId);
				return requestId;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return AndyConstants.NO_REQUEST;
	}

	public boolean parseIsApproved(String response) {
		if (TextUtils.isEmpty(response)) {
			return false;
		}
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				if (jsonObject.getString(AndyConstants.Params.IS_APPROVED)
						.equals("1")) {
					return true;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return false;
	}

	@SuppressWarnings("deprecation")
	public RequestDetail parseRequestStatus(String response) {
		if (TextUtils.isEmpty(response)) {
			return null;
		}
		RequestDetail requestDetail = new RequestDetail();
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {

				requestDetail.setJobStatus(AndyConstants.IS_ASSIGNED);
				JSONObject object = jsonObject
						.getJSONObject(AndyConstants.Params.REQUEST);
				if (object.getInt(IS_CANCELLED) == 1) {
					requestDetail.setJobStatus(AndyConstants.NO_REQUEST);
				} else if (object.getInt(IS_WALKER_STARTED) == 0) {
					requestDetail.setJobStatus(AndyConstants.IS_WALKER_STARTED);
					// status = AndyConstants.IS_WALKER_STARTED;
				} else if (object.getInt(IS_WALKER_ARRIVED) == 0) {
					requestDetail.setJobStatus(AndyConstants.IS_WALKER_ARRIVED);
					// status = AndyConstants.IS_WALKER_ARRIVED;
				} else if (object.getInt(IS_WALK_STARTED) == 0) {
					requestDetail.setJobStatus(AndyConstants.IS_WALK_STARTED);
					// status = AndyConstants.IS_WALK_STARTED;
				} else if (object.getInt(IS_WALK_COMPLETED) == 0) {
					preferenceHelper.putIsTripStart(true);
					requestDetail.setJobStatus(AndyConstants.IS_WALK_COMPLETED);

					// status = AndyConstants.IS_WALK_COMPLETED;
				} else if (object.getInt(IS_DOG_RATED) == 0) {
					requestDetail.setJobStatus(AndyConstants.IS_DOG_RATED);
					// status = AndyConstants.IS_DOG_RATED;
				}

				String time = object.optString(AndyConstants.Params.START_TIME);
				// "start_time": "2014-11-20 03:27:37"
				if (!TextUtils.isEmpty(time)) {
					try {
						TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
						Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
								Locale.ENGLISH).parse(time);
						AppLog.Log("TAG", "START DATE---->" + date.toString()
								+ " month:" + date.getMonth());

						TimeZone timezone = TimeZone.getDefault();
						SimpleDateFormat dateformat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						dateformat.setTimeZone(timezone);
						dateformat.format(date);

						preferenceHelper.putRequestTime(date.getTime());
						requestDetail.setStartTime(date.getTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}

				preferenceHelper.putPaymentType(object.getInt("payment_type"));
				try {
					if (object.getString("dest_latitude").length() != 0) {
						preferenceHelper.putClientDestination(new LatLng(object
								.getDouble("dest_latitude"), object
								.getDouble("dest_longitude")));
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}

				JSONObject ownerDetailObject = object
						.getJSONObject(AndyConstants.Params.OWNER);
				requestDetail.setClientName(ownerDetailObject
						.getString(AndyConstants.Params.NAME));
				requestDetail.setClientProfile(ownerDetailObject
						.getString(AndyConstants.Params.PICTURE));
				requestDetail.setClientPhoneNumber(ownerDetailObject
						.getString(AndyConstants.Params.PHONE));
				requestDetail.setClientRating((float) ownerDetailObject
						.optDouble(AndyConstants.Params.RATING));
				requestDetail.setClientLatitude(ownerDetailObject
						.getString(AndyConstants.Params.LATITUDE));
				requestDetail.setClientLongitude(ownerDetailObject
						.getString(AndyConstants.Params.LONGITUDE));
				if(object.has(AndyConstants.Params.UNIT))
				requestDetail.setUnit(object
						.getString(AndyConstants.Params.UNIT));

				requestDetail.setPackage_type(ownerDetailObject
						.getString("package_type"));
				requestDetail.setBooking_date(ownerDetailObject
						.getString("booking_date"));
				requestDetail.setSrc_address(ownerDetailObject
						.getString("src_address"));
				requestDetail.setDest_address(ownerDetailObject
						.getString("dest_address"));
				requestDetail.setTotal(ownerDetailObject
						.getString("total"));
				requestDetail.setRequest_id(ownerDetailObject
						.getString("request_id"));

				requestDetail.setDistance(ownerDetailObject
						.getString("distance"));

				JSONObject jsonObjectBill = object.optJSONObject("bill");

				if (jsonObjectBill != null) {
					requestDetail.setAmount(jsonObjectBill.getString("total"));
					requestDetail.setTime(jsonObjectBill.getString("time"));
					requestDetail.setDistance(jsonObjectBill
							.getString("distance"));
					requestDetail.setBasePrice(jsonObjectBill
							.getString(BASE_PRICE));
					requestDetail.setDistanceCost(jsonObjectBill
							.getString(AndyConstants.Params.DISTANCE_COST));
					requestDetail.setTimecost(jsonObjectBill
							.getString(AndyConstants.Params.TIME_COST));
					requestDetail.setReferralBonus(jsonObjectBill
							.getString(AndyConstants.Params.REFERRAL_BONUS));
					requestDetail.setPromoBonus(jsonObjectBill
							.getString(AndyConstants.Params.PROMO_BONUS));
					requestDetail.setTotal(new DecimalFormat("0.00")
							.format(Double.parseDouble(jsonObjectBill
									.getString(AndyConstants.Params.TOTAL))));
					requestDetail.setPricePerDistance(jsonObjectBill
							.getString(PRICE_PER_UNIT_DISTANCE));
					requestDetail.setPricePerTime(jsonObjectBill
							.getString(PRICE_PER_UNIT_TIME));
					requestDetail.setPayment_type(jsonObjectBill.getInt(PAYMENT_TYPE));
				}
			} else {
				requestDetail.setUnit(jsonObject
						.getString(AndyConstants.Params.UNIT));
				requestDetail.setDestinationLatitude(jsonObject
						.getString(AndyConstants.Params.DESTINATION_LATITUDE));
				requestDetail.setDestinationLongitude(jsonObject
						.getString(AndyConstants.Params.DESTINATION_LONGITUDE));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return requestDetail;
	}

	public RequestDetail parseAllRequests(String response) {
		if (TextUtils.isEmpty(response)) {
			return null;
		}
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				JSONArray jsonArray = jsonObject
						.getJSONArray(AndyConstants.Params.INCOMING_REQUESTS);
				if (jsonArray.length() > 0) {
					JSONObject object = jsonArray.getJSONObject(0);



					if (object.getInt(AndyConstants.Params.REQUEST_ID) != AndyConstants.NO_REQUEST) {

						RequestDetail requestDetail = new RequestDetail();
						requestDetail.setRequestId(object
								.getInt(AndyConstants.Params.REQUEST_ID));
						int timeto_respond = object
								.getInt(AndyConstants.Params.TIME_LEFT_TO_RESPOND);
						/*if (timeto_respond < 0) {
							return null;
						} else {
							requestDetail.setTimeLeft(timeto_respond);
						}*/
						requestDetail.setTimeLeft(timeto_respond);

						JSONObject requestData = object
								.getJSONObject(AndyConstants.Params.REQUEST_DATA);
						JSONObject ownerDetailObject = requestData
								.getJSONObject(AndyConstants.Params.OWNER);
						requestDetail.setClientName(ownerDetailObject
								.getString(AndyConstants.Params.NAME));
						requestDetail.setClientProfile(ownerDetailObject
								.getString(AndyConstants.Params.PICTURE));
						requestDetail.setClientPhoneNumber(ownerDetailObject
								.getString(AndyConstants.Params.PHONE));
						if (!TextUtils.isEmpty(ownerDetailObject
								.getString(AndyConstants.Params.RATING))) {
							requestDetail
									.setClientRating((float) ownerDetailObject
											.getDouble(AndyConstants.Params.RATING));
						} else {
							requestDetail.setClientRating(0);
						}
						requestDetail.setClientLatitude(ownerDetailObject
								.getString(AndyConstants.Params.LATITUDE));
						requestDetail.setClientLongitude(ownerDetailObject
								.getString(AndyConstants.Params.LONGITUDE));
						preferenceHelper.putPaymentType(ownerDetailObject
								.getInt("payment_type"));

						if(object.has(AndyConstants.Params.UNIT))
						preferenceHelper.putUnit(object
								.getString(AndyConstants.Params.UNIT));



						requestDetail.setPackage_type(ownerDetailObject
								.getString("package_type"));
						requestDetail.setBooking_date(ownerDetailObject
								.getString("booking_date"));
						requestDetail.setSrc_address(ownerDetailObject
								.getString("src_address"));
						requestDetail.setDest_address(ownerDetailObject
								.getString("dest_address"));
						requestDetail.setTotal(ownerDetailObject
								.getString("total"));
						requestDetail.setRequest_id(ownerDetailObject
								.getString("request_id"));

						requestDetail.setDistance(ownerDetailObject
								.getString("distance"));


						LatLng destLatLng = new LatLng(
								ownerDetailObject.getDouble("dest_latitude"),
								ownerDetailObject.getDouble("dest_longitude"));
						preferenceHelper.putClientDestination(destLatLng);
						return requestDetail;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public User parseUserAndStoreToDb(String response) {
		User user = null;
		try {
			JSONObject jsonObject = new JSONObject(response);

			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				user = new User();
				DBHelper dbHelper = new DBHelper(activity);

				user.setUser_id(jsonObject
						.getString(AndyConstants.Params.USERID));
				user.setFull_name(jsonObject
						.getString(AndyConstants.Params.FULLNAME));
				user.setFirst_name(jsonObject
						.getString(AndyConstants.Params.FIRSTNAME));
				user.setLast_name(jsonObject
						.getString(AndyConstants.Params.LAST_NAME));
				user.setPhone(jsonObject
						.getString(AndyConstants.Params.PHONE));
				user.setEmail(jsonObject
						.getString(AndyConstants.Params.EMAIL));
				user.setPicture(jsonObject
						.getString(AndyConstants.Params.PICTURE));
				user.setBio(jsonObject
						.getString(AndyConstants.Params.BIO));
				user.setAddress(jsonObject
						.getString(AndyConstants.Params.ADDRESS));
				user.setState(jsonObject
						.getString(AndyConstants.Params.STATE));
				user.setCountry(jsonObject
						.getString(AndyConstants.Params.COUNTRY));
				user.setZipcode(jsonObject
						.getString(AndyConstants.Params.ZIPCODE));
				user.setLogin_by(jsonObject
						.getString(AndyConstants.Params.LOGIN_BY));
				user.setSocial_unique_id(jsonObject
						.getString(AndyConstants.Params.SOCIAL_UNIQUE_ID));
				user.setIs_approved(jsonObject
						.getString(AndyConstants.Params.IS_APPROVED));
				user.setCar_model(jsonObject
						.getString(AndyConstants.Params.CAR_MODEL));
				user.setCar_number(jsonObject
						.getString(AndyConstants.Params.CAR_NUMBER));
				user.setIs_approved_txt(jsonObject
						.getString(AndyConstants.Params.IS_APPROVED_TXT));
				user.setIs_available(jsonObject
						.getString(AndyConstants.Params.IS_AVAILABLE));
				user.setOtp_verified(jsonObject
						.getString(AndyConstants.Params.OTP_VERFIED));

				user.setCar_make(jsonObject
						.getString(AndyConstants.Params.CAR_MAKE));
				user.setCar_year(jsonObject
						.getString(AndyConstants.Params.CAR_YEAR));

				user.setPackage_type(jsonObject
						.getString(AndyConstants.Params.PACKAGE_TYPE));



				dbHelper.createUser(user);

			} else {
				// AndyUtils.showToast(jsonObject.getString(KEY_ERROR),
				// activity);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return user;
	}

	// public boolean isSuccess(String response) {
	// if (TextUtils.isEmpty(response)) {
	// return false;
	// }
	// try {
	// JSONObject jsonObject = new JSONObject(response);
	// if (jsonObject.getBoolean(KEY_SUCCESS)) {
	// return true;
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// return false;
	//
	// }
	public RequestDetail parseNotification(String response) {
		if (TextUtils.isEmpty(response)) {
			return null;
		}
		try {

			JSONObject object = new JSONObject(response);

			if (object.getInt(AndyConstants.Params.REQUEST_ID) != AndyConstants.NO_REQUEST) {
				RequestDetail requestDetail = new RequestDetail();
				requestDetail.setRequestId(object
						.getInt(AndyConstants.Params.REQUEST_ID));
				int timeto_respond = object
						.getInt(AndyConstants.Params.TIME_LEFT_TO_RESPOND);
				if (timeto_respond < 0) {
					return null;
				} else {
					requestDetail.setTimeLeft(timeto_respond);
				}

				JSONObject requestData = object
						.getJSONObject(AndyConstants.Params.REQUEST_DATA);
				JSONObject ownerDetailObject = requestData
						.getJSONObject(AndyConstants.Params.OWNER);
				requestDetail.setClientName(ownerDetailObject
						.getString(AndyConstants.Params.NAME));
				requestDetail.setClientProfile(ownerDetailObject
						.getString(AndyConstants.Params.PICTURE));
				requestDetail.setClientPhoneNumber(ownerDetailObject
						.getString(AndyConstants.Params.PHONE));
				requestDetail.setClientRating((float) ownerDetailObject
						.getDouble(AndyConstants.Params.RATING));
				requestDetail.setClientLatitude(ownerDetailObject
						.getString(AndyConstants.Params.LATITUDE));
				requestDetail.setClientLongitude(ownerDetailObject
						.getString(AndyConstants.Params.LONGITUDE));
				preferenceHelper.putPaymentType(ownerDetailObject
						.getInt("payment_type"));
				preferenceHelper.putUnit(object
						.getString(AndyConstants.Params.UNIT));
				LatLng destLatLng = new LatLng(
						ownerDetailObject.getDouble("owner_dist_lat"),
						ownerDetailObject.getDouble("owner_dist_long"));
				preferenceHelper.putClientDestination(destLatLng);
				return requestDetail;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

	public ArrayList<ApplicationPages> parsePages(
			ArrayList<ApplicationPages> list, String response) {
		list.clear();
		ApplicationPages applicationPages = new ApplicationPages();
		applicationPages.setId(-1);
		applicationPages.setTitle(activity.getResources().getString(
				R.string.text_home));
		applicationPages.setData("");
		applicationPages.setIcon("");
		list.add(applicationPages);

		 applicationPages = new ApplicationPages();
		applicationPages.setId(-2);
		applicationPages.setTitle(activity.getResources().getString(
				R.string.text_profile));
		applicationPages.setData("");
		applicationPages.setIcon("");
		list.add(applicationPages);

		applicationPages = new ApplicationPages();
		applicationPages.setId(-3);
		applicationPages.setTitle(activity.getResources().getString(
				R.string.text_history));

		applicationPages.setData("");
		applicationPages.setIcon("");
		list.add(applicationPages);

		/*applicationPages = new ApplicationPages();
		applicationPages.setId(-4);
		applicationPages.setTitle(activity.getResources().getString(
				R.string.text_share));
		applicationPages.setData("");
		applicationPages.setIcon("");
		list.add(applicationPages);*/
		if (TextUtils.isEmpty(response)) {
			return list;
		}
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				JSONArray jsonArray = jsonObject
						.getJSONArray(AndyConstants.Params.INFORMATIONS);
				if (jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						applicationPages = new ApplicationPages();
						JSONObject object = jsonArray.getJSONObject(i);
						applicationPages.setId(object
								.getInt(AndyConstants.Params.ID));
						applicationPages.setTitle(object
								.getString(AndyConstants.Params.TITLE));
						applicationPages.setData(object
								.getString(AndyConstants.Params.CONTENT));
						applicationPages.setIcon(object
								.getString(AndyConstants.Params.ICON));
						list.add(applicationPages);
					}
				}

			}
			// else {
			// AndyUtils.showToast(jsonObject.getString(KEY_ERROR), activity);
			// }
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean checkDriverStatus(String response) {
		if (TextUtils.isEmpty(response))
			return false;
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				if (jsonObject.getInt(AndyConstants.Params.IS_ACTIVE) == 0) {
					return false;
				} else {
					return true;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public ArrayList<VehicalType> parseTypes(String response,
			ArrayList<VehicalType> list) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				JSONArray jsonArray = jsonObject.getJSONArray(TYPES);
				for (int i = 0; i < jsonArray.length(); i++) {
					VehicalType type = new VehicalType();
					JSONObject typeJson = jsonArray.getJSONObject(i);
					type.setBasePrice(typeJson.getString(BASE_PRICE));
					type.setIcon(typeJson.getString(ICON));
					type.setId(typeJson.getInt(ID));
					type.setName(typeJson.getString(AndyConstants.Params.NAME));
					type.setPricePerUnitDistance(typeJson
							.getString(PRICE_PER_UNIT_DISTANCE));
					type.setPricePerUnitTime(typeJson
							.getString(PRICE_PER_UNIT_TIME));
					list.add(type);

				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;

	}

	public ArrayList<History> parseHistory(String response,
			ArrayList<History> list) {
		/*list.clear();

		if (TextUtils.isEmpty(response)) {
			return list;
		}
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				JSONArray jsonArray = jsonObject
						.getJSONArray(AndyConstants.Params.REQUESTS);
				if (jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object = jsonArray.getJSONObject(i);
						History history = new History();
						history.setId(object.getInt(AndyConstants.Params.ID));
						history.setSourceLat(object
								.getString(AndyConstants.Params.SOURCE_LAT));
						history.setSourceLong(object
								.getString(AndyConstants.Params.SOURCE_LONG));
						history.setDestLat(object
								.getString(AndyConstants.Params.DEST_LAT));
						history.setDestLong(object
								.getString(AndyConstants.Params.DEST_LONG));
						history.setSrcAdd(object
								.getString(AndyConstants.Params.SRC_ADD));
						history.setDestAdd(object
								.getString(AndyConstants.Params.DEST_ADD));
						history.setMapImage(object
								.getString(AndyConstants.Params.MAP_URL));
						history.setDate(object
								.getString(AndyConstants.Params.DATE));
						history.setDistance(object
								.getString(AndyConstants.Params.DISTANCE));
						history.setUnit(object
								.getString(AndyConstants.Params.UNIT));
						history.setTime(object
								.getString(AndyConstants.Params.TIME));
						history.setBasePrice(object.getString(BASE_PRICE));
						history.setDistanceCost(object
								.getString(AndyConstants.Params.DISTANCE_COST));
						history.setTimecost(object
								.getString(AndyConstants.Params.TIME_COST));
						history.setReferralBonus(object
								.getString(AndyConstants.Params.REFERRAL_BONUS));
						history.setPromoBonus(object
								.getString(AndyConstants.Params.PROMO_BONUS));
						history.setTotal(new DecimalFormat("0.00").format(Double
								.parseDouble(object
										.getString(AndyConstants.Params.TOTAL))));
						history.setCurrency(object
								.getString(AndyConstants.Params.CURRENCY));
						history.setPricePerDistance(object
								.getString(PRICE_PER_UNIT_DISTANCE));
						history.setPricePerTime(object
								.getString(PRICE_PER_UNIT_TIME));

						JSONObject userObject = object
								.getJSONObject(AndyConstants.Params.OWNER);
						history.setFirstName(userObject
								.getString(AndyConstants.Params.FIRSTNAME));
						history.setLastName(userObject
								.getString(AndyConstants.Params.LAST_NAME));
						history.setPhone(userObject
								.getString(AndyConstants.Params.PHONE));
						history.setPicture(userObject
								.getString(AndyConstants.Params.PICTURE));
						history.setEmail(userObject
								.getString(AndyConstants.Params.EMAIL));
						history.setBio(userObject
								.getString(AndyConstants.Params.BIO));
						list.add(history);
					}
				}

			}
			// else {
			// AndyUtils.showToast(jsonObject.getString(KEY_ERROR), activity);
			// }
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
		return list;
	}


	public ArrayList<History> parseDeliveryHistory(String response,
										   ArrayList<History> list) {
		list.clear();

		if (TextUtils.isEmpty(response)) {
			return list;
		}
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				JSONArray jsonArray = jsonObject
						.getJSONArray("data");
				if (jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object = jsonArray.getJSONObject(i);
						History history = new History();

						history.setPackage_id(object.getString("package_id"));
						history.setPackage_type(object.getString("package_type"));
						history.setBooking_date(object.getString("booking_date"));
						history.setBooking_time(object.getString("booking_time"));
						history.setDelivery_date(object.getString("delivery_date"));
						history.setDelivery_time(object.getString("delivery_time"));
						history.setPickup_address(object.getString("pickup_address"));
						history.setDestination_address(object.getString("destination_address"));
						history.setPrice(object.getString("price"));
						history.setOwner_name(object.getString("owner_name"));
						list.add(history);

					}
				}

			}
			// else {
			// AndyUtils.showToast(jsonObject.getString(KEY_ERROR), activity);
			// }
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * @param response
	 * @return
	 */
	public boolean parseAvaibilty(String response) {
		if (TextUtils.isEmpty(response))
			return false;
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				if (jsonObject.getInt(AndyConstants.Params.IS_ACTIVE) == 1) {
					return true;
				}
			}

			// else {
			// AndyUtils.showToast(jsonObject.getString(KEY_ERROR), activity);
			// }
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @param response
	 * @param points
	 */
	public ArrayList<LatLng> parsePathRequest(String response,
			ArrayList<LatLng> points) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				JSONArray jsonArray = jsonObject
						.getJSONArray(AndyConstants.Params.LOCATION_DATA);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject json = jsonArray.getJSONObject(i);
					points.add(new LatLng(Double.parseDouble(json
							.getString(AndyConstants.Params.LATITUDE)), Double
							.parseDouble(json
									.getString(AndyConstants.Params.LONGITUDE))));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return points;
	}

	public String parseNearestDriverDurationString(String response) {
		if (TextUtils.isEmpty(response))
			return "NA";
		try {
			JSONArray jsonArray = new JSONObject(response).getJSONArray("rows");
			JSONArray jArrSub = jsonArray.getJSONObject(0).getJSONArray(
					"elements");
			return jArrSub.getJSONObject(0).getJSONObject("duration")
					.getString("text");
		} catch (JSONException e) {
			e.printStackTrace();
			return "NA";
		}
	}

	public String parseNearestDriverDistanceString(String response) {
		if (TextUtils.isEmpty(response))
			return "NA";
		try {
			JSONArray jsonArray = new JSONObject(response).getJSONArray("rows");
			JSONArray jArrSub = jsonArray.getJSONObject(0).getJSONArray(
					"elements");
			double distance;
			if (preferenceHelper.getUnit().equals("kms")) {
				distance = jArrSub.getJSONObject(0).getJSONObject("distance")
						.getDouble("value") / 1000;
			} else {
				distance = jArrSub.getJSONObject(0).getJSONObject("distance")
						.getDouble("value") / 1609.34;
			}
			return MathUtils.getRound(distance) + " "
					+ preferenceHelper.getUnit();
		} catch (JSONException e) {
			e.printStackTrace();
			return "NA";
		}
	}
}
