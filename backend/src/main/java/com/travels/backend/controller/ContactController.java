package com.travels.backend.controller;

import com.travels.backend.dto.ContactDTO;
import com.travels.backend.dto.ContactRequestDTO;
import com.travels.backend.service.ContactService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contacto", description = "Formulario público y gestión de mensajes (ADMIN)")
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<ContactDTO> createContact(@Valid @RequestBody ContactRequestDTO dto) {
        log.info("Creando nuevo mensaje de contacto de: {}", dto.getEmail());
        var contact = contactService.createContact(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contactService.convertToDTO(contact));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContactDTO> getContactById(@PathVariable Long id) {
        log.info("Obteniendo mensaje de contacto con ID: {}", id);
        var contact = contactService.getContactById(id);
        return ResponseEntity.ok(contactService.convertToDTO(contact));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContactDTO>> getAllContacts() {
        log.info("Obteniendo todos los mensajes de contacto");
        var contacts = contactService.getAllContacts();
        return ResponseEntity.ok(contactService.convertToDTO(contacts));
    }

    @GetMapping("/unresolved")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContactDTO>> getUnresolvedContacts() {
        log.info("Obteniendo mensajes de contacto no resueltos");
        var contacts = contactService.getUnresolvedContacts();
        return ResponseEntity.ok(contactService.convertToDTO(contacts));
    }

    @PatchMapping("/{id}/respond")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContactDTO> respondContact(
            @PathVariable Long id,
            @RequestBody String response) {
        log.info("Respondiendo mensaje de contacto con ID: {}", id);
        var contact = contactService.respondContact(id, response);
        return ResponseEntity.ok(contactService.convertToDTO(contact));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        log.info("Eliminando mensaje de contacto con ID: {}", id);
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}
