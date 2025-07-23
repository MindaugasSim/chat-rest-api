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

    @Query(value = "SELECT * FROM messages WHERE user_id = :userId", nativeQuery = true)
    List<Message> findByUserId(Long userId);

    @Query(value = """
        SELECT u.username,
               COUNT(m.id) AS message_count,
               MIN(m.created_at) AS first_message_time,
               MAX(m.created_at) AS last_message_time,
               AVG(LENGTH(m.content)) AS avg_message_length,
               (SELECT m2.content FROM messages m2 WHERE m2.user_id = u.id ORDER BY m2.created_at DESC LIMIT 1) AS last_message_text
        FROM users u
        LEFT JOIN messages m ON m.user_id = u.id
        GROUP BY u.id, u.username
        """, nativeQuery = true)
    List<Object[]> getUserStatisticsNative();
}
