package com.fyc._4bootnote.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyc._4bootnote.common.Result;
import com.fyc._4bootnote.entity.Note;
import com.fyc._4bootnote.mapper.NoteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteMapper noteMapper;

    // 新增
    @PostMapping
    public Result<String> addNote(@RequestBody Note note) {
        noteMapper.insert(note);
        return Result.success("添加成功");
    }

    // 查单个
    @GetMapping("/{id}")
    public Result<Note> getNoteById(@PathVariable Long id) {
        Note note = noteMapper.selectById(id);
        return Result.success(note);
    }

    // 更新
    @PutMapping("/{id}")
    public Result<Note> updateNote(@PathVariable Long id, @RequestBody Note note) {
        note.setId(id);
        noteMapper.updateById(note);
        return Result.success(noteMapper.selectById(id));
    }

    // 删除（逻辑删除）
    @DeleteMapping("/{id}")
    public Result<String> deleteNote(@PathVariable Long id) {
        noteMapper.deleteById(id);
        return Result.success("删除成功");
    }

    // 按用户查询
    @GetMapping("/user/{userId}")
    public Result<List<Note>> getNotesByUser(@PathVariable Long userId) {
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Note::getUserId, userId);
        List<Note> list = noteMapper.selectList(wrapper);
        return Result.success(list);
    }

    // 分页查询
    @GetMapping("/page")
    public Result<Page<Note>> getNotesByPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Note> pageObj = new Page<>(page, size);
        noteMapper.selectPage(pageObj, null);
        return Result.success(pageObj);
    }
}
