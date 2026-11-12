package com.taxiproject.mytaxi.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.taxiproject.mytaxi.Dto.ReportResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;

@Service
public class PdfExportService {

    @Autowired
    private TemplateEngine templateEngine;

    public byte[] generateReportPdf(ReportResponseDto report) throws Exception {
        Context context = new Context();
        context.setVariable("report", report);

        String html = templateEngine.process("pdf/report-template", context);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();

        builder.useFont(new File("C:/Windows/Fonts/arial.ttf"), "Arial");
        builder.useFastMode();
        builder.withHtmlContent(html, null);
        builder.toStream(os);
        builder.run();

        return os.toByteArray();
    }
}
