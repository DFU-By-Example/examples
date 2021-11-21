package com.robotgryphon.dfu.tests.fixes;

import com.mojang.datafixers.*;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.robotgryphon.dfu.tests.schema.ComplexSchema;

public class UpdateDataNodeV2 extends DataFix {
    public UpdateDataNodeV2(Schema outputSchema) {
        super(outputSchema, true);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        final Schema input = getInputSchema();
        final Schema output = getOutputSchema();

        final Type<?> inData = input.getType(ComplexSchema.DATA_NODE);
        final Type<?> outData = output.getType(ComplexSchema.DATA_NODE);

        var addVersionField = writeFixAndRead("addVersion", inData, outData, data -> {
            return data.set("version", data.createInt(0));
        });

        var rewriteV1Data = fixTypeEverywhereTyped("updateToV2", outData, outData, this::updateV2Data);

        return TypeRewriteRule.seq(addVersionField, rewriteV1Data);
    }

    private Typed<?> updateV2Data(Typed<?> type) {
        final OpticFinder<String> typeFinder = DSL.fieldFinder("type", DSL.string());
        final OpticFinder<Integer> verFinder = DSL.fieldFinder("version", DSL.intType());

        type = type.update(typeFinder, (oldType) -> oldType + "_v2");
        type = type.set(verFinder, 2);
        return type;
    }
}
