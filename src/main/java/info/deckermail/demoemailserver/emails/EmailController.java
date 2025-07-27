package info.deckermail.demoemailserver.emails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping("/emails")
@RequiredArgsConstructor
@Log4j2
class EmailController {

    private final EmailService emailService;

    @ExceptionHandler(NoSuchEmailException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    void handleNotFoundException(NoSuchEmailException e) {
        log.warn("Email with ID {} not found.", e.getEmailId());
        log.debug("Exception details: ", e);
    }

    @ExceptionHandler(EmailUpdateFailedException.class)
    @ResponseStatus(code = HttpStatus.PRECONDITION_FAILED)
    void handleUpdateFailedException(EmailUpdateFailedException e) {
        log.warn("Email update failed for ID {}.", e.getEmailId());
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

    @PostMapping
    ResponseEntity<EmailDto> create(@Valid @RequestBody EmailCreationRequest request) {
        final EmailDto createdEmail = emailService.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdEmail.id())
                .toUri();

        return ResponseEntity.created(location).body(createdEmail);
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    Collection<EmailDto> create(@Valid @RequestBody EmailBulkCreationRequest request) {
        return emailService.createBulk(request);
    }

    @PutMapping("/{id}")
    EmailDto update(@PathVariable Long id, @Valid @RequestBody EmailUpdateRequest request) throws NoSuchEmailException, EmailUpdateFailedException {
        return emailService.update(id, request);
    }
}
