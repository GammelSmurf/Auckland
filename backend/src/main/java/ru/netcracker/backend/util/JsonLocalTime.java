package ru.netcracker.backend.util;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@Documented
@JsonSerialize(using = LocalTimeSerializer.class)
@JsonDeserialize(using = LocalTimeDeserializer.class)
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
public @interface JsonLocalTime {
}
