package com.example.pocpdfmergekotlin

import org.apache.pdfbox.multipdf.PDFMergerUtility
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class PDFMergeReq(
    @Size(min = 2)
    val urls: List<String>,
    @NotBlank
    val fileName: String = UUID.randomUUID().toString()
)

@RestController
@RequestMapping("api/v1/pdf-merge")
class PDFMergeController {
    @PostMapping(produces = [MediaType.APPLICATION_PDF_VALUE])
    fun merge(@Valid @RequestBody pdfMergeReq: PDFMergeReq): ResponseEntity<ByteArray> {
        val bytes = mergePDFFiles(pdfMergeReq.urls.map { URL(it).openStream() })

        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=${pdfMergeReq.fileName}")
            .contentLength(bytes.size.toLong())
            .contentType(MediaType.APPLICATION_PDF)
            .body(bytes)
    }

    private fun mergePDFFiles(inputStreams: List<InputStream>): ByteArray {
        val output = ByteArrayOutputStream()

        with(PDFMergerUtility()) {
            destinationStream = output
            addSources(inputStreams)
            mergeDocuments(null)
        }

        return output.toByteArray()
    }
}