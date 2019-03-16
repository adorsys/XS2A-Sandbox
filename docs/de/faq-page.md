# Frequently Asked Questions

---

## Consents

**Warum kann ich keinen Consent anlegen?**\
Die Rolle in Ihrem QWAC Zertifikat beinhaltet nicht die benötigte AIS-Rolle.
In diesem Fall sollte Ihnen folgender Fehler angezeigt werden:

```json
{
  "timestamp": 1549441548991,
  "status": 403,
  "error": "Forbidden",
  "message": "You don't have access to this resource",
  "path": "/v1/consents"
}
```

Lösung: Entweder Sie erstellen sich mittels des Zertifikatsservices ein neues Zertifikat mit der benötigten AIS-Rolle und integrieren es anschließend in Ihren Request oder Sie entschlüsseln das Zertifikat und prüfen, ob "PSP_AI" beinhaltet ist.

Eine andere Fehlerursache könnte sein, dass in Ihrem Request das Attribut "recurringIndicator" auf "false" und das Attribut "frequencyPerDay" ungleich "1" gesetzt ist. Standardmäßig ist der "recurringIndicator" auf "true" gesetzt, da Drittanbieter die Kontodaten in der angegebenen Zeit abfragen wollen.
Wenn der Drittanbieter nur eine Liste möglicher Konten für den Nutzer (PSU = Payment Service User) anzeigen möchte, ist es ausreichend nur einmalig Zugang zu gewähren.
In diesem Fall sollte folgende Fehlermeldung auftreten:

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

Lösung: Wenn Sie nur einmalig auf die Account Daten zugreifen möchten, setzen Sie "frequencyPerDay" auf den Wert "1". Andernfalls ändern Sie einfach den "recurringIndicator" auf "true".

**Warum kann ich meine Account Daten nicht abfragen?**\
Es kann sein, dass der angelegte Consent diese Abfrage nicht erlaubt. Ein AIS-Consent beinhaltet drei unterschiedliche Zugangsebenen. Auf der ersten Ebene wird der Zugang auf die "Accounts" gewährt, auf der zweiten Ebene der Zugang zu den "Balances" und auf der dritten Ebene erhält man Zugriffsrechte auf die "Transactions".
Verschiedene Kombinationen sind möglich und die Auswahl von "Balances" oder "Transactions" liefert die "Accounts" ebenfalls mit zurück.
Ein Consent, der für "Accounts" angelegt wurde, erlaubt jedoch im Umkehrschluss nicht automatisch die "Balances" mit abzufragen.

In diesem Fall würden Sie folgende Fehlermeldung erhalten:

TODO

Lösung: Erstellen Sie mit dem PSU einen neuen Consent, der es Ihnen erlaubt die gewünschten Endpoints zu benutzen.

Ein anderer Grund könnte sein, dass Ihr Consent Status nicht valide ist. Der Consent Status wird automatisch auf "expired" gesetzt und ist damit ungültig, wenn das Datum im Feld "expirationDate" überschritten wurde.

## Nutzerauthentifizierung (SCA)

**Wie kann ich den Transactions- oder Consentstatus verändern?**\
Nach dem Anlegen haben Transaction- und Consent Status standardmäßig den Wert "received".
Um die Authentifizierung (SCA) mittels Redirect Ansatz abzudecken, stellt die Sandbox einen Redirect Server bereit, der den Status je nach PSU aktualisiert.
Da der Redirect Ansatz für die Sandbox vereinfacht umgesetzt wurde, ist es für eine vollständige SCA-Simulation ausreichend, die PSU-ID als Query-Parameter zu übergeben. Folgendes Beispiel verdeutlicht die Umsetzung:

`GET https://sandbox-api.dev.adorsys.de/v1/online-banking/init/pis/5239763c-8641-41cc-a354-d52d35da0c9e?psu-id=PSU-Successful`

Der Query Parameter ist zwingend notwendig für die Durchführung. Wenn er nicht angefügt wird, zeigt der Redirect Server eine Standard-Fehlermeldung an.

**Warum kann ich den Transactions- oder Consentstatus von einem bestimmten PSU nicht verändern**\
Wenn der Transactions- oder Consentstatus "received" lautet, kann es sein, dass die PSU-ID nicht zur angegebenen IBAN in der Zahlungs- oder Consentanlage passt. Die definierten PSUs mit zugehörigen Testdaten finden Sie im Developer Portal.

Falls sich der Status nicht verändert, obwohl SCA erfolgreich durchgeführt wurde und PSU-ID und IBAN zusammenpassen, überprüfen Sie Ihre Eingabe auf Tippfehler sowie Groß- und Kleinschreibung.

## Zertifikate

**Wie kann ich ein valides Zertifikat generieren?**\
Um auf die XS2A API zugreifen zu können, braucht ein Drittanbieter ein gültiges QWAC Zertifikat, welches zukünftig von registrierten Treuhanddienstleister (TSP = Trust Service Provider) ausgestellt wird.
Um in dieser Sandbox Zugriff zu erhalten, können Sie hier ein Testzertifikat generieren:
[https://sandbox-portal.dev.adorsys.de/app/certificate-service](https://sandbox-portal.dev.adorsys.de/app/certificate-service).
Sie erhalten ein unterschriebenes Zertifikat (.pem) mit zugehörigem Private Key (.key) in einer .zip Datei.

**Wie kann ich das Zertifikat in einen Request einbinden?**\
Wenn Sie ein QWAC Zertifikat erhalten haben, müssen Sie es in Ihren Request integrieren. Es gibt viele Tools mit denen REST Calls ausgeführt werden können. Wir erklären nachfolgend die Durchführung mit zwei weit verbreiteten Tools - cURL und Postman.
cURL: Fügen Sie die .pem Datei mit `--cert ./certificate.p` und den private key mit `--key ./private.key` in Ihren Request ein. Bitte beachten Sie, dass der relative Pfad zu Ihren Dateien gegebenenfalls angepasst werden muss.
Postman: Navigieren Sie zu "Preferences > Certificates > Add Certificate" und setzen Sie die Host url (in unserem Beispiel wäre es
[https://sandbox-api.dev.adorsys.de](https://sandbox-api.dev.adorsys.de)).
Achten Sie darauf, dass "https://" und der Port 443 bereits gesetzt sind. Anschließend fügen Sie Ihre .pem und .key Datei hinzu. Die Passphrase muss dabei leer gelassen werden.

**Warum funktioniert mein QWAC Zertifikat nicht?**\
Es kann sein, dass Requests trotz hinzugefügtem Zertifikat nicht funktionieren. Das passiert, wenn Zertifikate abgelaufen und ungültig werden. Jedes Zertifikat verfügt über ein Attribut
"validUntil", das ein Ablaufdatum als Wert enthält.
Wenn dieser Fall zutrifft, erhalten Sie folgende Fehlermeldung:

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

In diesem Fall müssen Sie für einen PSU einen neuen Consent anlegen und anschließend die neu generierte Consent-Id verwenden.
Eine weitere Möglichkeit wäre, dass Ihr Zertifikat nicht die benötigte Rolle für Ihren Request beinhaltet. Die Rolle "PIS" erlaubt beispielsweise nicht die Anlage von Consents.
In diesem Fall erhalten Sie folgende Fehlermeldung:

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

Lösung: Entweder Sie erstellen sich über den Certificate Service ein neues Zertifikat mit der benötigten Rolle und integrieren es anschließend in Ihren
Request oder Sie entschlüsseln das Zertifikat und prüfen, ob die Rolle "PSP_AI" enthalten ist.
