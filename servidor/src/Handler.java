/**
* Computación distribuida
* José Fernando Brigido Pablo
*/

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;

import java.net.URLDecoder;
import com.google.gson.Gson;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import io.fusionauth.jwt.JWTExpiredException;
import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACSigner;
import io.fusionauth.jwt.hmac.HMACVerifier;

public class Handler implements HttpHandler {

    public static MySQL dao = new MySQL();
    public static Gson gson = new Gson();

	@Override
    public void handle(HttpExchange t) {
        dao.connect();

    	try {

            Map<String, String> params = queryToMap(t.getRequestURI().getQuery());
            String tipo = params.get("op");
            if(tipo == null)
                tipo = "na";


            DataExchange data = requestToData(t);

            if (params.get("user") != null && params.get("hash") != null){
                data.user = params.get("user");
                data.hash = params.get("hash");
            }

            //Primero se verifica las operaciones de autenticación y registro
            //ya que no se necesita verificación de token para realizarse.

            if (tipo.equals("aut")){

                if(data.user.isEmpty() || data.hash.isEmpty()){
                    data.codigo = 404;
                    data.message = "Faltan parametros.";
                } else if (!dao.searchUser(data)){
                    data.codigo = 401;
                    data.message = "Clave incorrecta o usuario no registrado.";
                } else {
                    data.token = obtenToken(data.user);
                    data.message =  "Atenticación exitosa";
                }

            }else if (tipo.equals("reg")){

                if (!verificaDataUser(data)){
                    data.codigo = 404;
                    data.message = "Faltan parámetros";
                }else{
                    data.message = dao.addUser(data);
                }

            }else if (data.token.isEmpty()){
                data.codigo = 401;
                data.message = "No autorizado. Inicia sesión o registrate :)";

            }else if (tipo.equals("rep")){
                if (!verificaToken(data.token)){
                    data.codigo = 401;
                    data.message = "No autorizado. Inicia sesión o registrate :)";
                }else{
                    data.message = "Bienvenido de nuevo :)";
                }
            }else if (!verificaToken(data.token)){
                data.codigo = 401;
                data.message = "Tu sesión ha expirado :(";
            }else {
                data = executeOperation(tipo, data);
            }

            String respuesta = gson.toJson(data);

            byte[] bs = respuesta.getBytes("UTF-8");
            t.sendResponseHeaders(data.codigo, bs.length);
            OutputStream os = t.getResponseBody();
            os.write(bs);
            os.close();

    	} catch(IOException e){
    		System.out.println(e);
    	} catch(Exception k){
    		System.out.println(k);
    	}
   	}

    public DataExchange requestToData(HttpExchange t) throws Exception{
        InputStreamReader reader = new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader buff = new BufferedReader(reader);
        StringBuilder builder = new StringBuilder();

        int c;
        while((c = buff.read()) != -1)
            builder.append((char) c);

        buff.close();
        reader.close();
        String body = builder.toString();


        DataExchange data = gson.fromJson(body, DataExchange.class);
        if (data == null){
            data = new DataExchange();
        }

        data.token = t.getRequestHeaders().get("Cookie").get(0);
        data.codigo = 200;
        return data;
    }

    public DataExchange executeOperation(String op, DataExchange data){
    //se identifica el tipo de operacion y se arma la respuesta
        data.codigo = 200;
        switch (op){
            case "all":
                        String registros = dao.getGlobalRegister();
                        if (registros.isEmpty()){
                            data.message = "No hay registros de ningún usuario :(";
                        } else {
                            data.message = "Registros de todos los corredores:\n";
                            data.message += registros;
                        }
                        break;

            case "myreg":
                        if (data.user.isEmpty() || data.hash.isEmpty()){
                            data.message = "Faltan parámetros";
                            data.codigo = 404;
                        }else{
                            data.message = "Estos son tus registros actuales:\n";
                            data.message += dao.getUserRegister(data);
                        }
                        break;

            case "addreg":
                        if (!verificaRegUser(data)){
                            data.codigo = 404;
                            data.message = "Faltan parámetros";
                        } else {
                            data.message = dao.addUserRegister(data);
                        }
                        break;

            case "mydata":
                        if (data.user.isEmpty() || data.hash.isEmpty()){
                            data.message = "Faltan parámetros";
                            data.codigo = 404;
                        }else{
                            data.message += dao.getUserData(data.user, data.hash);
                        }
                        break;
            default:
                        data.codigo = 404;
                        data.message = "Operación inválida";
        }
        return data;
    }

   	public Map<String, String> queryToMap(String query) {
        if(query == null){
            return null;
        }
        if(query.length() == 0){
            return null;
        }
    	Map<String, String> result = new HashMap<>();
    	for (String param : query.split("&")) {
        	String[] entry = param.split("=");
        	if (entry.length > 1) {
            	result.put(entry[0], entry[1]);
        	}else{
            	result.put(entry[0], "");
        	}
    	}
    	return result;
	}

    private String obtenToken(String usuario){
        Signer signer = HMACSigner.newSHA256Signer("secreto");
        JWT jwt = new JWT().setIssuer("localhost")
                           .setIssuedAt(ZonedDateTime.now(ZoneOffset.UTC))
                           .setSubject(usuario)
                           .setExpiration(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(1));
        String encodedJWT = JWT.getEncoder().encode(jwt, signer);
        return encodedJWT;
    }

    private boolean verificaToken(String token){
        try{
            Verifier verifier = HMACVerifier.newVerifier("secreto");
            JWT jwt2 = JWT.getDecoder().decode(token, verifier);
        }catch(Exception e){
            return false;
        }
        return true;
    }

    public boolean verificaDataUser(DataExchange data){
        if (data.user.isEmpty() || data.hash.isEmpty() ||
            data.sexo.isEmpty() || data.residencia.isEmpty() ||
            data.edad == -1)
            return false;
        return true;
    }

    public boolean verificaRegUser(DataExchange data){
        if (data.user.isEmpty() || data.hash.isEmpty() ||
            data.distancia.isEmpty() || data.tiempo.isEmpty() ||
            data.tipo.isEmpty() || data.fecha.isEmpty())
            return false;
        return true;
    }

}
