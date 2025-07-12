package com.example.demo;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;

import net.sf.jasperreports.engine.*;

@RestController
@RequestMapping("api/reports")
public class ReportsController {

    @Operation(
        summary = "Generate Stock Alert PDF Report",
        description = "Generates a PDF report for items with stock below the specified threshold."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF report generated", 
            content = @Content(mediaType = "application/pdf")),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/StockReports")
    public ResponseEntity<byte[]> generateStockReport(
        @Parameter(description = "Minimum stock level to include in the report", example = "10")
        @RequestParam BigDecimal LowStockThreshold
    ) {
        try {
            // Load the JRXML report
            InputStream jrxmlInput = getClass().getResourceAsStream("/templates/StockAlerts.jrxml");
            if (jrxmlInput == null) {
                throw new RuntimeException("Could not find StockAlerts.jrxml in /templates.");
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlInput);

            // Parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("LowStockThreshold", LowStockThreshold);

            // PostgreSQL Connection
            String dbUrl = System.getenv("DB_URL");
            String dbUser = System.getenv("DB_USER");
            String dbPass = System.getenv("DB_PASS");

            if (dbUrl == null || dbUser == null || dbPass == null) {
                throw new RuntimeException("Database environment variables are not set properly.");
            }

            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
                JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, conn);
                byte[] pdf = JasperExportManager.exportReportToPdf(print);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDisposition(ContentDisposition.attachment().filename("stock_report.pdf").build());

                return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error generating report: " + e.getMessage()).getBytes());
        }
    }
}
