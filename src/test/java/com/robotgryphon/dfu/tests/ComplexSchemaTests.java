package com.robotgryphon.dfu.tests;

import java.util.concurrent.Executors;
import com.google.gson.JsonElement;
import com.mojang.datafixers.*;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.robotgryphon.dfu.tests.schema.complex.ComplexSchema;
import com.robotgryphon.dfu.tests.util.FileHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ComplexSchemaTests {

    @Test
    void canReadDeepValue() {
        var df = makeFixer();

        var json = FileHelper.INSTANCE.getJsonFromFile("complex.json");

        final Schema schema = df.getSchema(1);

        final var jsonRoot = schema.getType(ComplexSchema.FILE)
                .readTyped(JsonOps.INSTANCE, json)
                .result()
                .orElseThrow()
                .getFirst();

        // OF for finding the "type" field (string) inside a "data" group (defined type)
        final OpticFinder<String> dataTypeField = DSL.fieldFinder("type", DSL.string())
                .inField("data", schema.getType(ComplexSchema.DATA_NODE));

        final var dataTypeValue = jsonRoot.get(dataTypeField);

        Assertions.assertEquals("complex_data_type", dataTypeValue);
    }

    @Test
    void canUpdateDeepValue() {
        var df = makeFixer();

        var json = FileHelper.INSTANCE.getJsonFromFile("complex.json");

        final Schema schema = df.getSchema(1);

        final var jsonRoot = schema.getType(ComplexSchema.FILE)
                .readTyped(JsonOps.INSTANCE, json)
                .result()
                .orElseThrow()
                .getFirst();

        // OF for finding the "type" field (string) inside a "data" group (defined type)
        final OpticFinder<String> dataTypeField = DSL.fieldFinder("type", DSL.string())
                .inField("data", schema.getType(ComplexSchema.DATA_NODE));

        // read updated JSON
        final var updatedJson = jsonRoot.update(dataTypeField, old -> old + "_new")
                .write()
                .result()
                .orElseThrow()
                .cast(JsonOps.INSTANCE)
                .getAsJsonObject();

        Assertions.assertEquals("complex_data_type_new", updatedJson.get("data").getAsJsonObject().get("type").getAsString());
    }

    static DataFixer makeFixer() {
        DataFixerBuilder builder = new DataFixerBuilder(2);

        builder.addSchema(1, ComplexSchema::new);

        return builder.build(Executors.newSingleThreadExecutor());
    }
}
