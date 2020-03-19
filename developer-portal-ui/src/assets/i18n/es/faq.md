
<div class="centeredText">

# P+F(preguntas mas frequentes)
</div>

<div class="divider">
</div>

# Consentimientos
## ¿Por qué no puedo crear un consentimiento?
La función dentro del Certificado QWAC no contiene la función AIS. En este caso debería recibir un error similar:

```json
{
  "timestamp": 1549441548991,
  "status": 403,
  "error": "Forbidden",
  "message": "You don\"t have access to this resource",
  "path": "/v1/consents"
}
```

Solución: cree un nuevo certificado que contenga la función AIS e inserte este certificado en su solicitud o decodifique su certificado y verifique la qcStatement que debe contener “PSP_AI".

Otra posibilidad es que el atributo "recurringIndicator" dentro de su solicitud esté configurado en "false", mientras que el atributo "frequencyPerDay" no es igual a "1". De forma predeterminada, el "recurringIndicator" debe ser "true", ya que un TPP desea acceder a los datos de la cuenta en el período de tiempo indicado. Sin embargo, cuando el TPP solo quiere mostrar una lista de posibles cuentas para la PSU, basta con un acceso único. En este caso debería recibir el siguiente error:

```json
{
  "tppMessages": [
    {
      "category": "ERROR",
      "code": "FORMAT_ERROR",
      "path": null,
      "text": "Format of certain request fields are not matching the XS2A requirements."
    }
  ],
  "_links": null
}
```

Solución: Si desea acceder a los datos de la cuenta con este consentimiento solo una vez, cambie el valor de "frequencyPerDay" a "1". De lo contrario, asigne "true" al “"recurringIndicator".

## ¿Por qué no puedo acceder a los datos de mi cuenta?
Es posible que el consentimiento emitido no permita esta operación. Un AIS-Consent define tres niveles de acceso. El primer nivel permite el acceso a "accounts", el segundo a "balances" y el tercero a “transactions". Hay algunas posibles combinaciones y una selección de "balances" o "transactions" también otorga acceso a "accounts". Un consentimiento que otorga acceso a las cuentas y transacciones no permite llamar al punto final de saldo.

Otra razón podría ser que el estado de consentimiento no es válido. Un estado será, por ejemplo, expira automáticamente cuando se excede la "expirationDate" definida en el consentimiento. También podría ser posible que se haya producido un error al realizar SCA. Para obtener información más detallada, consulte las P+F sobre autenticación fuerte de clientes(Strong Customer Authentication)

<div class="divider">
</div>

# Strong Customer Authentication (Autenticación de cliente fuerte)
## ¿Cómo puedo cambiar el estado de la transacción / consentimiento?
Por defecto, una nueva transacción / consentimiento creado tiene el estado "received" . Para realizar SCA utilizando el enfoque REDIRECT, el sandbox proporciona un Redirect Server donde el estado se actualiza según la PSU. Como SCA para REDIRECT está simplificado para este entorno limitado, es suficiente pasar un PSU-ID a través de Query-Parameter para simular todo el SCA.

El Query Parameter psu-id es obligatorio. Si no se proporciona, el servidor de redirección mostrará un mensaje de error predeterminado.

## ¿Por qué no puedo cambiar el estado de la transacción / consentimiento con una PSU específica?
Si el estado de la transacción / consentimiento es “recieved", es posible que el PSU-ID no coincida con el IBAN en el inicio de pago o la solicitud de creación de consentimiento. La asignación entre PSU-ID y IBAN (s) se documenta en el portal del desarrollador. Si el estado no cambia a pesar de que se realizó SCA y el IBAN coincide con la ID de la PSU, verifique la ID de la PSU para detectar errores tipográficos y la sensibilidad de las mayúsculas y minúsculas.

<div class="divider">
</div>

# Certificados
## ¿Cómo puedo crear un certificado válido?
Para acceder a la XS2A API, un TPP debe tener un Qualified Website Authentication Certificate (QWAC, por sus siglas en inglés) válido que generalmente es emitido por un registrado Trust Service Provider. Para el uso de este sandbox, puede emitir certificados de prueba aquí: . Obtendrá un certificado autofirmado (.pem) y una clave privada correspondiente (.key) incrustadas en un archivo .zip

## ¿Cómo puedo incrustar un certificado en una solicitud?
Después de recibir un QWAC, debe integrarlo a su solicitud. Hay muchas herramientas para realizar llamadas REST. Explicamos la configuración con dos de los más comunes: cURL y Postman. cURL: agregue el archivo .pem usando --cert ./certificate.p y la clave privada usando --key ./private.key. Tenga en cuenta que es posible que deba adaptar la ruta relativa a sus archivos. Postman: vaya a "Preferences > Certificates > Add Certificate" y configure la URL del host (en nuestros ejemplos, esto sería ). Tenga en cuenta que "https://" así como el puerto 443 ya están configurados. Luego agrega tu archivo .pem y .key La frase de contraseña debe dejarse vacía.

## ¿Por qué mi certificado QWAC no funciona?
Es posible que las solicitudes aún no funcionen aunque se agregue un certificado. Esto sucede cuando los certificados expiran. Cada certificado tiene un atributo "validUntil" con la fecha de vencimiento. En este caso debería recibir el siguiente error:

```json
{
  "tppMessages": [
    {
      "category": "ERROR",
      "code": "CONSENT_EXPIRED",
      "path": null,
      "text": "The consent was created by this TPP but has expired and needs to be renewed"
    }
  ],
  "_links": null
}
```

Necesita la PSU para crear un nuevo consentimiento y utilizar el nuevo ID de consentimiento. Otra posibilidad es que el certificado no contenga la función que necesita para su solicitud. P.ej. tener el rol "PIS" no le permite crear consentimientos. En tal caso, debería recibir el siguiente error:

```json
{
  "tppMessages": [
    {
      "category": "ERROR",
      "code": "CONSENT_INVALID",
      "path": null,
      "text": "The consent was created by this TPP but is not valid for the addressed service/resource"
    }
  ],
  "_links": null
}
```
