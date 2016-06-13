package rubiconproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.util.Objects;

public class Site {

    @JsonProperty("site_id")
    private int id;
    private String name;
    @JsonSerialize(using = BooleanNumericSerializer.class)
    @JsonDeserialize(using = BooleanNumericDeserializer.class)
    private boolean mobile;
    private String keywords;
    private int score;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public static class BooleanNumericDeserializer extends JsonDeserializer<Boolean> {

        @Override
        public Boolean deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
            return !jp.getText().equals("0");
        }

    }

    public static class BooleanNumericSerializer extends JsonSerializer<Boolean> {

        @Override
        public void serialize(Boolean boolValue, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
            jg.writeString(boolValue ? "1" : "0");
        }

    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.id;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + (this.mobile ? 1 : 0);
        hash = 29 * hash + Objects.hashCode(this.keywords);
        hash = 29 * hash + this.score;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Site other = (Site) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.mobile != other.mobile) {
            return false;
        }
        if (!Objects.equals(this.keywords, other.keywords)) {
            return false;
        }
        if (this.score != other.score) {
            return false;
        }
        return true;
    }


}
