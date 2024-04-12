package com.nexola.atividade3.services;

import com.nexola.atividade3.entities.Person;
import com.nexola.atividade3.entities.PersonDTO;
import com.nexola.atividade3.exceptions.DBException;
import com.nexola.atividade3.exceptions.ResourceNotFoundException;
import com.nexola.atividade3.repositories.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository repository;

    @Transactional(readOnly = true)
    public PersonDTO findById(Long id) {
        Person person = repository.findById(id).get();
        return new PersonDTO(person);
    }

    @Transactional
    public PersonDTO insert(PersonDTO dto) {
        Person entity = new Person();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity = repository.save(entity);
        return new PersonDTO(entity);
    }

    @Transactional
    public PersonDTO update(Long id, PersonDTO dto) {
        try {
            Person entity = repository.getReferenceById(id);
            entity.setName(dto.getName());
            entity.setEmail(dto.getEmail());
            entity.setPhone(dto.getPhone());
            entity = repository.save(entity);
            return new PersonDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id não encontrado: " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Id não encontrado: " + id);
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DBException("Violação de integridade referencial");
        }
    }
}
