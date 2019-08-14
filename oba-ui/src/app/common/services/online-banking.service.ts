import {Injectable} from "@angular/core";
import {OnlineBankingAccountInformationService} from "../../api/services/online-banking-account-information.service";
import {OnlineBankingAuthorizationService} from "../../api/services/online-banking-authorization.service";
import {OnlineBankingConsentsService} from "../../api/services/online-banking-consents.service";
import {Observable} from "rxjs";
import {AccountDetailsTO} from "../../api/models/account-details-to";
import {TransactionTO} from "../../api/models/transaction-to";
import {AisAccountConsent} from "../../api/models/ais-account-consent";
import {AuthService} from "./auth.service";
import {map} from "rxjs/operators";


@Injectable({
    providedIn: 'root'
})
export class OnlineBankingService {

    constructor(private authService: AuthService,
                private onlineBankingAccountInfoService: OnlineBankingAccountInformationService,
                private onlineBankingAuthorizationService: OnlineBankingAuthorizationService,
                private onlineBankingConsentService: OnlineBankingConsentsService) {
    }

    /*****
     *
     * Online banking account infos
     * **/
    public getAccounts(userLogin?: string): Observable<AccountDetailsTO[]> {
        const login = userLogin ? userLogin : this.authService.getAuthorizedUser();
        return this.onlineBankingAccountInfoService.accountsUsingGET(login);
    }

    public getAccount(accountID: string, userLogin?: string): Observable<AccountDetailsTO> {
        const login = userLogin ? userLogin : this.authService.getAuthorizedUser();
        return this.onlineBankingAccountInfoService.accountsUsingGET(login).pipe(
            map(accounts => accounts.find(account => account.id === accountID))
        );
    }

    public getTransactions(params: OnlineBankingAccountInformationService.TransactionsUsingGETParams): Observable<TransactionTO[]> {
        return this.onlineBankingAccountInfoService.transactionsUsingGET(params);
    }

    /***
     * Online banking consents
     * */
    public getConsents(userLogin?: string): Observable<AisAccountConsent[]> {
        const login = userLogin ? userLogin : this.authService.getAuthorizedUser();
        return this.onlineBankingConsentService.consentsUsingGET(login);
    }

}