package com.github.romualdrousseau.any2json.examples;

import java.util.EnumSet;
import java.util.List;

import com.github.romualdrousseau.any2json.Document;
import com.github.romualdrousseau.any2json.DocumentFactory;
import com.github.romualdrousseau.any2json.parser.LayexTableParser;

public class Tutorial2 implements Runnable {

    public Tutorial2() {
    }

    @Override
    public void run() {

        final var model = Common.loadModel("sales-english", this.getClass());

        // Add product name entity to the model

        model.getEntityList().add("PRODUCTNAME");
        model.getPatternMap().put("\\D+\\dml", "PRODUCTNAME");
        model.update();

        // Add a layex to the model

        final var tableParser = new LayexTableParser(
                List.of("v$"),
                List.of("(()(v+$))(()(S{3,}$)())+(S{2}$)"));
        model.registerTableParser(tableParser);

        final var file = Common.loadData("document with multiple tables.xlsx", this.getClass());
        try (final var doc = DocumentFactory.createInstance(file, "UTF-8")
                .setModel(model)
                .setHints(EnumSet.of(Document.Hint.INTELLI_LAYOUT))
                .setRecipe("sheet.setExtractionThreshold(0)")) {

            doc.sheets().forEach(s -> Common.addSheetDebugger(s).getTable().ifPresent(t -> {
                Common.printHeaders(t.headers());
                Common.printRows(t.rows());
            }));
        }
    }

    public static void main(final String[] args) {
        new Tutorial2().run();
    }
}
