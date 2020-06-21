package learn.concurrent.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class SimpleDataPo {
    private Long id;

    private String name;

    private String description;

    private String text;

    private Date createTime = new Date();

    private Date updateTime;

    public SimpleDataPo(Long id, String name, String description, String text) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.text = text;
    }

    public void setId(Long id) {
        this.updateTime = new Date();
        this.id = id;
    }

    public void setName(String name) {
        this.updateTime = new Date();
        this.name = name;
    }

    public void setDescription(String description) {
        this.updateTime = new Date();
        this.description = description;
    }

    public void setText(String text) {
        this.updateTime = new Date();
        this.text = text;
    }
}
