package com.robotgryphon.dfu.tests;

import java.util.List;
import java.util.concurrent.Executors;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.*;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
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

        final Schema schema = df.getSchema(1);
        final var allData = schema.getType(ComplexSchema.DATA_FILE);

        final var allDataRead = allData.readTyped(JsonOps.INSTANCE, json);
        final var allDataReadResult = allDataRead.result().orElseThrow();
        final var data = allDataReadResult.getFirst();

        final Typed<?> updateResult = data.update(DSL.field("version", DSL.intType()).finder(), (old) -> {
            int o = (int) old;
            return 2;
        });

        var j = updateResult.write();
        final JsonObject afterUpdate = j.result().orElseThrow().cast(JsonOps.INSTANCE).getAsJsonObject();
        Assertions.assertEquals(2,  afterUpdate.get("version").getAsInt());
    }

    static DataFixer makeFixer() {
        DataFixerBuilder builder = new DataFixerBuilder(2);

        builder.addSchema(1, ComplexSchema::new);

        return builder.build(Executors.newSingleThreadExecutor());
    }
}
