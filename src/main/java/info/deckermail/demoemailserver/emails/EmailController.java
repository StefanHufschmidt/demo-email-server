package info.deckermail.demoemailserver.emails;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/emails")
@RequiredArgsConstructor
class EmailController {

    private final EmailService emailService;

    @GetMapping
    Page<EmailDto> findAll(Pageable pagable) {
        return emailService.findAll(pagable);
    }
}
