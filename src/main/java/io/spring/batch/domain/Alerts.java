package io.spring.batch.domain;

public class Alerts {

    private long id;

    private String title;

    private String description;

    public Alerts() {

        super();
    }

    public Alerts(long id, String title, String description) {

        super();
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {

        this.id = id;
    }

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

    @Override
    public String toString() {

        return "ScheduledAlerts [id=" + id + ", title=" + title + ", description=" + description + "]";
    }
}
