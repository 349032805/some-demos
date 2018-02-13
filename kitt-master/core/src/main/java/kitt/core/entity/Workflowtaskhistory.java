package kitt.core.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Workflowtaskhistory implements Serializable {
    private Integer id;

    private Integer taskid;

    private Integer workflowid;

    private String workflowname;

    private Integer currentnodeid;

    private String currentnodename;

    private Integer tasksequence;

    private String comment;

    private String url;

    private String taskstatus;

    private String createdBy;

    private LocalDateTime createdDate;

    private String lastModifiedBy;

    private LocalDateTime lastModifiedDate;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskid() {
        return taskid;
    }

    public void setTaskid(Integer taskid) {
        this.taskid = taskid;
    }

    public Integer getWorkflowid() {
        return workflowid;
    }

    public void setWorkflowid(Integer workflowid) {
        this.workflowid = workflowid;
    }

    public Integer getCurrentnodeid() {
        return currentnodeid;
    }

    public void setCurrentnodeid(Integer currentnodeid) {
        this.currentnodeid = currentnodeid;
    }

    public Integer getTasksequence() {
        return tasksequence;
    }

    public void setTasksequence(Integer tasksequence) {
        this.tasksequence = tasksequence;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getTaskstatus() {
        return taskstatus;
    }

    public void setTaskstatus(String taskstatus) {
        this.taskstatus = taskstatus == null ? null : taskstatus.trim();
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy == null ? null : createdBy.trim();
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy == null ? null : lastModifiedBy.trim();
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public String getWorkflowname() {
        return workflowname;
    }

    public void setWorkflowname(String workflowname) {
        this.workflowname = workflowname;
    }

    public String getCurrentnodename() {
        return currentnodename;
    }

    public void setCurrentnodename(String currentnodename) {
        this.currentnodename = currentnodename;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return "Workflowtaskhistory{" +
                "id=" + id +
                ", taskid=" + taskid +
                ", workflowid=" + workflowid +
                ", workflowname='" + workflowname + '\'' +
                ", currentnodeid=" + currentnodeid +
                ", currentnodename='" + currentnodename + '\'' +
                ", tasksequence=" + tasksequence +
                ", comment='" + comment + '\'' +
                ", url='" + url + '\'' +
                ", taskstatus='" + taskstatus + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdDate=" + createdDate +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}