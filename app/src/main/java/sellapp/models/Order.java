// Order.java
package sellapp.models;
import java.io.Serializable;

public class Order implements Serializable
{
    private String orderID;
    private String timestamp;
    private String employeeName;
    private Recipient recipient;
    private String packageSize;
    private String handlingInfo;
    private String deliveryDate;
    private String customDropOffPlace;

    public Order()
    {
    }

    public Order(String orderID, String timestamp, Recipient recipient,
                 String packageSize, String handlingInfo, String deliveryDate, String customDropOffPlace)
    {
        this.orderID = orderID;
        this.timestamp = timestamp;
        this.recipient = recipient;
        this.employeeName = employeeName;
        this.packageSize = packageSize;
        this.handlingInfo = handlingInfo;
        this.deliveryDate = deliveryDate;
        this.customDropOffPlace = customDropOffPlace;
    }


    public String getOrderID()
    {
        return orderID;
    }

    public void setOrderID(String orderID)
    {
        this.orderID = orderID;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getEmployeeName()
    {
        return employeeName;
    }

    public void setEmployeeName(String employeeName)
    {
        this.employeeName = employeeName;
    }

    public Recipient getRecipient()
    {
        return recipient;
    }
    public void setRecipient(Recipient recipient)
    {
        this.recipient = recipient;
    }

    public String getPackageSize()
    {
        return packageSize;
    }

    public void setPackageSize(String packageSize)
    {
        this.packageSize = packageSize;
    }

    public String getHandlingInfo()
    {
        return handlingInfo;
    }

    public void setHandlingInfo(String handlingInfo)
    {
        this.handlingInfo = handlingInfo;
    }

    public String getDeliveryDate()
    {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate)
    {
        this.deliveryDate = deliveryDate;
    }

    public String getCustomDropOffPlace()
    {
        return customDropOffPlace;
    }

    public void setCustomDropOffPlace(String customDropOffPlace)
    {
        this.customDropOffPlace = customDropOffPlace;
    }

    public String getStreet()
    {
        return recipient.getAddress().getStreet();
    }
    public void setRecipientOverOrder(String firstName, String lastName, String street, String houseNumber, String zip)
    {
        this.recipient  = new Recipient(firstName, lastName, new Address(street, houseNumber, zip));
    }

    @Override
    public String toString()
    {
        return "Order{"
                + "token='" + orderID + '\''
                + ", timestamp='" + timestamp + '\''
                + ", employeeName='" + employeeName + '\''
                + ", packageSize='" + packageSize + '\''
                + ", handlingInfo='" + handlingInfo + '\''
                + ", deliveryDate='" + deliveryDate + '\''
                + ", customDropOffPlace='" + customDropOffPlace + '\''
                + '}';
    }
}

