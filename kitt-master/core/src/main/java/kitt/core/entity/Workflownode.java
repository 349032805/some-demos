package kitt.core.entity;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Workflownode implements Serializable {
    private Integer id;

    private Integer roleid;

    private String rolename;

    private Integer groupid;

    private String groupname;

    private Integer workflowid;

    private String workflowname;

    private String name;

    private String comment;

    private String url;

    private String status;

    private String arrowstatus;

    private String createdBy;

    private LocalDateTime createdDate;

    private String lastModifiedBy;

    public boolean isChecked;

    private LocalDateTime lastModifiedDate;

    private List<Workflowarrows> workflowarrowsNextList;

    private List<Workflowarrows> workflowarrowsBackList;


    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoleid() {
        return roleid;
    }

    public void setRoleid(Integer roleid) {
        this.roleid = roleid;
    }

    public Integer getGroupid() {
        return groupid;
    }

    public void setGroupid(Integer groupid) {
        this.groupid = groupid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
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

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Integer getWorkflowid() {
        return workflowid;
    }

    public void setWorkflowid(Integer workflowid) {
        this.workflowid = workflowid;
    }

    public String getWorkflowname() {
        return workflowname;
    }

    public void setWorkflowname(String workflowname) {
        this.workflowname = workflowname;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getArrowstatus() {
        return arrowstatus;
    }

    public void setArrowstatus(String arrowstatus) {
        this.arrowstatus = arrowstatus;
    }

    public List<Workflowarrows> getWorkflowarrowsNextList() {
        return workflowarrowsNextList;
    }

    public void setWorkflowarrowsNextList(List<Workflowarrows> workflowarrowsNextList) {
        this.workflowarrowsNextList = workflowarrowsNextList;
    }

    public List<Workflowarrows> getWorkflowarrowsBackList() {
        return workflowarrowsBackList;
    }

    public void setWorkflowarrowsBackList(List<Workflowarrows> workflowarrowsBackList) {
        this.workflowarrowsBackList = workflowarrowsBackList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", workflowname=").append(workflowname);
        sb.append(", workflowid=").append(workflowid);
        sb.append(", roleid=").append(roleid);
        sb.append(", groupid=").append(groupid);
        sb.append(", name=").append(name);
        sb.append(", comment=").append(comment);
        sb.append(", url=").append(url);
        sb.append(", status=").append(status);
        sb.append(", createdBy=").append(createdBy);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", lastModifiedBy=").append(lastModifiedBy);
        sb.append(", lastModifiedDate=").append(lastModifiedDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}