package com.chatapp.restapi.repository;

import com.chatapp.restapi.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query(value = """
        SELECT * FROM messages m
        ORDER BY m.created_at DESC
        """, nativeQuery = true)
    List<Message> findAllOrderedByNewest();
}
