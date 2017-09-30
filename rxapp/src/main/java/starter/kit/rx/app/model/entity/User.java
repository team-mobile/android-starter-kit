package starter.kit.rx.app.model.entity;

import android.net.Uri;
import android.text.TextUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.parceler.Parcel;
import starter.kit.model.entity.DefaultEntity;

@Parcel
@JsonIgnoreProperties(ignoreUnknown = false)
public class User extends DefaultEntity {
  public String phone;
  public String nickname;
  public String avatar;
  public String token;

  public Uri uri() {
    if (TextUtils.isEmpty(avatar)) return null;

    if (avatar.startsWith("http://")) {
      return Uri.parse(avatar);
    }

    return null;
  }

  @Override
  public String toString() {
    return "User{" +
            "phone='" + phone + '\'' +
            ", nickname='" + nickname + '\'' +
            ", avatar='" + avatar + '\'' +
            ", token='" + token + '\'' +
            '}';
  }
}
