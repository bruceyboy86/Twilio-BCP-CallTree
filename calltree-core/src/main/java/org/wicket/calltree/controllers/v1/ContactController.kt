package org.wicket.calltree.controllers.v1

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.wicket.calltree.dto.ContactDto
import org.wicket.calltree.enums.Role
import org.wicket.calltree.services.ContactService
import javax.validation.Valid

/**
 * @author Alessandro Arosio - 10/04/2020 10:16
 */
@RestController
@RequestMapping("/api/v1/contacts")
class ContactController(private val contactService: ContactService) {

  @GetMapping("/all")
  fun fetchAllContacts() : List<ContactDto> {
    return contactService.allContacts
  }

  @GetMapping("/{id}")
  fun fetchContact(@PathVariable @Valid id: Long) : ContactDto {
    return contactService.getContact(id)
  }

  @GetMapping("/role/{role}")
  fun fetchContactsOfOneRole(@PathVariable role: String) : List<ContactDto> {
    return contactService.getAllSelectedRole(Role.valueOf(role.toUpperCase()))
  }

  @GetMapping("/tree/{role}")
  fun fetchTreeUntilRole(@PathVariable role: String) : List<ContactDto> {
    return contactService.getCalltreeUntilRole(Role.valueOf(role.toUpperCase()))
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  fun removeContact(@RequestBody @Valid contactDto: ContactDto) {
    contactService.deleteContact(contactDto.id)
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  fun saveContact(@RequestBody @Valid contactDto: ContactDto) : ContactDto {
    return contactService.saveOrUpdate(contactDto)
  }

  @PutMapping
  fun updateContact(@RequestBody @Valid contactDto: ContactDto) : ContactDto {
    return contactService.saveOrUpdate(contactDto)
  }
}