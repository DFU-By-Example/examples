import java.util.function.Function;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.JsonOps;
import com.mojang.datafixers.util.Pair;

/**
 * This is a simplified update fix that changes a data field to a hard-coded value.
 * You can obviously change this to do a lookup or mapping, but we're keeping it simple here.
 */
class UpdateDataValue extends DataFix {

    private final String newValue;

    /**
     * UpdateDataValue - updates the data field to a new value on traversal.
     *
     * @param outputSchema The schema the fix will end on.
     * @param newValue The new value of the data field, after update.
     */
    public UpdateDataValue(Schema outputSchema, String newValue) {
        super(outputSchema, false);
        this.newValue = newValue;
    }

    @Override
    protected TypeRewriteRule makeRule() {
        // This was copied from several sources. Here, we reference the DATA reference on both in and output schemas.
        final var inType = getInputSchema().getType(BasicSchema.DATA);
        final var outType = getOutputSchema().getType(BasicSchema.DATA);

        /*
         * An OpticFinder appears to be a means of finding data in a complex type.
         * Here, we use it to locate and modify our data field.
        */
        final var finder = DSL.fieldFinder(BasicSchema.DATA.typeName(), DSL.string());

        /*
         * This is still needing more research. FTET needs a name so it can perform equality comparison,
         * but overall this is how updates are actually applied, and how updates calculate a set of rules to apply.
         *
         * #Typed takes in the input type and output type, then applies an update function.
         * The DSL.string() is the resulting data type, and the lambda here is a simplified mapping function.
         */
        return fixTypeEverywhereTyped("UpdateDataValue: " + newValue, inType, outType, (a)
                -> a.update(finder, DSL.string(), old -> newValue));
    }
}
