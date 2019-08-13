import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountComponent} from './account.component';
import {RouterTestingModule} from "@angular/router/testing";
import {AccountService} from "../../services/account.service";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {Account, AccountStatus, AccountType, UsageType} from "../../models/account.model";
import {Observable} from "rxjs";
import "rxjs-compat/add/observable/of";
import {InfoService} from "../../commons/info/info.service";
import {InfoModule} from "../../commons/info/info.module";
import {IconModule} from "../../commons/icon/icon.module";

describe('AccountComponent', () => {
    let component: AccountComponent;
    let fixture: ComponentFixture<AccountComponent>;
    let accountService: AccountService;
    let infoService: InfoService;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                RouterTestingModule,
                HttpClientTestingModule,
                InfoModule,
                IconModule
            ],
            declarations: [AccountComponent],
            providers: [AccountService, InfoService]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(AccountComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        infoService = TestBed.get(InfoService);
        accountService = TestBed.get(AccountService);
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });


    it('should call getAccount on ngOnInit', () => {
        let getAccountSpy = spyOn(accountService, 'getAccount').and.callThrough();

        component.ngOnInit();

        expect(getAccountSpy).toHaveBeenCalled();
    });

    it('should assign account after server call', () => {
        let mockAccount: Account = {
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
        } as Account;

        spyOn(accountService, 'getAccount').and.returnValue(Observable.of(mockAccount));
        component.getAccount();
        expect(component.account).not.toBeUndefined();
    })


});
