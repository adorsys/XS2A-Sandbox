<div class="centeredText">

# Häufig gestellte Fragen
</div>

<div class="divider">
</div>

# Einwilligung
## Warum kann ich keine Einwilligung erstellen?
Das QWAC Zertifikat enthält nicht die Rolle AIS. In diesem Fall sollten Sie einen ähnlichen Fehler wie diesen ausgegeben bekommen:

```json
{
  "timestamp": 1549441548991,
  "status": 403,
  "error": "Forbidden",
  "message": "You don\"t have access to this resource",
  "path": "/v1/consents"
}
```

Lösung: Sie können ein neues Zertifikat erstellen, das die Rolle AIS beinhaltet, und es an Ihre Anfrage hängen oder Sie entschlüsseln Ihr Zertifikat und überprüfen ob das qcStatement „PSP_AI“ beinhaltet.

Eine weiter Möglichkeit ist, dass in Ihrer Anfrage das Attribut „recurringIndicator“ den Wert „false“ hat und „frequencyPerDay“ ungleich „1“ ist. Standardmäßig sollte der "recurringIndicator" "true" sein, da eine TPP im angegebenen Zeitraum auf die Kontodaten zugreifen möchte. Wenn der TPP dem PSU jedoch nur eine Liste der möglichen Konten zeigen möchte, genügt ein einmaliger Zugriff. In diesem Fall sollten Sie folgenden Fehler erhalten:

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

Lösung: Wenn Sie einmalig Zugang zu den Kontodaten haben möchten, ändern Sie den Wert von „frequencyPerDay“ auf „1“. Ansonsten setzen Sie „recurringIndicator“ auf „true“.

## Warum habe ich keinen Zugang zu meinen Kontodaten?
Es ist möglich, dass die erteilte Einwilligung diesen Vorgang nicht zulässt. Es gibt 3 verschiedene Level für die AIS-Einwilligung. Das erste Level erlaubt Ihnen Zugang zu den „Konten“, das zweite zu den „Salden“ und das dritte zu „Transaktionen“. Kombinationen der Level sind möglich und die Auswahl von "Salden" oder "Transaktionen" ermöglicht auch den Zugriff auf "Konten". Eine Einwilligung, die Zugang zu z.B. „Konten“ und „Transaktionen“ gewährt, erlaubt es nicht, den Endpunkt bezüglich „Salden“ aufzurufen.

Ein anderer Grund könnte sein, dass der Status der Einwilligung nicht gültig ist. Ein Status verfällt z.B. automatisch, wenn das in der Einwilligung definierte "expirationDate" überschritten wurde. Es ist auch möglich, dass bei der Durchführung von SCA ein Fehler aufgetreten ist. Detaillierte Informationen finden Sie in der FAQ Rubrik "Starke Kundenauthentifizierung".

<div class="divider">
</div>

# Starke Kundenauthentifizierung
## Wie kann ich den Status einer Transaktion/Einwilligung ändern?
Alle erstellten Transaktionen/Einwilligungen haben als Status automatisch "received" . Um SCA mit dem REDIRECT-Ansatz durchzuführen, stellt die Sandbox einen Redirect-Server zur Verfügung, bei dem der Status abhängig von dem PSU aktualisiert wird. Dieser Vorgang ist in unserer Sandbox vereinfacht, daher reicht es aus, die PSU-ID in den Query-Parameter zu übergeben, um den gesamten SCA zu simulieren.

Der Query-Parameter PSU-ID ist ein Pflichtfeld, das wenn es nicht angegeben sein sollte, zu einer Standardfehlermeldung des Redirect Servers führt.

## Warum kann ich den Status einer Transaktion/Einwilligung nicht mit einem bestimmten PSU ändern?
Wenn der Transaktions-/Einwilligungsstatus "received" ist, kann es sein, dass die PSU-ID nicht mit der IBAN in der Zahlungsinitiierungs- oder Einwilligungsanforderung übereinstimmt. Das Mapping zwischen PSU-ID und IBAN(s) ist im Developer-Portal dokumentiert. Wenn sich der Status nicht ändert, obwohl eine SCA Methode durchgeführt wurde und die IBAN mit der PSU-ID übereinstimmt, überprüfen Sie die PSU-ID auf Tippfehler und Groß-/Kleinschreibung.

<div class="divider">
</div>

# Zertifikate
## Wie kann ich ein gültiges Zertifikat erstellen?
Um Zugang zu der XS2A API zu erhalten, benötigt ein TPP ein gültiges QWAC Zertifikat. Solch ein Zertifikat bekommt man üblicherweise bei einem zugelassenen TSP, für unsere Sandbox verwenden wir erstellte Testzertifikate. Ein solches Zertifikat kann hier: erstellt werden oder direkt bei der Registrierung eines TPPs. So erhalten Sie ein für die Sandbox authentifiziertes Zertifikat (.pem) und den zugehörigen Privatschlüssel (.key) in einer .zip Datei.

## Wie kann ich ein Zertifikat in einer Anfrage integrieren?
Sobald Sie einen QWAC Zertifikat erhalten haben, müssen Sie es in Ihrer Anfrage einbinden. Es gibt zahlreiche Tools, um REST-Aufrufe durchzuführen. Wir erklären die Konfiguration mit den zwei gebräuchlichsten - cURL und Postman. cURL: Fügen Sie die .pem mit --cert ./certificate.p und den Privatschlüssel mit --key ./private.keyhinzu. Beachten Sie, dass Sie möglicherweise den jeweiligen Pfad Ihren Dateien anpassen müssen. Postman: Navigieren Sie zu "Preferences > Certificates > Add Certificate" und setzen Sie die Host-URL (in unseren Beispielen wäre dies ). Beachten Sie, dass sowohl"https://" als auch der Port 443 bereits eingestellt sind. Fügen Sie dann Ihre .pem und .key hinzu. Die Passwortangabe muss leer bleiben.

## Warum funktioniert mein QWAC Zertifikat nicht?
Es ist möglich, dass eine Anfrage trotz eingebundenen Zertifikates nicht funktioniert. Dies ist der Fall, wenn das Zertifikat abgelaufen ist. Jedes Zertifikat hat das Attribut „validUntil“, darin wird das Auslaufdatum gespeichert. In diesem Fall sollten Sie folgenden Fehler erhalten: Lösung: Dann ist es notwendig, dass der PSU eine neue Einwilligung erstellt und Sie nutzen die neue consentId.

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

Eine weitere Möglichkeit ist, dass Ihr Zertifikat nicht die benötigten Rollen abdeckt für diese Anfrage. Wenn Sie z.B. nur die Rolle "PIS" haben, können Sie keine Einwilligungen erstellen. In diesem Fall sollten Sie folgenden Fehler erhalten: Lösung: Sie können ein neues Zertifikat erstellen, das die Rolle AIS beinhaltet, und es an Ihre Anfrage hängen oder Sie entschlüsseln Ihr Zertifikat und überprüfen ob das qcStatement „PSP_AI“ beinhaltet.

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
