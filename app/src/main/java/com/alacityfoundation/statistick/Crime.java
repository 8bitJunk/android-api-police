package com.alacityfoundation.statistick;

/**
 * represents a crime object
 *
 * Created by ryan on 08/05/2015.
 */
public class Crime extends Model {
    private String category;
    private String location;
    private String outcome_status;
    private String persistent_id;
    private String id;
    private String month;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOutcome_status() {
        return outcome_status;
    }

    public void setOutcome_status(String outcome_status) {
        this.outcome_status = outcome_status;
    }

    public String getPersistent_id() {
        return persistent_id;
    }

    public void setPersistent_id(String persistent_id) {
        this.persistent_id = persistent_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}