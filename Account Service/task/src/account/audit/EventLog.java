package account.audit;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class EventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    private LocalDate date;
    private String action;
    private String subject;
    private String object;
    private String path;

    public EventLog() {
        this.date = LocalDate.now();
    }

    public EventLog(String action, String subject, String object, String path) {
        this.date = LocalDate.now();
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}