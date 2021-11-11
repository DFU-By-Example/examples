import java.util.Optional;
import java.util.concurrent.Executors;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.*;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.JsonOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestSimpleVersionUpdate {

    @Test
    void testSimpleUpdate() {
        var fixer = buildFixer();
        JsonObject in = inputData();

        final JsonObject jsonOut = fixer.update(BasicSchema.DATA, new Dynamic<>(JsonOps.INSTANCE, in), 1, 2)
                .getValue()
                .getAsJsonObject();

        Assertions.assertTrue(jsonOut.has("data"));
        Assertions.assertEquals("hithere2", jsonOut.get("data").getAsString());

        Assertions.assertTrue(jsonOut.has("data2"));
        Assertions.assertEquals("donottouch", jsonOut.get("data2").getAsString());
    }

    private JsonObject inputData() {
        JsonObject in = new JsonObject();
        in.addProperty("data", "hithere");
        in.addProperty("data2", "donottouch");
        return in;
    }

    private DataFixer buildFixer() {
        DataFixerBuilder builder = new DataFixerBuilder(3);
        final Schema v1 = builder.addSchema(1, BasicSchema::new);
        final Schema v2 = builder.addSchema(2, Schema::new);
        builder.addFixer(new UpdateDataValue(v2, "hithere2"));
        final Schema v3 = builder.addSchema(3, Schema::new);
        builder.addFixer(new UpdateDataValue(v3, "hello_there"));


        return builder.build(Executors.newSingleThreadExecutor());
    }

}
