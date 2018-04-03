package builderspace.notekeeper.model;

import java.util.Date;

public class Note {
    private String id;
    private String taskNote;
    private String status;
    private int priority;
    private Date createdTime;

    public Note(String taskNote, String status, int priority, Date createdTime) {
        this.taskNote = taskNote;
        this.status = status;
        this.priority = priority;
        this.createdTime = createdTime;
    }

    public Note() {

    }

    @Override
    public String toString() {
        return "Note{" +
                "createdTime=" + createdTime +
                ", id=" + id +
                ", taskNote='" + taskNote + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTaskNote() {
        return taskNote;
    }

    public void setTaskNote(String taskNote) {
        this.taskNote = taskNote;
    }

}
