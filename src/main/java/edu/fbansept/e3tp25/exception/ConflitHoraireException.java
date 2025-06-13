package edu.fbansept.e3tp25.exception;

import edu.fbansept.e3tp25.dto.ConflitDto;
import lombok.Getter;
import java.util.List;

@Getter
public class ConflitHoraireException extends RuntimeException {
    private final List<ConflitDto> conflits;
    
    public ConflitHoraireException(String message, List<ConflitDto> conflits) {
        super(message);
        this.conflits = conflits;
    }
}
