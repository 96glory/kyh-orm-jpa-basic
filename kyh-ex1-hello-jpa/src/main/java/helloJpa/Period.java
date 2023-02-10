package helloJpa;

import java.time.LocalDateTime;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PRIVATE)
@Embeddable
public class Period {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
