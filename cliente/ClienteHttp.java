/**
* Computación distribuida
* José Fernando Brigido Pablo
*/

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;


public class ClienteHttp {

    public static DataExchange data = new DataExchange();
    public static Gson gson = new Gson();

    private String URL = "http://localhost:8000/api?op=";

    // one instance, reuse
    private final HttpClient httpClient = HttpClient.newBuilder()
                                          .version(HttpClient.Version.HTTP_2)
                                          .build();

    public String getMyRegisters() throws Exception {
        return getData("myreg");
    }

    public String getGlobalRegisters() throws Exception {
        return getData("all");
    }

    public String getMyData() throws Exception {
        return getData("mydata");
    }

    public String getData(String op) throws Exception {
        String params = "&user="+data.user+"&"+"hash="+data.hash;
        HttpRequest request = HttpRequest.newBuilder()
                              .header("Accept", "application/json")
                              .header("Content-Type", "application/json")
                              .header("Cookie", data.token)
                              .GET()
                              .uri(URI.create(URL+op+params))
                              .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        DataExchange dataResponse = gson.fromJson(response.body(), DataExchange.class);
        data.message = dataResponse.message;

        return data.message;
    }

    public String addUser(String[] values, int edad) throws Exception {
        DataExchange newData = new DataExchange();
        newData.user = values[0];
        newData.hash = values[1];
        newData.residencia = values[2];
        newData.sexo =  values[3];
        newData.edad = edad;

        return sendPost("reg", gson.toJson(newData), "");
    }

    public String addRegister(String[] values) throws Exception{
        data.distancia = values[0];
        data.tiempo =  values[1];
        data.tipo = values[2];
        data.fecha = values[3];

        return sendPost("addreg", gson.toJson(data), data.token);
    }

    public String sendPost(String op, String json, String token) throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                              .header("Accept", "application/json")
                              .header("Content-Type", "application/json")
                              .header("Cookie", token)
                              .POST(HttpRequest.BodyPublishers.ofString(json))
                              .uri(URI.create(URL+op))
                              .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        DataExchange dataResponse = gson.fromJson(response.body(), DataExchange.class);
        this.data.message = dataResponse.message;

        return this.data.message;
    }

    public boolean restablecerSesion() throws Exception{
        String op = "rep";
        HttpRequest request = HttpRequest.newBuilder()
                              .header("Accept", "application/json")
                              .header("Content-Type", "application/json")
                              .header("Cookie", data.token)
                              .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(data)))
                              .uri(URI.create(URL+op))
                              .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        DataExchange dataResponse = gson.fromJson(response.body(), DataExchange.class);
        data.message = dataResponse.message;
        int code = dataResponse.codigo;

        if (code == 200)
            data.token = dataResponse.token;

        System.out.println(data.message);

        return code == 200;
    }

    public boolean checkUser(String user, String hash) throws Exception {
        DataExchange newData = new DataExchange();
        newData.user = user;
        newData.hash = hash;

        String op = "aut";
        String params = "&user="+user+"&hash="+hash;

        HttpRequest request = HttpRequest.newBuilder()
                              .header("Accept", "application/json")
                              .header("Content-Type", "application/json")
                              .header("Cookie", "")
                              .GET()
                              .uri(URI.create(URL+op+params))
                              .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        DataExchange dataResponse = gson.fromJson(response.body(), DataExchange.class);
        int code = response.statusCode();

        if (code == 200){
            data = dataResponse;
        }

        data.message = dataResponse.message;
        System.out.println(data.message);

        return code == 200;
    }

}
