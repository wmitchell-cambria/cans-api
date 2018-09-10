package gov.ca.cwds.cans.dao.hibernate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

/**
 * @author volodymyr.petrusha
 */
public class PostgreSqlEnumType extends org.hibernate.type.EnumType {

  public static final String PARAM_ACTIVE = "PostgreSqlEnumType.PARAM_ACTIVE";

  private boolean active = true;

  @Override
  public void nullSafeSet(PreparedStatement st, Object value, int index,
      SharedSessionContractImplementor session) throws SQLException {
    if (active) {
      customNullSafeSet(st, value, index);
    } else {
      super.nullSafeSet(st, value, index, session);
    }
  }

  @Override
  public void setParameterValues(Properties parameters) {
    super.setParameterValues(parameters);
    active = Boolean.parseBoolean(parameters.getProperty(PARAM_ACTIVE, "true"));
  }

  private void customNullSafeSet(PreparedStatement st, Object value, int index)
      throws SQLException {
    if (value == null) {
      st.setNull(index, Types.OTHER);
    } else {
      st.setObject(index, value.toString(), Types.OTHER);
    }
  }
}