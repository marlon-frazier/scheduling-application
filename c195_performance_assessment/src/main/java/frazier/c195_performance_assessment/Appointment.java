package frazier.c195_performance_assessment;

import java.sql.Timestamp;

/**
 * Represents an appointment in the appointment scheduling system.
 */
public class Appointment {

    private String title;
    private String description;
    private String location;
    private String type;
    private Timestamp startDate;
    private Timestamp endDate;
    private Timestamp createDate;
    private String createdBy;
    private Timestamp lastUpdateDate;
    private String lastUpdatedBy;
    private int customerID;
    private int userID;
    private int contactID;
    private String contactName;
    private int id;

    /**
     * Constructs an instance of the Appointment class with the specified attributes.
     *
     * @param id The ID of the appointment.
     * @param title The title of the appointment.
     * @param description The description of the appointment.
     * @param location The location of the appointment.
     * @param type The type of the appointment.
     * @param startDate The start date and time of the appointment.
     * @param endDate The end date and time of the appointment.
     * @param createDate The date and time when the appointment was created.
     * @param createdBy The user who created the appointment.
     * @param lastUpdateDate The date and time when the appointment was last updated.
     * @param lastUpdatedBy The user who last updated the appointment.
     * @param customerID The ID of the customer associated with the appointment.
     * @param userID The ID of the user associated with the appointment.
     * @param contactID The ID of the contact associated with the appointment.
     * @param contactName The name of the contact associated with the appointment.
     */
    public Appointment(int id,String title, String description, String location, String type, Timestamp startDate, Timestamp endDate, Timestamp createDate, String createdBy, Timestamp lastUpdateDate, String lastUpdatedBy, int customerID, int userID, int contactID, String contactName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdateDate = lastUpdateDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
        this.contactName = contactName;
    }

    /**
     * Retrieves the title of the appointment.
     *
     * @return The title of the appointment.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the appointment.
     *
     * @param title The title to be set for the appointment.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Retrieves the description of the appointment.
     *
     * @return The description of the appointment.
     */
    public String getDescription() {
        return description;
    }

    /** Sets the description of the appointment.
     * @param description the description for the appointment. */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Retrieves the location of the appointment.
     * @return returns location of the appointment.*/
    public String getLocation() {
        return location;
    }

    /** Sets the location of the appointment.
     * @param location the location of the appointment.*/
    public void setLocation(String location) {
        this.location = location;
    }

    /** Retrieves the type of appointment.
     * @return the type of appointment.*/
    public String getType() {
        return type;
    }

    /** Sets the type of appointment.
     * @param type the type of appointment.*/
    public void setType(String type) {
        this.type = type;
    }

    /** Retrieves the start date of the apppointment.
     * @return the appointment start date.*/
    public Timestamp getStartDate() {
        return startDate;
    }

    /** Sets the start date of the appointment.
     * @param startDate the start date of the appointment.*/
    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    /** Retrieves the end date of the appointment.
     * @return the end date /time of the appointment.*/
    public Timestamp getEndDate() {
        return endDate;
    }

    /** Sets the end date of the appointment.
     * @param endDate the end date of the appointment.*/
    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    /** Retrieves the creation date of the appointment.
     * @return the creation date.*/
    public Timestamp getCreateDate() {
        return createDate;
    }

    /** Sets the creation date of the appointment.
     * @param createDate the creation date.*/
    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    /** Retrieves who the appointment was created by.
     * @return the user who created the appointment*/
    public String getCreatedBy() {
        return createdBy;
    }

    /** Sets the user who created the appointment.
     * @param createdBy the user who created the appointment.*/
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /** Retrieves the last update date.
     * @return the date of the last update.*/
    public Timestamp getLastUpdateDate() {
        return lastUpdateDate;
    }

    /** Sets the last update date.
     * @param lastUpdateDate the last update date.*/
    public void setLastUpdateDate(Timestamp lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    /** Retrieves the user who last updated appointment.
     * @return the user who last did the update.*/
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /** Sets the user who last updated appointment.
     * @param lastUpdatedBy the user who last updated appointment.*/
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /** Retrieves the customer ID.
     * @return the customer ID.*/
    public int getCustomerID() {
        return customerID;
    }

    /** Sets the customer ID.
     * @param customerID the customer ID.*/
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /** Retrieves the user ID.
     * @return the user ID.*/
    public int getUserID() {
        return userID;
    }

    /** Sets the user ID.
     * @param userID the user ID.*/
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /** Retrieves the contact ID.
     * @return the contact ID>*/
    public int getContactID() {
        return contactID;
    }

    /** Sets the contact ID.
     * @param contactID the contact ID.*/
    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    /** Retrieves the contact name.
     * @return the contact name.*/
    public String getContactName() {
        return contactName;
    }

    /** Sets the contact name.
     * @param contactName the contact name.*/
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /** Retrieves appointment ID.
     * @return the appointment ID.*/
    public int getId() {
        return id;
    }

    /** Sets the appointment ID.
     * @param id the appointment ID.*/
    public void setId(int id) {
        this.id = id;
    }
}
