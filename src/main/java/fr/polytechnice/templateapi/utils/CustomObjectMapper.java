package fr.polytechnice.templateapi.utils;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

// Custom ObjectMapper with support for ZonedDateTime
public class CustomObjectMapper extends ObjectMapper {

    private static DateTimeFormatter dateTimeFormatterHHMMss;
    private static DateTimeFormatter dateTimeFormatterHH;
    static {
        dateTimeFormatterHHMMss = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .appendOffset("+HHMMss", "Z")
            .optionalStart()
            .appendLiteral('[')
            .parseCaseSensitive()
            .appendZoneRegionId()
            .appendLiteral(']')
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT)
            .withChronology(IsoChronology.INSTANCE);
        dateTimeFormatterHH = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .appendOffset("+HH", "Z")
            .optionalStart()
            .appendLiteral('[')
            .parseCaseSensitive()
            .appendZoneRegionId()
            .appendLiteral(']')
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT)
            .withChronology(IsoChronology.INSTANCE);
    }

    public CustomObjectMapper() {
        registerModule(new Jdk8Module());
        registerModule(new JavaTimeModule()
            // Always serialize with UTC timezone
            .addSerializer(ZonedDateTime.class, new JsonSerializer<ZonedDateTime>() {
                // Convert to utc
                @Override
                public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    ZonedDateTime utcValue = value.withZoneSameInstant(ZoneOffset.UTC);
                    ZonedDateTimeSerializer.INSTANCE.serialize(utcValue, gen, serializers);
                }
            })
            // Deserialize more valid timezone formats
            .addDeserializer(ZonedDateTime.class, new JsonDeserializer<ZonedDateTime>() {
                @Override
                public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
                    if(jsonParser.getCurrentTokenId() == JsonTokenId.ID_STRING) {
                        String text = jsonParser.getText().trim();
                        ZonedDateTime zonedDateTime = parseZonedDateTime(text);
                        if (zonedDateTime == null)
                            throw new JsonParseException(jsonParser, String.format("Cannot deserialize value of type `%s` from String \"%s\"", ZonedDateTime.class.getName(), text));
                        return zonedDateTime;
                    } else {
                        return InstantDeserializer.ZONED_DATE_TIME.deserialize(jsonParser, deserializationContext);
                    }
                }
            })
        );
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
    }

    private static ZonedDateTime parseZonedDateTime(String time) {
        if(time == null) return null;
        try {
            try {
                return ZonedDateTime.parse(time);
            } catch(DateTimeParseException e) {
                // The default parse expect an offset of the form "+HH:MM:ss"
                // But the form "+HHMMss" is also a valid form.
                try {
                    return ZonedDateTime.parse(time, dateTimeFormatterHHMMss);
                } catch (DateTimeParseException e2) {
                    // So is "+HH"
                    return ZonedDateTime.parse(time, dateTimeFormatterHH);
                }
            }
        } catch(RuntimeException e) {
            return null;
        }
    }
}
