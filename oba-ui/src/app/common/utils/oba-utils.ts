export class ObaUtils {

    public static getQueryParams(encryptedConsentId: string, authorisationId: string, redirectId?: string) {
        return { queryParams: {
                encryptedConsentId: ObaUtils.decodeParam(encryptedConsentId),
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
