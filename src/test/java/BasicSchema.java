import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

class BasicSchema extends Schema {
    public static final DSL.TypeReference DATA = () -> "data";

    public BasicSchema(int version, Schema parent) {
        super(version, parent);
    }

    @Override
    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entities, Map<String, Supplier<TypeTemplate>> blocks) {
        schema.registerType(false, DATA, () -> {
            return DSL.field("data", DSL.constType(DSL.string()));
        });

        // super constructor requires at least one recursive type, issue with Schema#50 (RECURSIVE_TYPES)
        schema.registerType(true, () -> "no_op", DSL::remainder);
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        return Collections.emptyMap();
    }
}
