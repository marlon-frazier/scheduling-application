package frazier.c195_performance_assessment;

/**
 * Represents a customer in the system.
 */
public class Customer {

    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private int divisionID;
    private String division;
    private String country;


    /**
     * Constructs a Customer object with the specified attributes.
     *
     * @param id          The customer ID.
     * @param name        The customer name.
     * @param address     The customer address.
     * @param postalCode  The customer postal code.
     * @param phone       The customer phone number.
     * @param divisionID  The division ID associated with the customer.
     * @param division    The division associated with the customer.
     * @param country     The country associated with the customer.
     */
    public Customer(int id, String name, String address, String postalCode, String phone, int divisionID, String division, String country) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionID = divisionID;
        this.division = division;
        this.country = country;
    }

    /**
     * Retrieves the customer ID.
     *
     * @return The customer ID.
     */
    public int getId() {
        return id;
    }


    /**
     * Sets the customer ID.
     *
     * @param id The customer ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retrieves the customer name.
     *
     * @return The customer name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the customer name.
     *
     * @param name The customer name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the customer address.
     *
     * @return The customer address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the customer address.
     *
     * @param address The customer address to set.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Retrieves the customer postal code.
     *
     * @return The customer postal code.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the customer postal code.
     *
     * @param postalCode The customer postal code to set.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Retrieves the customer phone number.
     *
     * @return The customer phone number.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the customer phone number.
     *
     * @param phone The customer phone number to set.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Retrieves the division ID associated with the customer.
     *
     * @return The division ID.
     */
    public int getDivisionID() {
        return divisionID;
    }

    /**
     * Sets the division ID associated with the customer.
     *
     * @param divisionID The division ID to set.
     */
    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    /**
     * Retrieves the division associated with the customer.
     *
     * @return The division.
     */
    public String getDivision() {
        return division;
    }

    /**
     * Sets the division associated with the customer.
     *
     * @param division The division to set.
     */
    public void setDivision(String division) {
        this.division = division;
    }

    /**
     * Retrieves the country associated with the customer.
     *
     * @return The country.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country associated with the customer.
     *
     * @param country The country to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }
}
