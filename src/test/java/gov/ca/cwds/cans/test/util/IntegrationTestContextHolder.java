package gov.ca.cwds.cans.test.util;

import gov.ca.cwds.cans.CansConfiguration;
import gov.ca.cwds.cans.test.AbstractRestClientTestRule;

/**
 * @author denys.davydov
 */
public final class IntegrationTestContextHolder {

  private IntegrationTestContextHolder() {
  }

  public static CansConfiguration cansConfiguration = null;

  public static AbstractRestClientTestRule clientTestRule = null;
}
