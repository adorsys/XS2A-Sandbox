export class ObaUtils {

    public static getQueryParams(
      operation?: string,
      encryptedConsentId?: string,
      paymentId?: string,
      authorisationId?: string,
      redirectId?: string
    ) {

        return { queryParams: {
                operation: ObaUtils.decodeParam(encryptedConsentId),
                encryptedConsentId: ObaUtils.decodeParam(encryptedConsentId),
                paymentId: ObaUtils.decodeParam(paymentId),
                authorisationId: ObaUtils.decodeParam(authorisationId),
                redirectId: ObaUtils.decodeParam(redirectId)}
        };
    }

    public static decodeParam(param): string {
        if (param) {
            return decodeURIComponent(param);
        }
        return param;
    }
}
