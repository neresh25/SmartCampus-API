package com.mycompany.smartcampus.models;

/**
 * A lightweight summary object returned by the GET /rooms list endpoint.
 *
 * Instead of returning the full Room object (which includes the sensorIds list),
 * this DTO returns only the fields a client needs to display a list or populate
 * a form. The 'href' field is a self-referencing HATEOAS link that tells the
 * client exactly where to fetch the full Room detail without constructing the
 * URL themselves.
 */
public class RoomSummary {

    private String id;
    private String name;
    private String href; // HATEOAS self-link to the full room detail endpoint

    public RoomSummary() {}

    public RoomSummary(String id, String name, String href) {
        this.id = id;
        this.name = name;
        this.href = href;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getHref() { return href; }
    public void setHref(String href) { this.href = href; }
}
