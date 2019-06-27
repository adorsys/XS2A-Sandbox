import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountDetailComponent } from './account-detail.component';
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientModule} from "@angular/common/http";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {AccountService} from "../../services/account.service";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {Observable} from "rxjs";
import {Account, AccountStatus, AccountType, UsageType} from "../../models/account.model";
import {AccountComponent} from "../account/account.component";
import {InfoService} from "../../commons/info/info.service";
import {InfoModule} from "../../commons/info/info.module";

describe('AccountDetailComponent', () => {
    let component: AccountDetailComponent;
    let fixture: ComponentFixture<AccountDetailComponent>;
    let accountService: AccountService;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                RouterTestingModule.withRoutes([{path: 'accounts', component: AccountComponent}]),
                ReactiveFormsModule,
                HttpClientTestingModule,
                InfoModule,
                FormsModule,
                HttpClientModule,
            ],
            declarations: [ AccountDetailComponent, AccountComponent ],
            providers: [AccountService, InfoService]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(AccountDetailComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        accountService = TestBed.get(AccountService);
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('create account when form submitted', () => {
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

        // submit invalid form
        expect(component.accountForm.valid).toBeFalsy();
        component.onSubmit();

        // submit valid form
        component.accountForm.controls['iban'].setValue("DE33737373773737377");
        expect(component.accountForm.valid).toBeTruthy();

        // mock http call
        let  createAccountSpy = spyOn(accountService, 'createAccount').and.returnValue(Observable.of(mockAccount));
        component.onSubmit();

        expect(component.submitted).toBeTruthy();
        expect(component.errorMessage).toEqual(null);
    }) ;
});
