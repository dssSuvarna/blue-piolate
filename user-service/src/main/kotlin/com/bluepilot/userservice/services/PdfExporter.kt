package com.bluepilot.userservice.services

import com.bluepilot.entities.EmployeeSalary
import com.bluepilot.entities.User
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.xhtmlrenderer.pdf.ITextRenderer
import java.io.ByteArrayOutputStream

@Component
@Transactional
class PdfExporter @Autowired constructor(private val templateEngine: TemplateEngine) {
    fun payslipExporter(employeeSalaryDetail: EmployeeSalary, user: User): ByteArray {
        val context = Context().apply {
            setVariable("data", employeeSalaryDetail)
            setVariable("name", "${user.firstName} ${user.lastName}")
            setVariable("bonus", employeeSalaryDetail.oneTimeIncentive + employeeSalaryDetail.performanceIncentive)
        }
        val htmlContent = templateEngine.process("payslip_template", context)

        val document = Jsoup.parse(htmlContent, "UTF-8")
        document.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml)

        val outputStream = ByteArrayOutputStream()
        val renderer = ITextRenderer(266.toFloat(), 90)
        renderer.setDocumentFromString(document.html())
        renderer.layout()
        renderer.createPDF(outputStream)
        return outputStream.toByteArray()
    }
}