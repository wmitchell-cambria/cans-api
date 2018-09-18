package gov.ca.cwds.cans.dao;

import static gov.ca.cwds.cans.domain.entity.Person.PARAM_USERS_COUNTY_EXTERNAL_ID;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import gov.ca.cwds.cans.Constants.Privileges;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import gov.ca.cwds.cans.domain.search.SearchPersonParameters;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import gov.ca.cwds.cans.util.Require;
import gov.ca.cwds.security.annotations.Authorize;
import gov.ca.cwds.security.utils.PrincipalUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author denys.davydov
 */
public class PersonDao extends AbstractCrudDao<Person> {

  @Inject
  public PersonDao(@CansSessionFactory final SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  @Authorize("person:read:result")
  public Person find(Serializable primaryKey) {
    return super.find(primaryKey);
  }

  @Override
  public Person create(@Authorize("person:create:person") Person person) {
    return super.create(person);
  }

  public Collection<Person> search(SearchPersonParameters searchPersonParameters) {
    Require.requireNotNullAndNotEmpty(searchPersonParameters);
    final Session session = grabSession();
    authorize();

    final PersonRole personRole = searchPersonParameters.getPersonRole();
    if (personRole != null) {
      session.enableFilter(Person.FILTER_PERSON_ROLE)
          .setParameter(Person.PARAM_PERSON_ROLE, personRole.name());
    }

    final String externalId = searchPersonParameters.getExternalId();
    if (StringUtils.isNotBlank(externalId)) {
      session.enableFilter(Person.FILTER_EXTERNAL_ID)
          .setParameter(Person.PARAM_EXTERNAL_ID, externalId);
    }

    final String usersCountyExternalId = searchPersonParameters.getUsersCountyExternalId();
    if (StringUtils.isNotBlank(usersCountyExternalId)) {
      session.enableFilter(Person.FILTER_COUNTY)
          .setParameter(PARAM_USERS_COUNTY_EXTERNAL_ID, usersCountyExternalId);
    }

    // TODO: DELETE MAX_RECORDS LINE
    final List<Person> results = session.createNamedQuery(Person.NQ_ALL, Person.class).setMaxResults(50).list();
    return ImmutableList.copyOf(results);
  }

  private void authorize() {
    if (!PrincipalUtils.getPrincipal().getPrivileges().contains(Privileges.SEALED)) {
      grabSession().enableFilter(Person.AUTHORIZATION_FILTER);
    }
  }

}
