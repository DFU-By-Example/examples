package com.robotgryphon.dfu.tests.schema;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

public class ComplexSchemaV2 extends Schema {

    public static final DSL.TypeReference FILE = () -> "all";
    public static final DSL.TypeReference DATA_NODE = () -> "data";

    public ComplexSchemaV2(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Override
    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {

        /**
         * version: 1,
         * data:
         *   version: 2
         *   type: "hi"
         *   id: "there"
         */
        schema.registerType(false, FILE, () -> {
            return DSL.fields(
                    "version", DSL.constType(DSL.intType()),
                    "data", DATA_NODE.in(schema)
            );
        });

        schema.registerType(false, DATA_NODE, () -> {
            return DSL.fields(
                    "version", DSL.constType(DSL.intType()),
                    "type", DSL.constType(DSL.string()),
                    "id", DSL.constType(DSL.string())
            );
        });

        // super constructor requires at least one recursive type, issue with Schema#50 (RECURSIVE_TYPES)
        schema.registerType(true, () -> "no_op", DSL::remainder);
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        // Even if not used here, these MUST be defined because the method is inlined in the constructor of Schema
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        // Even if not used here, these MUST be defined because the method is inlined in the constructor of Schema
        return Collections.emptyMap();
    }
}
