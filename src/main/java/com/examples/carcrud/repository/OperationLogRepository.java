package com.examples.carcrud.repository;

import com.examples.carcrud.model.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
}
