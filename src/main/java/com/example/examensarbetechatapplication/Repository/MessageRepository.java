package com.example.examensarbetechatapplication.Repository;

import com.example.examensarbetechatapplication.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
