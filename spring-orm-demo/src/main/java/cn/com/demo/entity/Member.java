package cn.com.demo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by Tom.
 */
@Entity
@Table(name="t_member")
@Data
public class Member implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id private Long id;
    private String name;
    private String addr;
    private Integer age;

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", addr='" + addr + '\'' +
                ", age=" + age +
                '}';
        
    }
}
