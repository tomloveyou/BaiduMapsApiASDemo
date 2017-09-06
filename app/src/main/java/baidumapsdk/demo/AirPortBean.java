package baidumapsdk.demo;

import java.io.Serializable;

import com.baidu.mapapi.model.LatLng;

public class AirPortBean implements Serializable {
	private int id;
	private int grade;
	private int type;
	private Double latitude;
	private Double longitude;
	private String city;
	private String code3;
	private String code4;
	private String name;
	private String province;
	private String weathercode1;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCode3() {
		return code3;
	}

	public void setCode3(String code3) {
		this.code3 = code3;
	}

	public String getCode4() {
		return code4;
	}

	public void setCode4(String code4) {
		this.code4 = code4;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getWeathercode1() {
		return weathercode1;
	}

	public void setWeathercode1(String weathercode1) {
		this.weathercode1 = weathercode1;
	}

}
