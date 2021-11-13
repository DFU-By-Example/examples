package com.robotgryphon.dfu.tests;

import java.util.concurrent.Executors;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.serialization.JsonOps;
import com.robotgryphon.dfu.tests.schema.complex.ComplexSchema;
import com.robotgryphon.dfu.tests.util.FileHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ComplexSchemaTests {

    @Test
    void canReadData() {
        var df = makeFixer();

        var json = FileHelper.INSTANCE.getJsonFromFile("complex.json");

        final var type = df.getSchema(1)
            .getType(ComplexSchema.DATA_FILE);

        final var read = type.readTyped(JsonOps.INSTANCE, json);

        final var res = read.result().orElseThrow();
        final var data = res.getSecond().getAsJsonObject();

        Assertions.assertEquals("complex_data_type", data.get("type").getAsString());
    }

    static DataFixer makeFixer() {
        DataFixerBuilder builder = new DataFixerBuilder(2);

        builder.addSchema(1, ComplexSchema::new);

        return builder.build(Executors.newSingleThreadExecutor());
    }
}
