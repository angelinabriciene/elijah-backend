package com.example.elijah_backend.repository;

import com.example.elijah_backend.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {
}