package com.fyc._4bootnote.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("note")
public class Note implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "用户id不能为空")
    private Long userId;


    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;


    private String createTime;
    private String updateTime;

    @TableLogic
    private Integer deleted;
}
