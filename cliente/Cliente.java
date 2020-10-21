/**
* Computación distribuida
* José Fernando Brigido Pablo
*/

import java.util.Scanner;
import com.google.gson.Gson;

public class Cliente{
    private static ClienteHttp clienteHttp = new ClienteHttp();

    public static void main(String[] args) {
        Cliente client = new Cliente();
        try{
            client.init();
        }catch (Exception e){}
    }

    public void init() throws Exception {
        System.out.println("Selecciona una opción.");
        menuInicio();
        Scanner scan = new Scanner(System.in);
        int option = Integer.parseInt(scan.nextLine());

        while(!validOption(option, 0, 3)){
            System.out.println("Selección inválida.");
            option = scan.nextInt();
        }

        String message = "";
        if (option == 0){
            menuAutenticarse();
        }else if (option == 1){
            if (clienteHttp.restablecerSesion()){
                menuPrincipal();
            }else {
                init();
            }

        }else if (option == 2){
            menuAddUser();
            init();
        }else {
            System.exit(1);
        }
    }

    public void menuAddUser() throws Exception{
        Scanner scan = new Scanner(System.in);
        String[] values = new String[4];
        System.out.println("Ingresa los siguientes datos:\n");
        System.out.println("Ingresa un nombre de usuario:\n");
        values[0] = scan.nextLine();
        System.out.println("Ingresa tu contraseña:\n");
        values[1] = scan.nextLine();
        System.out.println("Ingresa tu estado de residencia\n");
        values[2] = scan.nextLine();
        System.out.println("Ingresa tu sexo\n");
        values[3] = scan.nextLine();
        System.out.println("Ingresa edad\n");
        int edad = scan.nextInt();

        String message = clienteHttp.addUser(values, edad);
        System.out.println(message);
    }

    public void menuAutenticarse() throws Exception{
        Scanner scan = new Scanner(System.in);
        System.out.println("Ingresa un nombre de usuario:\n");
        String user = scan.nextLine();
        System.out.println("Ingresa tu contraseña:\n");
        String hash = scan.nextLine();

        if (clienteHttp.checkUser(user, hash)){
            menuPrincipal();
        }else{
            init();
        }
    }

    public void menuPrincipal() throws Exception {
        Scanner scan = new Scanner(System.in);
        System.out.println("Selecciona una opción.");
        menuUsuario();
        int option = scan.nextInt();
        while(!validOption(option, 0, 4)){
            System.out.println("Selección inválida.");
            option = scan.nextInt();
        }

        String message = "";
        switch(option){
            case 0:
                    message = clienteHttp.getMyRegisters();
                    break;
            case 1:
                    message = clienteHttp.getGlobalRegisters();
                    break;
            case 2:
                    String[] values = menuAddRegister();
                    message =  clienteHttp.addRegister(values);
                    break;
            case 3:
                    message = clienteHttp.getMyData();
                    break;
            case 4:
                    init();
                    break;
        }
        System.out.println(message);
        menuPrincipal();
    }

    public String[] menuAddRegister(){
        Scanner scan = new Scanner(System.in);
        String[] values = new String[4];
        System.out.println("Ingresa los siguientes datos:\n");
        System.out.println("Ingresa la distancia del recorrido en km:\n");
        values[0] = scan.nextLine();
        System.out.println("Ingresa el tiempo del recorrido en el formato hh:mm:ss\n");
        values[1] = scan.nextLine();
        System.out.println("Ingresa el tipo de carrera\n");
        values[2] = scan.nextLine();
        System.out.println("Ingresa la fecha de la carrera en el formato aaaa-mm-dd\n");
        values[3] = scan.nextLine();
        return values;
    }

    public boolean validOption(int option, int bottom, int top){
        if (option < bottom || option > top)
            return false;
        return true;
    }

    public void menuInicio(){
        String menu = "0. Autenticarse\n"+
                      "1. Volver a la sesión\n"+
                      "2. Registrarse\n"+
                      "3. Salir";
        System.out.println(menu);
    }

    public void menuUsuario(){
        String menu = "0. Bitácora\n"+
                      "1. Registros globales\n"+
                      "2. Agregar un registro a tu bitácora\n"+
                      "3. Información personal\n"+
                      "4. Salir";
        System.out.println(menu);
    }

}
