package news.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TopicRequest {

    @NotBlank(message = "Name should not be null")
    private String name;

    private String fathersName;
}
