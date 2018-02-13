package kitt.core.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by lich on 15/9/11.
 */
public class Testwk implements Serializable {
    private int id;
    private String name;
    private int age;
    private int taskid;
    private  String created_by;
    private LocalDateTime created_date;
    private String last_modified_by;
    private LocalDateTime last_modified_date;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "testwk{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", taskid=" + taskid +
                '}';
    }

    public Testwk(int id, String name, int age, int taskid) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.taskid = taskid;
    }

    public Testwk() {
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public LocalDateTime getLast_modified_date() {
        return last_modified_date;
    }

    public void setLast_modified_date(LocalDateTime last_modified_date) {
        this.last_modified_date = last_modified_date;
    }

    public String getLast_modified_by() {
        return last_modified_by;
    }

    public void setLast_modified_by(String last_modified_by) {
        this.last_modified_by = last_modified_by;
    }

    public LocalDateTime getCreated_date() {
        return created_date;
    }

    public void setCreated_date(LocalDateTime created_date) {
        this.created_date = created_date;
    }
}
