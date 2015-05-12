package com.alacityfoundation.statistick;

/**
 * Represents a police force object, contains a name and id.
 *
 * Created by ryan on 08/05/2015.
 */
public class Force extends Model {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
