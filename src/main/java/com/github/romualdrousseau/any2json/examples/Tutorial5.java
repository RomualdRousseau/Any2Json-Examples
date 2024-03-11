package com.github.romualdrousseau.any2json.examples;

import java.util.EnumSet;
import java.util.List;

import com.github.romualdrousseau.any2json.Document;
import com.github.romualdrousseau.any2json.DocumentFactory;
import com.github.romualdrousseau.any2json.parser.LayexTableParser;

public class Tutorial5 implements Runnable {

    public Tutorial5() {
    }

    @Override
    public void run() {
        final var model = Common.loadModelFromGitHub("sales-english");

        // Add product name entity to the model

        model.getEntityList().add("PRODUCTNAME");
        model.getPatternMap().put("\\D+\\dml", "PRODUCTNAME");
        model.getPatternMap().put("(?i)((20|19)\\d{2}-(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)-\\d{2})", "DATE");
        model.update();

        // Add a layex to the model

        final var tableParser = new LayexTableParser(
                List.of("(v.$)+"),
                List.of("(()(.+$.+$))(()(E.+$)())+(e.+$)"));
        model.registerTableParser(tableParser);

        final var file = Common.loadData("document with pivot.xlsx", this.getClass());
        try (final var doc = DocumentFactory.createInstance(file, "UTF-8")
                .setModel(model)
                .setHints(EnumSet.of(Document.Hint.INTELLI_LAYOUT, Document.Hint.INTELLI_TAG))
                .setRecipe(
                        "sheet.setCapillarityThreshold(0)",
                        "sheet.setPivotOption(\"WITH_TYPE_AND_VALUE\")",
                        "sheet.setPivotTypeFormat(\"%s\")")) {

            doc.sheets().forEach(s -> Common.addSheetDebugger(s).getTable().ifPresent(t -> {
                Common.printTags(t.headers());
                Common.printRows(t.rows());
            }));
        }
    }

    public static void main(final String[] args) {
        new Tutorial5().run();
    }
}
