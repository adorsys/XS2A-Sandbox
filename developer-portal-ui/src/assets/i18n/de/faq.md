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

<div class="divider">
</div>

# Allgemeine Fragen

## Warum passiert nichts, wenn ich execute betätige?

Wenn Sie versuchen Ihre Anfrage auszuführen und keinen Response-Code erhalten, dann ist es sehr wahrscheinlich, dass ein Header-Feld mit einem ungültigen Wert aufgefüllt ist. Bspw.: Ein Leerzeichen vor der X-Request-ID.
Lösung: Überprüfen Sie, ob ein Header-Feld Ihrer Anfrage rot markiert ist und korrigieren Sie dieses.

## Was soll ich tun, wenn ich weiterhin Containerkonflikte habe?

Es kommt vor, dass der Befehl _docker-compose rm -s -f -v_ nicht ausreicht, um alle aktiven Container zu löschen. Dies kann das Bauen der Services stören. In diesen Fall sollten Sie einen ähnlichen Fehler wie diesen ausgegeben bekommen:

```
Creating xs2a-sandbox_certificate-generator_1 ... error
" is already in use by container "ff45d8d0cc3e4745d4b7e2122c750efd11322f6b62a2610cde48923262793444". You have to remove (or rename) that container to be able to reuse that name.

ERROR: for xs2a-sandbox_certificate-generator_1  Cannot start service certificate-generator: driver failed programming external connectiCreating xs2a-sandbox_fakesmtp_1              ... done
.0:8092 failed: port is already allocated

```

oder diesen:

```
ERROR: for xs2a-consent-management  Cannot create container for service xs2a-consent-management: Conflict. The container name "/xs2a-conCreating ledgers                              ... error
or rename) that container to be able to reuse that name.
```

Lösung: Zuerst, solten Sie versuchen alle Docker Container zu löschen. Führen Sie dazu folgende Befehle nacheinander aus

**Warnung**: Dies löscht auch die von Ihnen erstellten lokalen Daten wie bereits registrierte TPPs:

1._docker rm -vf \$(docker ps -a -q)_

2._docker rmi -f \$(docker images -a -q)_

3._docker volume rm \$(docker volume ls -q)_

4._docker system prune -a_ - input _y_ to confirm

Falls Sie immer noch Probleme haben sollten, müssen Sie mit dem Befehl _kill \$(lsof -t -i :PORT_NUMBER)_ den Problem verursachenden Port beenden. Anstelle von PORT_NUMBER geben Sie die Nummer des Ports an, der den Konflikt verursacht, bspw. 8092 für den "Certificate Generator" oder 38080 für das "Consent Management System". Die Portnummer sollte in der Fehlermeldung stehen, falls nur der Name angegeben sein sollte, können Sie die entsprechende Nummer in unserer ['Links to environments' Tabelle](https://demo-dynamicsandbox-developerportalui.cloud.adorsys.de/getting-started) einsehen.
Im schlimmsten Fall ist ein Neustart Ihres Computers notwendig.

**Warnung:** Dies löscht all Ihre lokalen TPPs, User, Konten und Transaktionen dauerhaft.

## Warum antwortet die PUT Anfrage, dass der Service nicht erreichbar ist (Error 403)?

Dieser Fehler kommt vor, wenn Sie versuchen bei einem Test den embedded SCA-Ansatz zu verwenden, aber laut Ihrer Anbgaben, den redirect SCA-Ansatz ausgewählt haben. Hier ist entscheidend, ob Sie zuvor _TPP-Redirect-Preferred_ auf _true_ oder _false_ gesetzt haben. In diesen Fall, sollten Sie bei der ersten Auführung der PUT Anfrage - der Passworteingabe - Error 403 erhalten:

```
 {
  "tppMessages": [
    {
      "category": "ERROR",
      "code": "SERVICE_BLOCKED",
      "text": "This service is not reachable for the addressed PSU due to a channel independent blocking by the ASPSP. Additional information might be given by the ASPSP"
    }
  ]
}
```

Lösung: Falls Sie beispielsweise bei der POST Anfrage _TPP-Redirect-Preferred_ auf _true_ gesetzt haben, bedeutet das, dass Sie diesen Test nur mit dem erhaltenen scaRedirect link beenden können. Sie müssen den Test neustarten und _TPP-Redirect-Preferred_ entsprechend setzen.

## Wie lange ist meine TAN gültig?

Die erhaltene TAN ist solange gültig wie Ihre Session andauert. Sobald Sie automatisch ausgeloggt werden, verliert Ihre TAN die Gültigkeit und Sie müssen die Anfrage komplett wiederholen.

## Warum kann ich den Cancellation Authorisation Prozess nicht beenden?

Wenn Sie eine Zahlung erstellt und autorisiert haben und bei dem Versuch sie abzubrechen Error 400 erhalten:

```
{
  "tppMessages": [
    {
      "category": "ERROR",
      "code": "FORMAT_ERROR",
      "text": "Couldn't execute authorisation payment cancellation"
    }
  ]
}

```

Lösung: Es ist sehr wahrscheinlich, dass die Zahlung bereits ausgeführt wurde. Dies ist der Fall, wenn (1) Sie als _payment-product_ _instant-sepa-credit-transfers_ ausgewählt haben oder (2) wenn der Server Ihre Zahlung bereits gebucht hat - der Server finalisiert Zahlungen in regelmäßigen Zeitintervallen. Das bedeutet, dass die Zahlung bereits überwiesen wurde und damit nicht mehr abgebrochen werden kann. Dies können Sie überprüfen indem Sie in der OBA-UI die Liste der Transaktionen nach der Zahlung suchen oder den Status der Zahlung durch die GET Anfrage /v1/{payment-service}/{payment-product}/{paymentId} einsehen.

## Warum sehe ich meine Developer Portal Anpassungen nicht?

Wenn Sie das Developer Portal lokal angepasst haben, beispielsweise durch hinzufügen eines neuen Logos, können Sie die Services nicht einfach neu ausführen. Sie müssen sie neu bauen, am besten führen Sie den Befehl _make all_ aus. Sollte das nicht funktioniert haben, überprüfen Sie, ob Sie die benötigte JSON im angegebenen "custom" Ordner gespeichert haben und ob diese die korrekten Bildnamen verwendet.
