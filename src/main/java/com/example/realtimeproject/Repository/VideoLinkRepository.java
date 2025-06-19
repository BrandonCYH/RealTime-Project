package com.example.realtimeproject.Repository;

import com.example.realtimeproject.Model.VideoLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoLinkRepository extends JpaRepository<VideoLink, Long> {
    List<VideoLink> findByProcessedFalse();
}
