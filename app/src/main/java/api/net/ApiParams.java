package api.net;

import com.squareup.okhttp.MediaType;

/**
 * Created by clownqiang on 15/7/21.
 */
public class ApiParams {

    private static final MediaType TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType TYPE_XML = MediaType.parse("application/xml; charset=utf-8");
    private static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
    private static final MediaType MULTIPART_FORM_DATA = MediaType.parse("multipart/form-data");
    public static final int LIMIT = 20;
}
