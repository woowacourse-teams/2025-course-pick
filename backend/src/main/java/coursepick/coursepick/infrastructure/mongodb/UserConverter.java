package coursepick.coursepick.infrastructure.mongodb;

import coursepick.coursepick.domain.user.Nickname;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserProvider;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public abstract class UserConverter {

    @WritingConverter
    public static class Writer implements Converter<User, Document> {

        @Override
        public Document convert(User source) {
            Document document = new Document();
            if (source.id() != null && !source.id().isBlank()) {
                document.put("_id", source.id());
            }
            document.put("provider", source.provider().name());
            document.put("providerId", source.providerId());
            document.put("nickname", source.nickname().value());
            return document;
        }
    }

    @ReadingConverter
    public static class Reader implements Converter<Document, User> {

        @Override
        public User convert(Document source) {
            String nicknameValue = source.getString("nickname");
            return new User(
                    source.getObjectId("_id").toHexString(),
                    UserProvider.valueOf(source.getString("provider")),
                    source.getString("providerId"),
                    nicknameValue == null ? null : new Nickname(nicknameValue) // 마이그레이션 이후 제거
            );
        }
    }
}
