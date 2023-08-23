package news.news_management.topic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="topics")
public class Topic {

    @Id
    @SequenceGenerator(
            name = "topic_sequence",
            sequenceName = "topic_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "topic_sequence"
    )
    @Column(name="topic_id")
    private Long topicId;

    @Column(name = "name",unique = true)
    @NotBlank(message = "Name should not be null")
    private String name;

    @Column(name = "fathers_name")
    private String fathersName;

    


}