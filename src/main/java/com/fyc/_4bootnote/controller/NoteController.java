package com.fyc._4bootnote.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyc._4bootnote.common.Result;
import com.fyc._4bootnote.entity.Note;
import com.fyc._4bootnote.mapper.NoteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 新增
    @PostMapping
    public Result<String> addNote(@RequestBody Note note) {
        noteMapper.insert(note);
        return Result.success("添加成功");
    }

    // 查单个:先看redis再看MySQL
    //Cache-Aside 模式（旁路缓存）,如果redis没有，MySQL有，顺手存进redis
    @GetMapping("/{id}")
    public Result<Note> getNoteById(@PathVariable Long id) {
        String key = "note:" + id;

        //先查redis
        Note note = (Note) redisTemplate.opsForValue().get(key);
        if (note != null) {
            System.out.println("🔥 缓存命中！直接从 Redis 返回");
            return Result.success(note);//缓存命中，直接返回
        }
        System.out.println("💧 缓存未命中，查 MySQL...");
        //缓存没命中，查MySQL
        note = noteMapper.selectById(id);

        if (note != null) {
            //查到后存进redis，设置过期时间1小时
            redisTemplate.opsForValue().set(key,note,1, TimeUnit.HOURS);
        }
        return Result.success(note);
    }

    // 更新
    @PutMapping("/{id}")
    public Result<Note> updateNote(@PathVariable Long id, @RequestBody Note note) {
        note.setId(id);
        noteMapper.updateById(note);
        //删掉旧的缓存
        redisTemplate.delete("note:" + id);
        return Result.success(noteMapper.selectById(id));
    }

    // 删除（逻辑删除）
    @DeleteMapping("/{id}")
    public Result<String> deleteNote(@PathVariable Long id) {
        noteMapper.deleteById(id);
        //删掉
        redisTemplate.delete("note:" + id);
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
