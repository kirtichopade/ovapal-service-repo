package com.ovapal.repository;

import com.ovapal.entity.PeriodRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PeriodRecordRepository extends JpaRepository<PeriodRecord, Long> {
    List<PeriodRecord> findByUserId(Long userId);
} 