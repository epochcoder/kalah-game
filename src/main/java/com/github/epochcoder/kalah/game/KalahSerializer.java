package com.github.epochcoder.kalah.game;

import com.google.common.base.Preconditions;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 * serializes an instance of Kalah
 * @author Willie Scholtz
 */
public final class KalahSerializer {

    /**
     * the GSON serializer that will serialize the game
     */
    private static final Gson JSON_SERIALIZER = new GsonBuilder()
            .registerTypeAdapter(Kalah.class, new CustomKalahSerializer())
            .serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .create();

    private KalahSerializer() {
        // hidden ctor
    }

    /**
     * serializes an instance of the Kalah game
     * @param game the game to serialize
     * @return a non-null valid JSON String
     */
    public static String serializeGame(final Kalah game) {
        return JSON_SERIALIZER.toJson(Preconditions.checkNotNull(
                game, "cannot serialize a null game!"));
    }

    /**
     * a custom serializer for Kalah.class.
     */
    private static class CustomKalahSerializer implements JsonSerializer<Kalah> {

        @Override
        public JsonElement serialize(Kalah src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject kalah = new JsonObject();
            kalah.add("configuration", context.serialize(src.getConfiguration()));
            kalah.add("currentPlayer", context.serialize(src.getCurrentPlayer().getPlayerId()));
            kalah.add("playerOne", context.serialize(src.getPlayerOne()));
            kalah.add("playerTwo", context.serialize(src.getPlayerTwo()));

            return kalah;
        }
    }
}