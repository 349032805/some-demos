package kitt.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;


/**
 * Created by xiangyang on 15/8/19.
 */

public abstract class BaseEntity {


    @JsonIgnore
    protected  String createdBy;

    @JsonIgnore
    protected LocalDateTime createdDate;

    @JsonIgnore
    protected String lastModifiedBy;

    @JsonIgnore
    protected LocalDateTime lastModifiedDate;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
