package com.robotgryphon.dfu.tests;

import java.util.concurrent.Executors;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.robotgryphon.dfu.tests.fixes.UpdateDataNodeV2;
import com.robotgryphon.dfu.tests.schema.ComplexSchema;
import com.robotgryphon.dfu.tests.schema.ComplexSchemaV2;
import com.robotgryphon.dfu.tests.util.FileHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UpdateComplexNode {

    @Test
    void doDataUpdate() {
        // Create a DataFixer instance; it holds a couple of versions of our "complex" schema
        var df = makeFixer();

        final Dynamic<JsonElement> updated = df.update(ComplexSchema.FILE,
                new Dynamic<>(JsonOps.INSTANCE, FileHelper.INSTANCE.getJsonFromFile("complex.json")),
                1, 2);

        final JsonObject result = updated.cast(JsonOps.INSTANCE).getAsJsonObject().getAsJsonObject("data");

        Assertions.assertTrue(result.has("version"));
        Assertions.assertEquals(2, result.get("version").getAsInt());
    }

    static DataFixer makeFixer() {
        DataFixerBuilder builder = new DataFixerBuilder(2);

        builder.addSchema(1, ComplexSchema::new);
        final Schema schemaV2 = builder.addSchema(2, ComplexSchemaV2::new);
        builder.addFixer(new UpdateDataNodeV2(schemaV2));

        return builder.build(Executors.newSingleThreadExecutor());
    }
}
