export class ObaUtils {

    public static getQueryParams(encryptedConsentId: string, authorisationId: string) {
        return { queryParams: {
                encryptedConsentId: ObaUtils.decodeParam(encryptedConsentId),
                authorisationId: ObaUtils.decodeParam(authorisationId)}
        };
    }

    public static decodeParam(param): string {
        if (param) {
            return decodeURIComponent(param);
        }
        return param;
    }

}