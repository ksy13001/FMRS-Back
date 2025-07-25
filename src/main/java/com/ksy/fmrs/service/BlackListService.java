package com.ksy.fmrs.service;

import com.ksy.fmrs.repository.BlackListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BlackListService {

    private final BlackListRepository blackListRepository;

    public void rotateBlackList() {

    }
}
