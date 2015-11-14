package team6.iguide;


public class DetailItemRecycler {
    //public String key;
    //public String value;
    public String title;
    public String description;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DetailItemRecycler(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
