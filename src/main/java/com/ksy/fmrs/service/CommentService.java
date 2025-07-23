package com.ksy.fmrs.service;

import com.ksy.fmrs.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public void save(){
//        return commentRepository.save(new Comment()).getId();
    }
}
