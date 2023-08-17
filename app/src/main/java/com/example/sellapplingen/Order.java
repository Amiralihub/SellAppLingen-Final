// Order.java
package com.example.sellapplingen;
import java.io.Serializable;

public class Order implements Serializable {
    private String token;
    private String timestamp;
    private String employeeName;
    private String firstName;
    private String lastName;
    private String street;
    private String houseNumber;
    private String zip;
    private String city;
    private String numberPackage;
    private String packageSize;
    private String handlingInfo;
    private String deliveryDate;
    private String customDropOffPlace;

    public Order() {

    }




    public Order(String token, String timestamp, String employeeName, String firstName, String lastName,
                 String street, String houseNumber, String zip, String city, String numberPackage,
                 String packageSize, String handlingInfo, String deliveryDate, String customDropOffPlace) {
        this.token = token;
        this.timestamp = timestamp;
        this.employeeName = employeeName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.houseNumber = houseNumber;
        this.zip = zip;
        this.city = city;
        this.numberPackage = numberPackage;
        this.packageSize = packageSize;
        this.handlingInfo = handlingInfo;
        this.deliveryDate = deliveryDate;
        this.customDropOffPlace = customDropOffPlace;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNumberPackage() {
        return numberPackage;
    }

    public void setNumberPackage(String numberPackage) {
        this.numberPackage = numberPackage;
    }

    public String getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(String packageSize) {
        this.packageSize = packageSize;
    }

    public String getHandlingInfo() {
        return handlingInfo;
    }

    public void setHandlingInfo(String handlingInfo) {
        this.handlingInfo = handlingInfo;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getCustomDropOffPlace() {
        return customDropOffPlace;
    }

    public void setCustomDropOffPlace(String customDropOffPlace) {
        this.customDropOffPlace = customDropOffPlace;
    }

    @Override
    public String toString() {
        return "Order{" +
                "token='" + token + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", zip='" + zip + '\'' +
                ", city='" + city + '\'' +
                ", numberPackage='" + numberPackage + '\'' +
                ", packageSize='" + packageSize + '\'' +
                ", handlingInfo='" + handlingInfo + '\'' +
                ", deliveryDate='" + deliveryDate + '\'' +
                ", customDropOffPlace='" + customDropOffPlace + '\'' +
                '}';
    }
}
