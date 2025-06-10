

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Logger;

class PdfStatementGenerator {
    private static final Logger logger = Logger.getLogger(PdfStatementGenerator.class.getName());

    public static void generate(Account account, List<Transaction> transactions, String filePath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            try {
                PDImageXObject logoImage = PDImageXObject.createFromFile("logo.png", document);
                contentStream.drawImage(logoImage, 50, 720, 70, 70);
            } catch (IOException e) {
                logger.warning("logo.png not found. PDF will be generated without a logo.");
            }

            if (account.profilePicture != null) {
                PDImageXObject profileImage = PDImageXObject.createFromByteArray(document, account.profilePicture, account.customerName);
                contentStream.drawImage(profileImage, 480, 680, 80, 80);
            }

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
            contentStream.newLineAtOffset(140, 750);
            contentStream.showText("BT Bank Statement");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(50, 680);
            contentStream.showText("Customer Name: " + account.customerName);
            contentStream.newLine();
            contentStream.showText("Account Number: " + account.accountNumber);
            contentStream.newLine();
            contentStream.showText("Address: " + account.address);
            contentStream.newLine();
            contentStream.showText("Statement Date: " + new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
            contentStream.endText();

            drawTable(contentStream, transactions);

            contentStream.close();
            document.save(filePath);
        }
    }

    private static void drawTable(PDPageContentStream contentStream, List<Transaction> transactions) throws IOException {
        float yPosition = 600;
        final float tableWidth = 500.0f;
        final float yStart = 600;
        final float margin = 50;

        float[] colWidths = {150, 250, 100};
        String[] headers = {"Date / Time", "Description", "Amount (INR)"};

        drawRow(contentStream, headers, yStart, margin, colWidths, PDType1Font.HELVETICA_BOLD);
        yPosition -= 20;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Transaction tx : transactions) {
            String[] rowData = {
                    sdf.format(tx.getTimestamp()),
                    tx.getType(),
                    String.format("%.2f", tx.getAmount())
            };
            drawRow(contentStream, rowData, yPosition, margin, colWidths, PDType1Font.HELVETICA);
            yPosition -= 20;
        }
    }

    private static void drawRow(PDPageContentStream stream, String[] data, float y, float margin, float[] colWidths, PDType1Font font) throws IOException {
        stream.setFont(font, 10);
        float x = margin;
        for (int i = 0; i < data.length; i++) {
            stream.beginText();
            stream.newLineAtOffset(x + 5, y);
            stream.showText(data[i]);
            stream.endText();
            x += colWidths[i];
        }
    }
}