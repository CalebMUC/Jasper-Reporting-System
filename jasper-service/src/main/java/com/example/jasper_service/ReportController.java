package main.java.com.example.jasper_service;

import net.sf.jasperreports.engine.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @GetMapping("/sales")
    public ResponseEntity<byte[]> generateReport(
        @RequestParam String fromDate,
        @RequestParam String toDate
    ) throws Exception {

        InputStream jrxmlInput = getClass().getResourceAsStream("/reports/sales_report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlInput);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("FromDate", fromDate);
        parameters.put("ToDate", toDate);

        Connection conn = DriverManager.getConnection(
            System.getenv("DB_URL"),
            System.getenv("DB_USER"),
            System.getenv("DB_PASS")
        );

        JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, conn);
        byte[] pdf = JasperExportManager.exportReportToPdf(print);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "sales_report.pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
