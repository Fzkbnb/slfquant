package com.slf.quant.facade.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.data.annotation.Id;
import java.io.Serializable;

/**
 * BaseEntity：
 *
 * @author: fzk
 * @date: 2020-08-27 10:46
 */
@Data
public  abstract class BaseEntity implements Serializable {

    /**
     * 主键编号
     */
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    protected Long    id;

    /**
     * 数据版本号
     */
    protected Long    version = Long.valueOf(1);

    /**
     * 删除标识
     */
    @JsonIgnore
    protected Boolean delFlag = false;
}
