package com.bluepilot.userservice.services

import com.bluepilot.entities.CMSSheet
import com.bluepilot.entities.ESIAndPFDetails
import com.bluepilot.entities.EmployeeSalary
import com.bluepilot.enums.Month
import com.bluepilot.repositories.UserRepository
import com.bluepilot.userservice.constants.ESiAndPfFieldsMap
import com.bluepilot.userservice.constants.EmployeeSalaryFieldsMap
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.full.memberProperties


@Component
@Transactional
class SheetExporter @Autowired constructor(
    private val userRepository: UserRepository
) {

    @Value("\${config.esi.file-name}")
    private lateinit var fileName: String

    @Value("\${config.esi.sheet-name}")
    private lateinit var sheetName: String

    @Value("\${config.cms.product-code}")
    private lateinit var cmsProductCode: String

    @Value("\${config.cms.payment-type}")
    private lateinit var cmsPaymentType: String

    @Value("\${config.cms.debit-account-number}")
    private lateinit var cmsDebitAccNo: String

    @Value("\${config.cms.client-code}")
    private lateinit var cmsClientCode: String

    @Value("\${config.cms.debit-narration}")
    private lateinit var cmsDebitNarration: String

    @Value("\${config.cms.credit-narration}")
    private lateinit var cmsCreditNarration: String

    @Value("\${config.cms.bank-code}")
    private lateinit var cmsBankCode: String

    fun exportESISheet(esiAndPfDetails: List<ESIAndPFDetails>): Resource {
        val fieldsWithValues = mutableMapOf<String, String>()
        var dataValues = mutableListOf<String>()
        val memberProperties = ESIAndPFDetails::class.memberProperties
        val data = mutableListOf<List<String>>()
        for (element in esiAndPfDetails) {
            memberProperties.forEach {
                val value = it.get(element) ?: ""
                run {
                    fieldsWithValues[it.name] = value.toString()
                }
            }
            for (esiDetail in ESiAndPfFieldsMap.esiAndPfDetailsMap) {
                if (fieldsWithValues.keys.contains(esiDetail.name)) {
                    dataValues.add(fieldsWithValues[esiDetail.name]!!)
                }
            }
            data.add(dataValues)
            dataValues = mutableListOf()
        }

        val inputStream = UserService::class.java.getResourceAsStream("/${fileName}")
        val workbook = WorkbookFactory.create(inputStream)
        val newSheet = workbook.getSheet(sheetName)
        val lastRowNum = 4
        val cc = newSheet.getRow(1)
        val cell1 = cc.createCell(1)
        cell1.setCellValue(ZonedDateTime.now().month.name)
        for (i in data.indices) {
            val row = newSheet.createRow(lastRowNum + i + 1)
            for (j in data[i].indices) {
                val cell = row.createCell(j)
                cell.setCellValue(data[i][j])
            }
        }
        inputStream?.close()
        val fileOutputStream = FileOutputStream(fileName)
        workbook.write(fileOutputStream)
        fileOutputStream.close()
        val outPutFile = File(fileName)
        return FileSystemResource(outPutFile)
    }

    fun exportCMSSheet(employeeSalaryDetails: List<EmployeeSalary>, month: Month, year: Int): Resource {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        val date = dateFormatter.format(LocalDateTime.now())
        val cmsSheetDetails = employeeSalaryDetails.map {
            val user = userRepository.findById(it.userId).get()
            CMSSheet(
                date = date.toString(),
                salaryAmount = it.grossPay.toString(),
                beneficiaryName = user.userDetails!!.bankDetails!!.accountHolderName,
                beneficiaryAccountNumber = user.userDetails!!.bankDetails!!.accountNumber.toString(),
                beneficiaryIfsc = user.userDetails!!.bankDetails!!.ifsc,
                clientCode = cmsClientCode,
                productCode = cmsProductCode,
                paymentType = cmsPaymentType,
                debitAccountNumber = cmsDebitAccNo,
                bankCode = cmsBankCode,
                debitNarration = cmsDebitNarration,
                creditNarration = cmsCreditNarration
            )
        }

        val fieldsWithValues = mutableMapOf<String, String>()
        val memberProperties = CMSSheet::class.memberProperties
        var dataValues = mutableListOf<String>()
        val data = mutableListOf<List<String>>()
        for (element in  cmsSheetDetails){
            memberProperties.forEach {
                val value = it.get(element) ?: ""
                run {
                    fieldsWithValues[it.name] = value.toString()
                }
            }
            for (employeeSalaryField in EmployeeSalaryFieldsMap.employeeSalaryFieldsMap) {
                if (fieldsWithValues.keys.contains(employeeSalaryField)) {
                    dataValues.add(fieldsWithValues[employeeSalaryField]!!)
                }
            }
            data.add(dataValues)
            dataValues = mutableListOf()
        }

        val inputStream = UserService::class.java.getResourceAsStream("/salary_payments.xlsx")
        val workbook = WorkbookFactory.create(inputStream)
        val newSheet = workbook.getSheet("month")

        /* Setting font size to 14 */
        val font = workbook.createFont()
        font.fontHeightInPoints = 14.toShort()

        /* Setting the name of the sheet to the corresponding month */
        val sheetIndex = workbook.getSheetIndex(newSheet)
        workbook.setSheetName(sheetIndex, "${month.name} $year")

        val headerRow = newSheet.getRow(0)
        val totalColumns = headerRow.lastCellNum
        for (i in data.indices) {
            val row = newSheet.createRow(  i + 1)
            var valueIndex = 0
            for (j in 0 until totalColumns) {
                val headerCell = headerRow.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
                if(headerCell != null) {
                    val cell = row.createCell(j)
                    cell.setCellValue(data[i][valueIndex])
                    cell.cellStyle.setFont(font)
                    valueIndex++
                }
            }
        }
        inputStream?.close()
        val fileOutputStream = FileOutputStream("cms.xlsx")
        workbook.write(fileOutputStream)
        fileOutputStream.close()
        val outPutFile = File("cms.xlsx")
        return FileSystemResource(outPutFile)
    }
}