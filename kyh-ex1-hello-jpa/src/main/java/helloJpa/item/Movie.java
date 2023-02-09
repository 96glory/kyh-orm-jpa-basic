package helloJpa.item;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("MOVIE")
public class Movie extends Item {

    private String director;
    private String actor;

}
