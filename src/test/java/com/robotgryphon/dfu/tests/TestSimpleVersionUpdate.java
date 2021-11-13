package com.robotgryphon.dfu.tests;

import java.util.concurrent.Executors;
import com.google.gson.JsonObject;
import com.mojang.datafixers.*;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.JsonOps;
import com.robotgryphon.dfu.tests.fixes.UpdateDataValue;
import com.robotgryphon.dfu.tests.schema.BasicSchema;
import com.robotgryphon.dfu.tests.util.FileHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestSimpleVersionUpdate {

    @Test
    void testSimpleUpdate() {
        // Build up a DataFixer instance for testing use.
        var fixer = buildFixer();

        // Get some input data. For us, we just define two fields on an object.
        JsonObject in = FileHelper.INSTANCE.getJsonFromFile("simple_data.json").getAsJsonObject();

        // Run the input data through a v1 to v2 update. We expect that everything except "data" is left alone,
        // and that "data" is updated via com.robotgryphon.dfu.tests.fixes.UpdateDataValue to be "hithere2" after the update.
        final JsonObject jsonOut = fixer.update(BasicSchema.DATA, new Dynamic<>(JsonOps.INSTANCE, in), 1, 2)
                .getValue()
                .getAsJsonObject();

        // Ensure that data has updated to "hithere2"
        Assertions.assertTrue(jsonOut.has("data"));
        Assertions.assertEquals("hithere2", jsonOut.get("data").getAsString());

        // Ensure that nothing else has changed on the input data.
        Assertions.assertTrue(jsonOut.has("data2"));
        Assertions.assertEquals("donottouch", jsonOut.get("data2").getAsString());
    }

    private DataFixer buildFixer() {
        // We need a data builder. Here we specify that the last version of the fixer is "3"
        DataFixerBuilder builder = new DataFixerBuilder(3);

        // Define a v1 to use as a base.
        final Schema v1 = builder.addSchema(1, BasicSchema::new);

        // v2 keeps the same structure as v1; in Mojang's real world usage, they call ::new
        final Schema v2 = builder.addSchema(2, Schema::new);

        // v2 has a single update that's necessary - we update "data" to "hithere2"
        // this is a simplified example, obviously you can do fancier mappings here
        builder.addFixer(new UpdateDataValue(v2, "hithere2"));

        // v3 keeps the same schema again, but it updates "data" to be "hello_there"
        final Schema v3 = builder.addSchema(3, Schema::new);
        builder.addFixer(new UpdateDataValue(v3, "hello_there"));

        // in Minecraft code, this uses the bootstrapExecutor; see DataFixesManager#createFixerUpper
        return builder.build(Executors.newSingleThreadExecutor());
    }

}
