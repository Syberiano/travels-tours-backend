package com.travels.backend.service;

import com.travels.backend.dto.ContactDTO;
import com.travels.backend.dto.ContactRequestDTO;
import com.travels.backend.exception.ResourceNotFoundException;
import com.travels.backend.model.Contact;
import com.travels.backend.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final ContactRepository contactRepository;

    public Contact createContact(ContactRequestDTO dto) {
        log.info("Creando mensaje de contacto de: {}", dto.getEmail());

        Contact contact = Contact.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .message(dto.getMessage())
                .phone(dto.getPhone())
                .resolved(false)
                .build();

        return contactRepository.save(contact);
    }

    public Contact getContactById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mensaje de contacto no encontrado con ID: " + id));
    }

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public List<Contact> getUnresolvedContacts() {
        return contactRepository.findByResolved(false);
    }

    public List<Contact> getResolvedContacts() {
        return contactRepository.findByResolved(true);
    }

    public Contact respondContact(Long id, String response) {
        log.info("Respondiendo mensaje de contacto con ID: {}", id);

        Contact contact = getContactById(id);
        contact.setResponse(response);
        contact.setResolved(true);

        return contactRepository.save(contact);
    }

    public void deleteContact(Long id) {
        log.info("Eliminando mensaje de contacto con ID: {}", id);
        contactRepository.deleteById(id);
    }

    public ContactDTO convertToDTO(Contact contact) {
        return ContactDTO.builder()
                .id(contact.getId())
                .name(contact.getName())
                .email(contact.getEmail())
                .message(contact.getMessage())
                .phone(contact.getPhone())
                .resolved(contact.getResolved())
                .response(contact.getResponse())
                .build();
    }

    public List<ContactDTO> convertToDTO(List<Contact> contacts) {
        return contacts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
