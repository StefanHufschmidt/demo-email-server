package info.deckermail.demoemailserver.emails;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emails")
@RequiredArgsConstructor
@Log4j2
class EmailController {

    private final EmailService emailService;

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    void handleNotFoundException(NoSuchEmailException e) {
        log.warn("Email with ID {} not found.", e.getEmailId());
        log.debug("Exception details: ", e);
    }

    @GetMapping("/{id}")
    EmailDto findById(@PathVariable Long id) throws NoSuchEmailException {
        return emailService.findById(id);
    }

    @GetMapping
    Page<EmailDto> findAll(Pageable pagable) {
        return emailService.findAll(pagable);
    }
}
