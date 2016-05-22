package api.impl;


import java.io.IOException;

import api.net.HttpClient;
import api.utils.ResponseParse;
import model.response.ApiResponse;


/**
 * Created by clownqiang on 15/9/10.
 */
public class ApiImpl extends BaseApiImpl {

    ResponseParse paser = new ResponseParse();

    public ApiResponse getSongList(String url) throws IOException {
        return paser.parse(HttpClient.get(url, false));
    }

    public ApiResponse downloadSong(String url, String filename) throws IOException {
        return paser.decodeFile(HttpClient.download(url), filename);
    }
}
