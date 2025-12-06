package code.model.entity.rating;

import code.model.entity.rooms.RoomEntity;
import code.model.entity.users.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewsEntity {
    @Id
    @Column(name = "review_id", nullable = false)
    private String reviewId;
    @Column(name = "stars", nullable = false)
    private double stars;
    @Column(name = "comment", nullable = false)
    private String comment;
    @Column(name = "reply", nullable = true)
    private String reply;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_rated_id")
    private UserEntity userRated;

    @ManyToOne
    @JoinColumn(name = "user_replied_id")
    private UserEntity userReplied;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private RoomEntity room;
}
