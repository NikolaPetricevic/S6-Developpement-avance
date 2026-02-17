package com.todolist.todolist.features.annonces.entity;

import com.todolist.todolist.features.categories.entity.Category;
import com.todolist.todolist.features.annonces.enums.StatusEnum;
import com.todolist.todolist.features.users.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Annonce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, length = 64)
    private String title;

    @Column(nullable = false, length = 256)
    private String description;

    @Column(nullable = false, length = 64)
    private String adress;

    @Column(length = 64)
    private String mail;

    private Timestamp date;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @Override
    public String toString() {
        return "Annonce{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", adress='" + adress + '\'' +
                ", mail='" + mail + '\'' +
                ", date=" + date +
                ", status=" + status +
                ", author_id=" + author.getId() +
                ", category_id=" + category.getId() +
                '}';
    }
}
