package com.example.myapplication.utils;

import com.example.myapplication.entity.PersonBirthday;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class PersonBirthdayExcelFileParser implements PersonBirthdayFileParser {
    private final static int NAME_AND_SURNAME_CELL_INDEX = 0;
    private final static int BIRTH_DATE_CELL_INDEX = 1;
    @Override
    public List<PersonBirthday> parse(InputStream inputStream) {

        List<PersonBirthday> parsedList = new ArrayList<>();

        XSSFWorkbook workbook;
        try {
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        XSSFSheet sheet = workbook.getSheetAt(NAME_AND_SURNAME_CELL_INDEX);
        Iterator<Row> rowIterator = sheet.rowIterator();

        while (rowIterator.hasNext()) {
            Row nextRow = rowIterator.next();

            int id = nextRow.getRowNum();

            String nameAndSurname = nextRow.getCell(NAME_AND_SURNAME_CELL_INDEX).getStringCellValue();
            String[] nameAndSurnameArr = nameAndSurname.split(" ");

            String name = Objects.requireNonNull(getNameAndSurnameFromArray(nameAndSurnameArr)).name();
            String surname = Objects.requireNonNull(getNameAndSurnameFromArray(nameAndSurnameArr)).surname();
            LocalDate localDateRaw = nextRow.getCell(BIRTH_DATE_CELL_INDEX).getLocalDateTimeCellValue().toLocalDate();
            LocalDate localDate = localDateRaw.withYear(LocalDate.now().getYear());

            parsedList.add(new PersonBirthday(name, surname, localDate));
        }
        return parsedList;
    }

    /**
     * @param arr incoming array with data extracted from NAME_AND_SURNAME_CELL_INDEX cell
     * @return model NameAndSurname which consists of name as arr[0] and surname as arr[1...arr.length]
     */
    private NameAndSurname getNameAndSurnameFromArray(String[] arr) {
        if (arr.length == 1) {
            return new NameAndSurname(arr[0], null);
        } else if (arr.length > 1) {
            String[] surnameArr = new String[arr.length - 1];
            System.arraycopy(arr, 1, surnameArr, 0, arr.length - 1);
            return new NameAndSurname(arr[0], String.join(" ", surnameArr));
        }
        return null;
    }

    private record NameAndSurname(String name, String surname) {
    }
}
