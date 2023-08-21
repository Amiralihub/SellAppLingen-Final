package com.example.sellapplingen;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlacedOrder implements Serializable {
    private String orderID;
    private String timestamp;
    private String employeeName;
    private String packageSize;
    private String deliveryDate;
    private String customDropOffPlace;
    private String handlingInfo;
    private String firstName;
    private String lastName;
    private String street;
    private String houseNumber;
    private String zip;


    public PlacedOrder(String orderID, String timestamp, String employeeName, String packageSize, String deliveryDate,
                 String customDropOffPlace, String handlingInfo, String firstName, String lastName,
                 String street, String houseNumber, String zip) {
        this.orderID = orderID;
        this.timestamp = timestamp;
        this.employeeName = employeeName;
        this.packageSize = packageSize;
        this.deliveryDate = deliveryDate;
        this.customDropOffPlace = customDropOffPlace;
        this.handlingInfo = handlingInfo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.houseNumber = houseNumber;
        this.zip = zip;

    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID='" + orderID + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", packageSize='" + packageSize + '\'' +
                ", deliveryDate='" + deliveryDate + '\'' +
                ", customDropOffPlace='" + customDropOffPlace + '\'' +
                ", handlingInfo='" + handlingInfo + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", zip='" + zip + '\'' +
                '}';
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
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

    public String getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(String packageSize) {
        this.packageSize = packageSize;
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

    public String getHandlingInfo() {
        return handlingInfo;
    }

    public void setHandlingInfo(String handlingInfo) {
        this.handlingInfo = handlingInfo;
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


    public static String formatDate(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");

        try {
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Return an empty string or handle the error as needed
        }
    }


}
