package team6.iguide;

import com.google.gson.annotations.Expose;

public class OverpassTags {

    @Expose
    private String indoor;
    @Expose
    private String level;
    @Expose
    private String name;
    @Expose
    private String ref;
    @Expose
    private String room;

    /**
     * @return The indoor
     */
    public String getIndoor() {
        return indoor;
    }

    /**
     * @param indoor The indoor
     */
    public void setIndoor(String indoor) {
        this.indoor = indoor;
    }

    /**
     * @return The level
     */
    public String getLevel() {
        return level;
    }

    /**
     * @param level The level
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The ref
     */
    public String getRef() {
        return ref;
    }

    /**
     * @param ref The ref
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    /**
     * @return The room
     */
    public String getRoom() {
        return room;
    }

    /**
     * @param room The room
     */
    public void setRoom(String room) {
        this.room = room;
    }
}