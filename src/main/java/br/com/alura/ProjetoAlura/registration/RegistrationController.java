package br.com.alura.ProjetoAlura.registration;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/registration/new")
    public ResponseEntity<Registration> createRegistration(@Valid @RequestBody NewRegistrationDTO newRegistration) {
        Registration registration = registrationService.createRegistration(newRegistration);
        return ResponseEntity.status(HttpStatus.CREATED).body(registration);
    }

    @GetMapping("/registration/report")
    public ResponseEntity<List<RegistrationReportItem>> report() {
        List<RegistrationReportItem> items = registrationService.generateReport();
        return ResponseEntity.ok(items);
    }
}
