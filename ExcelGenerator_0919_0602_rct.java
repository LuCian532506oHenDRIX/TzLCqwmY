// 代码生成时间: 2025-09-19 06:02:59
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.ByteStreams;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelGenerator {

    private static final String EXCEL_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String EXCEL_EXTENSION = ".xlsx";
    private static final AtomicInteger fileCounter = new AtomicInteger(0);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        Javalin app = Javalin.create()
                .staticFiles.Location(Location.EXTERNAL)
                .start(7000);
        setupEndpoints(app);
    }

    private static void setupEndpoints(Javalin app) {
        app.get("/generateExcel", ctx -> {
            try {
                String fileName = "ExcelReport" + fileCounter.incrementAndGet() + EXCEL_EXTENSION;
                ctx.result(createExcelFile());
                ctx.contentType(EXCEL_MIME_TYPE);
                ctx.res.setHeader("Content-Disposition", "attachment; filename=