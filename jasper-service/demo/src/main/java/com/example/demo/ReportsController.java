package com.example.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Dtos.StockReportRequest;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

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
    @PostMapping("/StockReports")
    public ResponseEntity<byte[]> generateStockReport(@org.springframework.web.bind.annotation.RequestBody StockReportRequest request) {
        try {
            InputStream jrxmlInput = getClass().getResourceAsStream("/templates/StockAlerts.jrxml");
            if (jrxmlInput == null) throw new RuntimeException("Could not find StockAlerts.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlInput);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("LowStockThreshold", request.getLowStockThreshold());

            String dbUrl = System.getenv("DB_URL");
            String dbUser = System.getenv("DB_USER");
            String dbPass = System.getenv("DB_PASS");

            if (dbUrl == null || dbUser == null || dbPass == null)
                throw new RuntimeException("Database environment variables not set");

            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
                JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, conn);

                String format = (request.getFormat() == null) ? "pdf" : request.getFormat().toLowerCase();
                HttpHeaders headers = new HttpHeaders();
                byte[] fileBytes;
                String filename;

                switch (format) {
                    case "xlsx":
                        ByteArrayOutputStream xlsxOut = new ByteArrayOutputStream();
                        JRXlsxExporter xlsxExporter = new JRXlsxExporter();
                        xlsxExporter.setExporterInput(new SimpleExporterInput(print)); // Correct
                        xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(xlsxOut));
                        xlsxExporter.exportReport();
                        fileBytes = xlsxOut.toByteArray();
                        headers.setContentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                        filename = "stock_report.xlsx";
                        break;
                    case "pdf":
                    default:
                        fileBytes = JasperExportManager.exportReportToPdf(print);
                        headers.setContentType(MediaType.APPLICATION_PDF);
                        filename = "stock_report.pdf";
                        break;
                }

                headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
                return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());
        }
    }
}
