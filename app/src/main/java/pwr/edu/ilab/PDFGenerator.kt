package pwr.edu.ilab

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.Toast
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import pwr.edu.ilab.models.ElementResult
import pwr.edu.ilab.models.SingleTestResults
import java.io.File
import java.io.FileOutputStream

@SuppressLint("UseCompatLoadingForDrawables")
fun generatePDF(
    context: Context,
    directory: File,
    logoDrawable: Drawable,
    filename: String,
    results: SingleTestResults
) {
    val filePath = File(directory, "$filename.pdf")
    val fOut = FileOutputStream(filePath)
    val pdfWriter = PdfWriter(fOut)
    val pdfDocument = PdfDocument(pdfWriter)
    val layoutDocument = Document(pdfDocument)
    val font = PdfFontFactory.createFont("assets/fonts/great_sailor.ttf")
    layoutDocument.setFont(font)

    val logoBitmap = drawableToBitmap(logoDrawable)
    val stream = ByteArrayOutputStream()
    logoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    val imageData = ImageDataFactory.create(stream.toByteArray())
    val logo = Image(imageData)
        .setHorizontalAlignment(HorizontalAlignment.CENTER)
        .setWidth(logoBitmap.width / 2f)
        .setHeight(logoBitmap.height / 2f)
    layoutDocument.add(logo)

    layoutDocument.add(
        Paragraph("Wyniki badan wykonanych dnia: ${results.date.replace("-", ".")}")
            .setTextAlignment(TextAlignment.LEFT)
            .setPaddingTop(30f)
            .setPaddingLeft(4f)
    )

    addTable(layoutDocument, results.results)
    layoutDocument.close()
    Toast.makeText(context, "PDF file generated at: $filePath", Toast.LENGTH_LONG).show()
}

fun drawableToBitmap(drawable: Drawable): Bitmap {

    if (drawable is BitmapDrawable) {
        return drawable.bitmap;
    }

    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    );
    val canvas = Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.width, canvas.height);
    drawable.draw(canvas);

    return bitmap;
}

fun addTable(layoutDocument: Document, items: List<ElementResult>) {
    val table = Table(
        UnitValue.createPointArray(
            floatArrayOf(
                150f,
                80f,
                150f,
                80f,
            )
        )
    ).setHorizontalAlignment(HorizontalAlignment.CENTER)

    table.addCell(Paragraph("Substancja").setBold().setTextAlignment(TextAlignment.CENTER))
    table.addCell(Paragraph("Wynik").setBold().setTextAlignment(TextAlignment.CENTER))
    table.addCell(Paragraph("Prawidlowy zakres").setBold().setTextAlignment(TextAlignment.CENTER))
    table.addCell(Paragraph("Jednostka").setBold().setTextAlignment(TextAlignment.CENTER))

    for (a in items) {
        table.addCell(Paragraph(a.name).setTextAlignment(TextAlignment.CENTER))
        table.addCell(Paragraph("${a.result}").setTextAlignment(TextAlignment.CENTER))
        table.addCell(Paragraph("${a.rangeStart} - ${a.rangeEnd}").setTextAlignment(TextAlignment.CENTER))
        table.addCell(Paragraph(a.unit).setTextAlignment(TextAlignment.CENTER))
    }
    layoutDocument.add(table)
}
