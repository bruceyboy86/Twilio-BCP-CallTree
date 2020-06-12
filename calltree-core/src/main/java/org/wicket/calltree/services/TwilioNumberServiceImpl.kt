package org.wicket.calltree.services

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.wicket.calltree.dto.TwilioNumberDto
import org.wicket.calltree.mappers.TwilioNumberMapper
import org.wicket.calltree.models.TwilioNumber
import org.wicket.calltree.repository.TwilioNumberRepository
import java.util.stream.Collectors
import javax.validation.constraints.NotNull

@Service
class TwilioNumberServiceImpl(private val numberRepository: TwilioNumberRepository,
                              private val twilioNumberMapper: TwilioNumberMapper) : TwilioNumberService {

    override fun getAllNumbers(page: Int, size: Int): Page<TwilioNumber> {
        return numberRepository.findAllByActiveIsTrue(PageRequest.of(page, size))
    }

    override fun saveNumber(newNumberDto: TwilioNumberDto): TwilioNumberDto {
        val existingNumber = numberRepository.findFirstByTwilioNumber(newNumberDto.twilioNumber)
        return if (existingNumber.isPresent)
            updateExistingNumber(existingNumber.get())
        else createNewNumber(newNumberDto)
    }

    override fun deleteNumber(id: Long) {
        val number = numberRepository.findById(id)
        number.ifPresent {
            it.active = false;
            numberRepository.save(it)
        }
    }

    override fun getAvailableNumbers(): List<TwilioNumberDto> {
        val numbersAvailable = numberRepository.findAllByIsAvailableIsTrueAndActiveIsTrue()
        return numbersAvailable.stream()
                .map { twilioNumberMapper.entityToDto(it) }
                .collect(Collectors.toList())
    }

    override fun getNumberById(id: Long): TwilioNumberDto {
        val number = numberRepository.findByIdAndActiveIsTrue(id)
        number.get().let { return twilioNumberMapper.entityToDto(it) }
    }

    override fun getManyNumbers(active: Boolean, @NotNull vararg id: Long): List<TwilioNumberDto> {
        val allAvailable = numberRepository.findAllByActiveIsTrue()
        return allAvailable
                .filter { id.contains(it.id!!) }
                .map { twilioNumberMapper.entityToDto(it) }
                .toList()
    }

    private fun createNewNumber(newNumberDto: TwilioNumberDto): TwilioNumberDto {
        val number = twilioNumberMapper.dtoToEntity(newNumberDto)
        number.active = true
        val persisted = numberRepository.save(number)
        return twilioNumberMapper.entityToDto(persisted)
    }

    private fun updateExistingNumber(number: TwilioNumber): TwilioNumberDto {
        number.active = true
        val persisted = numberRepository.save(number)
        return twilioNumberMapper.entityToDto(persisted)
    }
}