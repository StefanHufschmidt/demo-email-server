package info.deckermail.demoemailserver.emails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            description = "Get an email by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email found, see body for details"),
                    @ApiResponse(responseCode = "404", description = "Email with given id not found"),
            }
    )
    @GetMapping("/{id}")
    EmailResponse findById(@Parameter(in = ParameterIn.PATH, description = "ID of the related email to retrieve") @PathVariable long id) throws NoSuchEmailException {
        return emailService.findById(id);
    }

    @Operation(
            description = "Get multiple emails with optional pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email found, see body for details"),
            }
    )
    @GetMapping
    Page<EmailResponse> findAll(Pageable pagable) {
        return emailService.findAll(pagable);
    }

    @Operation(
            description = "Creat an email entry",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Email successfully created, see body for details"),
                    @ApiResponse(responseCode = "400", description = "Invalid request, see body for details"),
            }
    )
    @PostMapping
    ResponseEntity<EmailResponse> create(@Valid @RequestBody EmailCreationRequest request) {
        final EmailResponse createdEmail = emailService.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdEmail.id())
                .toUri();

        return ResponseEntity.created(location).body(createdEmail);
    }

    @Operation(
            description = "Creat multiple email entries",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Emails successfully created, see body for details"),
                    @ApiResponse(responseCode = "400", description = "Invalid request, see body for details"),
            }
    )
    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    Collection<EmailResponse> create(@Valid @RequestBody EmailBulkCreationRequest request) {
        return emailService.createBulk(request);
    }

    @Operation(
            description = "Update an email entry. Related email must be in DRAFT state to be updated.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email successfully created, see body for details"),
                    @ApiResponse(responseCode = "400", description = "Invalid request, see body for details"),
                    @ApiResponse(responseCode = "404", description = "No email with given id found"),
                    @ApiResponse(responseCode = "412", description = "Email update failed, email is not in DRAFT state"),
            }
    )
    @PutMapping("/{id}")
    void update(
            @Parameter(in = ParameterIn.PATH, description = "ID of the related email to update") @PathVariable long id,
            @Valid @RequestBody EmailUpdateRequest request
    ) throws NoSuchEmailException, EmailUpdateFailedException {
        emailService.update(id, request);
    }

    @Operation(
            description = "Update an email entry. Related email must be in DRAFT state to be updated.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email successfully created, see body for details"),
                    @ApiResponse(responseCode = "400", description = "Invalid request, see body for details"),
                    @ApiResponse(responseCode = "404", description = "No email with given id found"),
                    @ApiResponse(responseCode = "412", description = "Email update failed, email is not in DRAFT state"),
            }
    )
    @PostMapping("/{id}")
    EmailResponse updateAndReturn(
            @Parameter(in = ParameterIn.PATH, description = "ID of the related email to update") @PathVariable Long id,
            @Valid @RequestBody EmailUpdateRequest request
    ) throws NoSuchEmailException, EmailUpdateFailedException {
        return emailService.update(id, request);
    }

    @Operation(
            description = "Delete an email entry.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Email with given ID does not exist anymore"),
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@Parameter(in = ParameterIn.PATH, description = "ID of the related email to delete") @PathVariable long id) {
        emailService.delete(id);
    }

    @Operation(
            description = "Delete multiple email entries.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Emails with given IDs do not exist anymore"),
            }
    )
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@RequestBody @Valid EmailBulkDeletionRequest request) {
        emailService.delete(request);
    }
}
