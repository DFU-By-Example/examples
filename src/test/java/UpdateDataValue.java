import java.util.function.Function;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.JsonOps;
import com.mojang.datafixers.util.Pair;

class UpdateDataValue extends DataFix {

    private final String newValue;

    public UpdateDataValue(Schema outputSchema, String newValue) {
        super(outputSchema, false);
        this.newValue = newValue;
    }

    @Override
    protected TypeRewriteRule makeRule() {
        final var inType = getInputSchema().getType(BasicSchema.DATA);
        final var outType = getOutputSchema().getType(BasicSchema.DATA);

        final var finder = DSL.fieldFinder(BasicSchema.DATA.typeName(), DSL.string());
        return fixTypeEverywhereTyped("UpdateDataValue: " + newValue, inType, outType, (a)
                -> a.update(finder, DSL.string(), old -> newValue));
    }
}
