package com.example.fastsoccer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "review")
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "content")
    private String content;
    @Column(name = "rate")
    private int rate;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "ownpitch_id") // thông qua khóa ngoại
    private OwnPitch ownPitch;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
