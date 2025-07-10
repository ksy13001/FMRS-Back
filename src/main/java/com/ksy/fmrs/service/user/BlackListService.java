package com.ksy.fmrs.service.user;

import com.ksy.fmrs.repository.user.BlackListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BlackListService {

    private final BlackListRepository blackListRepository;

    public void rotateBlackList() {

    }
}
