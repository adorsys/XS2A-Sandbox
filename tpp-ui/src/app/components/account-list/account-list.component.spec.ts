import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountListComponent} from './account-list.component';
import {RouterTestingModule} from "@angular/router/testing";
import {AccountService} from "../../services/account.service";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {Account, AccountStatus, AccountType, UsageType} from "../../models/account.model";
import {Observable} from "rxjs";
import "rxjs-compat/add/observable/from";

describe('AccountListComponent', () => {
    let component: AccountListComponent;
    let fixture: ComponentFixture<AccountListComponent>;
    let accountService: AccountService;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                RouterTestingModule,
                HttpClientTestingModule,
            ],
            declarations: [AccountListComponent],
            providers: [AccountService]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(AccountListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        accountService = TestBed.get(AccountService);
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should load accounts on NgOnInit', () => {
        let mockAccounts: Account[] = [
            {
                id: 'XXXXXX',
                iban: 'DE35653635635663',
                bban: 'BBBAN',
                pan: 'pan',
                maskedPan: 'maskedPan',
                currency: 'EUR',
                msisdn: 'MSISDN',
                name: 'Pupkin',
                product: 'Deposit',
                accountType: AccountType.CASH,
                accountStatus: AccountStatus.ENABLED,
                bic: 'BIChgdgd',
                usageType: UsageType.PRIV,
                details: '',
                linkedAccounts: '',
                balances: []
            } as Account
        ];
        let getAccountsSpy = spyOn(accountService, 'getAccounts').and.returnValue(Observable.of(mockAccounts));

        component.ngOnInit();

        expect(getAccountsSpy).toHaveBeenCalled();
        expect(component.accounts).toEqual(mockAccounts);
    });
});
