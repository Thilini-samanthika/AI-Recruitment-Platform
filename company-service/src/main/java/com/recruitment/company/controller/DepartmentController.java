package com.recruitment.company.controller;

import com.recruitment.company.dto.ApiResponse;
import com.recruitment.company.dto.CreateDepartmentRequest;
import com.recruitment.company.dto.DepartmentResponse;
import com.recruitment.company.dto.UpdateDepartmentRequest;
import com.recruitment.company.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/companies/{companyId}/departments")
@RequiredArgsConstructor
@Tag(name = "Department Management", description = "Endpoints for managing company organizational units and departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @Operation(
            summary = "Create a company department",
            description = "Creates a new department under the specified company. Protected: requires owning ROLE_COMPANY or ROLE_ADMIN.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Department created successfully",
                    content = @Content(schema = @Schema(implementation = DepartmentResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error / Missing required fields",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing authentication",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Caller does not own this company",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Department name already exists in this company",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(
            @Parameter(description = "Company MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345678")
            @PathVariable String companyId,
            @Valid @RequestBody CreateDepartmentRequest request,
            @Parameter(description = "Authenticated User ID forwarded by Gateway", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(description = "User Role (e.g. ROLE_COMPANY, ROLE_ADMIN)", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to create department '{}' in company ID: {}", request.getName(), companyId);
        DepartmentResponse response = departmentService.createDepartment(companyId, request, userId, role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Department created successfully", response));
    }

    @GetMapping
    @Operation(
            summary = "List departments for a company",
            description = "Retrieves all departments belonging to the specified company. Public / Authenticated."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Departments retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DepartmentResponse.class)))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getDepartments(
            @Parameter(description = "Company MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345678")
            @PathVariable String companyId) {
        log.debug("REST request to list departments for company ID: {}", companyId);
        List<DepartmentResponse> departments = departmentService.getDepartmentsByCompanyId(companyId);
        return ResponseEntity.ok(ApiResponse.success("Departments retrieved successfully", departments));
    }

    @GetMapping("/{departmentId}")
    @Operation(
            summary = "Get department by ID",
            description = "Retrieves details of a specific department in a company. Public / Authenticated."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Department found",
                    content = @Content(schema = @Schema(implementation = DepartmentResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Department or company not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentById(
            @Parameter(description = "Company MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345678")
            @PathVariable String companyId,
            @Parameter(description = "Department MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345679")
            @PathVariable String departmentId) {
        log.debug("REST request to get department ID: {} in company ID: {}", departmentId, companyId);
        DepartmentResponse response = departmentService.getDepartmentById(companyId, departmentId);
        return ResponseEntity.ok(ApiResponse.success("Department retrieved successfully", response));
    }

    @PutMapping("/{departmentId}")
    @Operation(
            summary = "Update department",
            description = "Updates department name, description, or head. Protected: requires owning ROLE_COMPANY or ROLE_ADMIN.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Department updated successfully",
                    content = @Content(schema = @Schema(implementation = DepartmentResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing authentication",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Access denied",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Department not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Department name conflict",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @Parameter(description = "Company MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345678")
            @PathVariable String companyId,
            @Parameter(description = "Department MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345679")
            @PathVariable String departmentId,
            @Valid @RequestBody UpdateDepartmentRequest request,
            @Parameter(description = "Authenticated User ID forwarded by Gateway", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(description = "User Role (e.g. ROLE_COMPANY, ROLE_ADMIN)", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to update department ID: {} in company ID: {}", departmentId, companyId);
        DepartmentResponse response = departmentService.updateDepartment(companyId, departmentId, request, userId, role);
        return ResponseEntity.ok(ApiResponse.success("Department updated successfully", response));
    }

    @DeleteMapping("/{departmentId}")
    @Operation(
            summary = "Delete department",
            description = "Deletes a department and unassigns recruiters from it. Protected: requires owning ROLE_COMPANY or ROLE_ADMIN.",
            security = { @SecurityRequirement(name = "apiKeyAuth"), @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Department deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing authentication",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Access denied",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Department not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(
            @Parameter(description = "Company MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345678")
            @PathVariable String companyId,
            @Parameter(description = "Department MongoDB ObjectId", required = true, example = "66c25a1f2b3e8c0012345679")
            @PathVariable String departmentId,
            @Parameter(description = "Authenticated User ID forwarded by Gateway", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(description = "User Role (e.g. ROLE_COMPANY, ROLE_ADMIN)", hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST request to delete department ID: {} in company ID: {}", departmentId, companyId);
        departmentService.deleteDepartment(companyId, departmentId, userId, role);
        return ResponseEntity.ok(ApiResponse.success("Department deleted successfully", null));
    }
}
