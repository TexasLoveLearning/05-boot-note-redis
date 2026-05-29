package com.fyc._4bootnote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fyc._4bootnote.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
