package sellapp.models;

import java.io.Serializable;

public class Address implements Serializable
{

    private String street;
    private String houseNumber;
    private String zip;

    private final String city = "Lingen";

    public Address(String street, String houseNumber, String zip)
    {
        this.street = street;
        this.houseNumber = houseNumber;
        this.zip = zip;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {

        this.street = street;
    }

    public String getHouseNumber()
    {

        return houseNumber;
    }

    public void setHouseNumber(String houseNumber)
    {

        this.houseNumber = houseNumber;
    }

    public String getZip()
    {

        return zip;
    }

    public void setZip(String zip)
    {

        this.zip = zip;
    }

    public String getCity()
    {
        return city;
    }
}
