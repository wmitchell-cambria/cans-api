package gov.ca.cwds.cans.domain.dto;

import static gov.ca.cwds.rest.api.domain.DomainObject.DATE_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.enumeration.Gender;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import gov.ca.cwds.cans.domain.enumeration.Race;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PersonDto extends Dto {
  @NotNull private PersonRole personRole;

  @NotEmpty
  @Size(min = 1, max = 50)
  private String firstName;

  @NotEmpty
  @Size(min = 1, max = 50)
  private String lastName;

  private String externalId;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private LocalDate dob;

  private Boolean estimatedDob;
  private Gender gender;
  private Race race;

  @NotEmpty
  @Size(min = 1, max = 50)
  private String caseId;

  private String countyClientNumber;
  private String clientIndexNumber;
  @NotNull private CountyDto county;
}
