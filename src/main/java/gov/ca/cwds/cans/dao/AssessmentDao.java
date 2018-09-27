package gov.ca.cwds.cans.dao;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.Instrument;
import gov.ca.cwds.cans.domain.search.SearchAssessmentParameters;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import gov.ca.cwds.cans.util.Require;
import gov.ca.cwds.security.annotations.Authorize;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.util.Collection;

import static gov.ca.cwds.cans.domain.entity.Assessment.FILTER_CREATED_BY_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.FILTER_PERSON_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.PARAM_CREATED_BY_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.PARAM_PERSON_ID;

/**
 * @author denys.davydov
 */
public class AssessmentDao extends AbstractCrudDao<Assessment> {

  @Inject
  public AssessmentDao(@CansSessionFactory final SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public void replaceCaseIds(final long personId, final long oldCaseId, final long newCaseId) {
    grabSession()
        .createQuery(
            "update Assessment set case_id = :newCaseId where person_id = :personId and case_id = :oldCaseId")
        .setParameter("personId", personId)
        .setParameter("oldCaseId", oldCaseId)
        .setParameter("newCaseId", newCaseId)
        .executeUpdate();
  }

  @Override
  public Assessment create(@Authorize({"person:write:assessment.person.id"}) Assessment assessment) {
    initializeRelationships(assessment);
    assessment.setCounty(assessment.getPerson().getCounty());
    return super.create(assessment);
  }

  @Override
  public Assessment update(@Authorize({"person:write:assessment.person.id"}) Assessment assessment) {
    revertCountyToInitialValue(assessment);
    initializeRelationships(assessment);
    return super.update(assessment);
  }

  @Override
  @Authorize({"person:read:result.person"})
  public Assessment find(Serializable primaryKey) {
    return super.find(primaryKey);
  }

  private void revertCountyToInitialValue(Assessment assessment) {
    final Assessment previousState = super.find(assessment.getId());
    assessment.setCounty(previousState.getCounty());
  }

  private void initializeRelationships(Assessment assessment) {
    hibernateInitializeIfNeeded(assessment.getCft());
    hibernateInitializeIfNeeded(assessment.getTheCase());
    hibernateInitializeIfNeeded(assessment.getCounty());
    hibernateInitializeIfNeeded(assessment.getPerson());
    hibernateInitializeIfNeeded(assessment.getInstrument());
    hibernateInitializeInstrument(assessment);
  }

  @Authorize({"person:read:assessment.person"})
  public Collection<Assessment> search(SearchAssessmentParameters searchAssessmentParameters) {
    Require.requireNotNullAndNotEmpty(searchAssessmentParameters);
    final Session session = grabSession();
    addFilterIfNeeded(session, FILTER_CREATED_BY_ID, PARAM_CREATED_BY_ID, searchAssessmentParameters.getCreatedById());
    addFilterIfNeeded(session, FILTER_PERSON_ID, PARAM_PERSON_ID, searchAssessmentParameters.getPersonId());
    // returns List (and not ImmutableList as usual) to filter results with authorizer)
    return session
        .createNamedQuery(Assessment.NQ_ALL, Assessment.class)
        .list();
  }

  private void addFilterIfNeeded(
      Session session, String filterName, String filterParameter, Object parameterValue) {
    if (parameterValue != null) {
      session.enableFilter(filterName).setParameter(filterParameter, parameterValue);
    }
  }

  private void hibernateInitializeIfNeeded(Object o) {
    if (o != null) {
      Hibernate.initialize(o);
    }
  }

  private void hibernateInitializeInstrument(Assessment assessment) {
    final Long instrumentId = assessment.getInstrumentId();
    if (instrumentId == null || assessment.getInstrument() != null) {
      return;
    }

    final Instrument instrument = new Instrument();
    instrument.setId(instrumentId);
    assessment.setInstrument(instrument);

    Hibernate.initialize(instrument);
  }
}
