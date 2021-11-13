package com.robotgryphon.dfu.tests.schema;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

// You must define at least one schema, to use as a base.
// The schema is used to hold information on types and templates for data storage.
public class BasicSchema extends Schema {
    // A type reference is used seemingly to reference a "known" type on objects
    // For reference, see block names and item stacks in Minecraft DFU code
    public static final DSL.TypeReference DATA = () -> "data";

    public BasicSchema(int version, Schema parent) {
        super(version, parent);
    }

    @Override
    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entities, Map<String, Supplier<TypeTemplate>> blocks) {
        // Here we register a simple type, "data", that's a field on our schema that can appear anywhere on any object
        schema.registerType(false, DATA, () -> {
            return DSL.field("data", DSL.constType(DSL.string()));
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
