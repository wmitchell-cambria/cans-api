package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.Constants.API.ID;
import static gov.ca.cwds.cans.Constants.API.SEARCH;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;
import static gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer.CANS_ROLLOUT_PERMISSION;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentMetaDto;
import gov.ca.cwds.cans.domain.dto.assessment.SearchAssessmentRequest;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.mapper.AssessmentMapper;
import gov.ca.cwds.cans.domain.mapper.search.SearchAssessmentRequestMapper;
import gov.ca.cwds.cans.domain.search.SearchAssessmentParameters;
import gov.ca.cwds.cans.rest.ResponseUtil;
import gov.ca.cwds.cans.service.AssessmentService;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/** @author denys.davydov */
@Api(value = ASSESSMENTS, tags = ASSESSMENTS)
@Path(value = ASSESSMENTS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AssessmentResource {

  private final AssessmentService assessmentService;
  private final AssessmentMapper assessmentMapper;
  private final ACrudResource<Assessment, AssessmentDto> crudResource;
  private final SearchAssessmentRequestMapper searchAssessmentMapper;

  @Inject
  public AssessmentResource(
      AssessmentService assessmentService,
      AssessmentMapper assessmentMapper,
      SearchAssessmentRequestMapper searchAssessmentMapper) {
    this.assessmentService = assessmentService;
    this.assessmentMapper = assessmentMapper;
    crudResource = new ACrudResource<>(assessmentService, assessmentMapper);
    this.searchAssessmentMapper = searchAssessmentMapper;
  }

  @UnitOfWork(CANS)
  @POST
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Not found")
      })
  @ApiOperation(value = "Post new Assessment", response = AssessmentDto.class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response post(
      @ApiParam(name = "Assessment", value = "The Assessment object") @Valid
          final AssessmentDto dto) {
    return crudResource.post(dto);
  }

  @UnitOfWork(CANS)
  @PUT
  @Path("/{" + ID + "}")
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Not found")
      })
  @ApiOperation(value = "Update existent Assessment", response = AssessmentDto.class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response put(
      @PathParam("id")
          @ApiParam(required = true, name = "id", value = "The Assessment id", example = "50000")
          final Long id,
      @ApiParam(name = "Assessment", value = "The Assessment object") @Valid
          final AssessmentDto dto) {
    return crudResource.put(id, dto);
  }

  @UnitOfWork(CANS)
  @GET
  @Path("/{" + ID + "}")
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Not found")
      })
  @ApiOperation(value = "Get Assessment by id", response = AssessmentDto.class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response get(
      @PathParam("id")
          @ApiParam(required = true, name = "id", value = "The Assessment id", example = "50000")
          final Long id) {
    return crudResource.get(id);
  }

  @UnitOfWork(CANS)
  @POST
  @Path(SEARCH)
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
      })
  @ApiOperation(
      value =
          "Search assessments by parameters, returns records created by the logged in user ONLY",
      response = AssessmentDto[].class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response search(
      @ApiParam(
              required = true,
              name = "Search Parameters",
              value = "Search assessments parameters")
          @NotNull
          final SearchAssessmentRequest searchRequest) {
    final SearchAssessmentParameters searchAssessmentParameters =
        searchAssessmentMapper.fromSearchRequest(searchRequest);
    final Collection<Assessment> entities = assessmentService.search(searchAssessmentParameters);
    final Collection<AssessmentMetaDto> dtos = assessmentMapper.toShortDtos(entities);
    return ResponseUtil.responseCreatedOrNot(dtos);
  }

  @UnitOfWork(CANS)
  @DELETE
  @Path("/{" + ID + "}")
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Not found")
      })
  @ApiOperation(value = "Delete Assessment by id", response = AssessmentDto.class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response delete(
      @PathParam("id")
          @ApiParam(required = true, name = "id", value = "The Assessment id", example = "50000")
          final Long id) {
    return crudResource.delete(id);
  }
}
