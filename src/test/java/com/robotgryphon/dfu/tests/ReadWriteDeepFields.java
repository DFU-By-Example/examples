package com.robotgryphon.dfu.tests;

import java.util.concurrent.Executors;
import com.mojang.datafixers.*;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.JsonOps;
import com.robotgryphon.dfu.tests.schema.ComplexSchema;
import com.robotgryphon.dfu.tests.util.FileHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReadWriteDeepFields {

    @Test
    void canReadDeepValue() {
        // Create a DataFixer instance; it holds a couple of versions of our "complex" schemaV1
        var df = makeFixer();

        // Load schema, version 1.
        final Schema schemaV1 = df.getSchema(1);

        // Load up our complex json structure and bind it into a type instance, so we can read values off it
        var jsonRoot = loadAndBindJson(schemaV1);

        /*
         * OpticFinder for finding the "type" field (string) inside a "data" group (defined type)
         * Note that the "type" field is a string field - we also know the DATA_NODE is a complex type in the schemaV1
         */
        final OpticFinder<String> typeFieldFinder = DSL.fieldFinder("type", DSL.string())
                .inField("data", schemaV1.getType(ComplexSchema.DATA_NODE));

        // Find the type string inside our file.
        final var dataTypeValue = jsonRoot.get(typeFieldFinder);

        // Make sure we found the right value.
        Assertions.assertEquals("complex_data_type", dataTypeValue);
    }

    @Test
    void canUpdateDeepValue() {
        // Create a DataFixer instance; it holds a couple of versions of our "complex" schemaV1
        var df = makeFixer();

        // Load schema v1, and bind it to a JSON instance
        final Schema schema = df.getSchema(1);
        final Typed<?> jsonRoot = loadAndBindJson(schema);

        /*
         * OpticFinder for finding the "type" field (string) inside a "data" group (defined type)
         * Note that the "type" field is a string field - we also know the DATA_NODE is a complex type in the schemaV1
         */
        final OpticFinder<String> dataTypeField = DSL.fieldFinder("type", DSL.string())
                .inField("data", schema.getType(ComplexSchema.DATA_NODE));

        /*
         * Here, we specify to update the type field by appending "_new" onto the end of the current value
         * We then read the result back into a JSON object instance
        */
        final var updatedJson = jsonRoot.update(dataTypeField, old -> old + "_new")
                .write()
                .result()
                .orElseThrow()
                .cast(JsonOps.INSTANCE)
                .getAsJsonObject();

        // Check that the type field got updated correctly
        Assertions.assertEquals("complex_data_type_new", updatedJson.get("data").getAsJsonObject().get("type").getAsString());
    }



    static DataFixer makeFixer() {
        DataFixerBuilder builder = new DataFixerBuilder(2);

        builder.addSchema(1, ComplexSchema::new);

        return builder.build(Executors.newSingleThreadExecutor());
    }

    private Typed<?> loadAndBindJson(Schema schema) {
        // Load in an instance of the FILE type. (This will make more sense as this goes)
        var json = FileHelper.INSTANCE.getJsonFromFile("complex.json");

        // Map the loaded JSON to the FILE schema type (binding)
        final var jsonRoot = schema.getType(ComplexSchema.FILE)
                .readTyped(JsonOps.INSTANCE, json)
                .result()
                .orElseThrow()
                .getFirst();

        return jsonRoot;
    }
}
