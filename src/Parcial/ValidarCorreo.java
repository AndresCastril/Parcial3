
package Parcial;


public class ValidarCorreo {
     public void validar(String correo) throws ExcepcionCorreo{
        if (!correo.contains("@") || !correo.endsWith(".com")) {
            throw new ExcepcionCorreo("El correo no es valido. Debe contener '@' y terminar en '.com'.");
        }
        System.out.println("Correo escrito de manera correcta");
    }
    
}
