package com.ksy.fmrs.scheduler;

import com.ksy.fmrs.repository.BlackListRepository;
import com.ksy.fmrs.util.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BlackListDeleteScheduler {

    private final BlackListRepository blackListRepository;
    private final TimeProvider timeProvider;

    @Transactional
    @Scheduled(cron = "${init.delete_blacklist_time}", zone = "Asia/Seoul")
    public void deleteExpiredBlackList() {
        blackListRepository.deleteAllByExpiryDateBefore(timeProvider.getCurrentInstant());
    }

}
