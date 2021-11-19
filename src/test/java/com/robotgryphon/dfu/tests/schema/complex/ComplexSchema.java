package com.robotgryphon.dfu.tests.schema.complex;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

public class ComplexSchema extends Schema {

    public static final DSL.TypeReference FILE = () -> "all";
    public static final DSL.TypeReference DATA_NODE = () -> "data";

    public ComplexSchema(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Override
    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
        // super constructor requires at least one recursive type, issue with Schema#50 (RECURSIVE_TYPES)

        schema.registerType(true, FILE, () -> {
            return DSL.fields(
                    "version", DSL.constType(DSL.intType()),
                    "data", DATA_NODE.in(schema)
            );
        });

        schema.registerType(false, DATA_NODE, () -> {
            return DSL.fields(
                    "type", DSL.constType(DSL.string()),
                    "id", DSL.constType(DSL.string())
            );
        });

//        schema.registerType(true, TypeReferences.ITEM_STACK, () -> {
//            return DSL.hook(DSL.optionalFields(
//                    "id", DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(schema)),
//                    "tag", DSL.optionalFields(
//                            "EntityTag", TypeReferences.ENTITY_TREE.in(schema),
//                            "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(schema),
//                            "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(schema)),
//                            "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(schema)))
//                    ),
//                    ADD_NAMES, Hook.HookFunction.IDENTITY);
//        });

        schema.registerType(true, () -> "no_op", DSL::remainder);
    }

    // NamedType[
    //      "all", (
    //          Tag["version", Int], (
    //              Tag["data", NamedType["data", (
    //                  Tag["type", String], Tag["id", String]
//                  )]], NilSave
    //          )
//          )
    // ]

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
