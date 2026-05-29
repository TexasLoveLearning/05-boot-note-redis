package com.fyc._4bootnote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fyc._4bootnote.entity.Note;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoteMapper extends BaseMapper<Note> {
}
