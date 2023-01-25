package com.robotgrpyphon.codec.tests;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.KeyDispatchCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CodecFallbackTests {

    public interface ICatalystMatcher {
        CatalystType<?> getType();
    }

    public interface CatalystType<T> {
        Codec<T> codec();
    }

    private record FakeItemStack(String id, int count) {
    }

    static final Codec<ICatalystMatcher> ITEM_CODEC = Codec.unit(new ICatalystMatcher() {
        @Override
        public CatalystType<?> getType() {
            return (CatalystType<ICatalystMatcher>) () -> ITEM_CODEC;
        }
    });

    private static final Codec<FakeItemStack> FAKE_ITEM_STACK_CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("id").forGetter(FakeItemStack::id),
            Codec.INT.optionalFieldOf("Count", 1).forGetter(FakeItemStack::count)
    ).apply(i, FakeItemStack::new));

    @Test
    void doesFallbackToValue() {
        JsonObject example = new JsonObject();
        example.addProperty("id", "minecraft:stick");
        example.addProperty("Count", 1);

        final var itemStack = FAKE_ITEM_STACK_CODEC.parse(JsonOps.INSTANCE, example)
                .getOrThrow(false, e -> {
                });

        Assertions.assertNotNull(itemStack);
        Assertions.assertEquals("minecraft:stick", itemStack.id);

        // Goal: Decode the above, so it uses the fake ItemStack codec above, rather than a type dispatch

        final var dispatch = new KeyDispatchCodec<CatalystType<?>, ICatalystMatcher>("type", Codec.STRING, k -> {
            return DataResult.success("cc:item");
        }, catType -> {
            if(catType instanceof )
            case "cc:item" -> DataResult.success(ITEM_CODEC);
            default -> DataResult.error("not_found");
        });

        final var decoder = new Decoder<CatalystType<?>>() {
            @Override
            public <T> DataResult<Pair<CatalystType<?>, T>> decode(DynamicOps<T> ops, T input) {
                final var typeCodec = Codec.optionalField("type", Codec.STRING);
                final var catalystTypeKey = typeCodec.decoder().parse(JsonOps.INSTANCE, example)
                        .getOrThrow(false, e -> {
                        });

                final var catalystType = ops.get(catalystNode.get(), "type").result();
                if (catalystType.isEmpty()) {
                    if (debugOutput)
                        CompactCrafting.LOGGER.warn("Error: no catalyst type defined; falling back to the itemstack handler.");

                    final ItemStack stackData = ItemStack.CODEC
                            .fieldOf("catalyst").codec()
                            .parse(ops, input)
                            .resultOrPartial(errorBuilder::append)
                            .orElse(ItemStack.EMPTY);

                    return new ItemStackCatalystMatcher(stackData));
                } else {
                    ICatalystMatcher catalyst = CatalystMatcherCodec.MATCHER_CODEC
                            .fieldOf("catalyst")
                            .codec()
                            .parse(ops, input)
                            .resultOrPartial(errorBuilder::append)
                            .orElse(new ItemStackCatalystMatcher(ItemStack.EMPTY));

                    // ICatalystMatcher catalyst = new ItemTagCatalystMatcher(ItemTags.PLANKS);
                    recipe.setCatalyst(catalyst);
                }
            }
        };



    }
}
